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
)


const MaxRTreeNodes = 50
const MinFillRatio = 0.35
const DefaultMapSize = 500

type Consumer struct {
    ID           string
    MessageQueue queue.Queue
    Area         rtree.Rect
}

func NewConsumer() *Consumer {
    consumer := new(Consumer)
    consumer.MessageQueue = queue.NewListQueue()
    return consumer
}

type Broker struct {
    ConsumerTree *rtree.RTree
    ConsumerMap  map[string]*Consumer
}

func newBroker() *Broker {
    minFill := MaxRTreeNodes * MinFillRatio
    return &Broker{
        ConsumerTree: rtree.New(MaxRTreeNodes, int(minFill)),
        ConsumerMap:  make(map[string]*Consumer, DefaultMapSize),
    }
}

type RegisterConsumerMessage struct {
    Longitude float32 `json:"longitude"`
    Latitude  float32 `json:"latitude"`
    Radius    float32 `json:"radius"`
}

func (msg *RegisterConsumerMessage) Bounds() rtree.Rect {
    left := msg.Longitude - msg.Radius
    right := msg.Longitude + msg.Radius
    top := msg.Latitude - msg.Radius
    bottom := msg.Latitude + msg.Radius

    return rtree.Rect{
        Left: left,
        Top: top,
        Bottom: bottom,
        Right: right,
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

    consumer := NewConsumer()
    consumer.Area = msg.Bounds()
    consumer.ID = base64.URLEncoding.EncodeToString(consID[:])

    b.ConsumerMap[consumer.ID] = consumer
    b.ConsumerTree.Insert(consumer, consumer.Area)

    w.Write([]byte(fmt.Sprintf("{\"consumer_id\": \"%s\"}\n", consumer.ID)))
}

func (b *Broker) handleConsume(w http.ResponseWriter, r *http.Request, body []byte) {
    pathParts := strings.Split(r.URL.Path, "/")
    if len(pathParts) != 3 {
        http.Error(w, "Bad request for API", 400)
        return
    }

    consumerID := pathParts[2]
    consumer, ok := b.ConsumerMap[consumerID]
    if !ok {
        http.Error(w, "Consumer not registered", 400)
        return
    }

    msgs := consumer.MessageQueue.Poll()
    bytes, err := json.Marshal(msgs)
    if err != nil {
        http.Error(w, "Error marshaling messages", 500)
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
    Location *Location                `json:"location"`
    Data     []map[string]interface{} `json:"data"`
}

type QueuedMessage struct {
    Location *Location              `json:"location"`
    Data     map[string]interface{} `json:"data"`
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
    broker := newBroker()
    http.Handle("/", broker)
    http.ListenAndServe(":80", nil)
}
