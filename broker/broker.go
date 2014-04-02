package main

import (
    "component-tech/queue"
    "component-tech/rtree"
    "component-tech/strict_json"
    "crypto/rand"
    "encoding/base64"
    "encoding/json"
    "fmt"
    "io/ioutil"
    "net/http"
    "strings"
    "sync"
    "time"
)


const MaxRTreeNodes = 50
const MinFillRatio = 0.35
const DefaultMapSize = 500
const ConsumerQueueSize = 50
const CoronerTimeout = time.Duration(1) * time.Minute
const ConsumerKeepAliveInterval = time.Duration(20) * time.Second


type Consumer struct {
    ID           string
    MessageQueue queue.Queue
    Area         rtree.Rect

    Coroner      *time.Timer
    TreeNode     *rtree.RTreeNode
    InWait       bool
}

func NewConsumer() *Consumer {
    consumer := new(Consumer)
    consumer.MessageQueue = queue.NewChannelQueue(ConsumerQueueSize)
    consumer.InWait = false
    return consumer
}


type Broker struct {
    ConsumerTree *rtree.RTree
    ConsumerMap  map[string]*Consumer
    RWLock       sync.RWMutex
}

func NewBroker() *Broker {
    minFill := MaxRTreeNodes * MinFillRatio
    return &Broker{
        ConsumerTree: rtree.New(MaxRTreeNodes, int(minFill)),
        ConsumerMap:  make(map[string]*Consumer, DefaultMapSize),
    }
}

func (b *Broker) RemoveConsumer(consumer *Consumer) {
    b.RWLock.Lock()
    defer b.RWLock.Unlock()

    consumer.Coroner.Stop()

    if _, ok := b.ConsumerMap[consumer.ID]; !ok {
        // We only remove consumers under the broker wlock,
        // so this should never happen.
        panic("consumer has already been removed!")
    }

    delete(b.ConsumerMap, consumer.ID)
    consumer.TreeNode.Remove()
}

type RegisterConsumerMessage struct {
    Longitude float32 `json:"longitude"`
    Latitude  float32 `json:"latitude"`
    Radius    float32 `json:"radius"`
}

func (msg *RegisterConsumerMessage) Bounds() rtree.Rect {
    return rtree.Rect{
        Left: msg.Longitude - msg.Radius,
        Top: msg.Latitude - msg.Radius,
        Bottom: msg.Latitude + msg.Radius,
        Right: msg.Longitude + msg.Radius,
    }
}

func (b *Broker) handleRegConsumer(w http.ResponseWriter, r *http.Request, body []byte) {
    var msg RegisterConsumerMessage
    err := strict_json.Unmarshal(body, &msg)
    if err != nil {
        http.Error(w, err.Error(), 400)
        return
    }

    var consID [16]byte
    _, err = rand.Read(consID[:])
    if err != nil {
        http.Error(w, err.Error(), 500)
        return
    }

    b.RWLock.Lock()
    defer b.RWLock.Unlock()

    consumer := NewConsumer()
    consumer.Area = msg.Bounds()
    consumer.ID = base64.URLEncoding.EncodeToString(consID[:])
    consumer.TreeNode = b.ConsumerTree.Insert(consumer, consumer.Area)

    b.ConsumerMap[consumer.ID] = consumer

    consumer.Coroner = time.AfterFunc(CoronerTimeout, func() {
        if consumer.InWait {
            consumer.Coroner.Reset(CoronerTimeout)
        } else {
            b.RemoveConsumer(consumer)
        }
    })

    w.Write([]byte(fmt.Sprintf(`{"consumer_id": "%s"}`, consumer.ID)))
}


func (b *Broker) handleConsume(w http.ResponseWriter, r *http.Request, body []byte) {
    pathParts := strings.Split(r.URL.Path, "/")
    if len(pathParts) != 3 {
        http.Error(w, "Bad request for API", 400)
        return
    }

    consumerID := pathParts[2]

    b.RWLock.RLock()

    consumer, ok := b.ConsumerMap[consumerID]
    if !ok {
        http.Error(w, "Consumer not registered", 400)
        b.RWLock.RUnlock()
        return
    }

    consumer.Coroner.Reset(CoronerTimeout)
    consumer.InWait = true

    // Drop the lock while we poll the queue
    b.RWLock.RUnlock()

    closeNotify := w.(http.CloseNotifier).CloseNotify()

    var msgs []interface{}
    for {
        msgs = consumer.MessageQueue.Poll(ConsumerKeepAliveInterval)
        if msgs != nil {
            break
        } else {
            w.Write([]byte("\n"))
            switch {
            case <- closeNotify:
                consumer.InWait = false
                return
            default:
            }
        }
    }

    b.RWLock.RLock()
    defer b.RWLock.RUnlock()

    consumer.InWait = false
    consumer.Coroner.Reset(CoronerTimeout)

    bytes, err := json.Marshal(msgs)
    if err != nil {
        http.Error(w, "Error marshaling messages", 500)
        return
    }

    w.Write(bytes)
}


func (b *Broker) handleRegProducer(w http.ResponseWriter, r *http.Request, body []byte) {
    w.Write([]byte("Producer registration not used right now\n"))
}


type Location struct {
    Longitude float32 `json:"longitude"`
    Latitude  float32 `json:"latitude"`
}

type ProducerMessage struct {
    Location *Location     `json:"location"`
    Data     []interface{} `json:"data"`
}

type QueuedMessage struct {
    Location *Location   `json:"location"`
    Data     interface{} `json:"data"`
}

func (b *Broker) handleProduce(w http.ResponseWriter, r *http.Request, body []byte) {
    pathParts := strings.Split(r.URL.Path, "/")
    if len(pathParts) != 3 {
        http.Error(w, "Bad request for API", 400)
        return
    }
    //producerID := pathParts[1]

    var msg ProducerMessage
    err := strict_json.Unmarshal(body, &msg)
    if err != nil {
        http.Error(w, err.Error(), 400)
        return
    }

    longitude := msg.Location.Longitude
    latitude  := msg.Location.Latitude

    storedData := make([]interface{}, len(msg.Data))
    for i := 0; i < len(msg.Data); i++ {
        storedData[i] = &QueuedMessage{
            Location: msg.Location,
            Data:     msg.Data[i],
        }
    }

    b.RWLock.RLock()
    defer b.RWLock.RUnlock()

    b.ConsumerTree.Visit(longitude, latitude,func(value interface{}, bounds rtree.Rect) {
        consumer := value.(*Consumer)
        consumer.MessageQueue.PutMany(storedData)
    })

    w.Write([]byte("cheers"))
}


func (b *Broker) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    bodyBytes, err := ioutil.ReadAll(r.Body)
    if err != nil {
        http.Error(w, "Error reading request body", 500)
        return
    }

    switch {
    case r.Method == "POST" && r.URL.Path == "/consume":
        b.handleRegConsumer(w, r, bodyBytes)
    case r.Method == "POST" && r.URL.Path == "/produce":
        b.handleRegProducer(w, r, bodyBytes)
    case r.Method == "GET" && strings.HasPrefix(r.URL.Path, "/consume/"):
        b.handleConsume(w, r, bodyBytes)
    case r.Method == "POST" && strings.HasPrefix(r.URL.Path, "/produce/"):
        b.handleProduce(w, r, bodyBytes)
    default:
        http.Error(w, "Endpoint not found", 404)
    }
}

func main() {
    broker := NewBroker()
    http.Handle("/", broker)
    http.ListenAndServe(":80", nil)
}
