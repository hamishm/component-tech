package main

import (
    "component-tech/rtree"
    "component-tech/strict_json"
    "crypto/rand"
    "encoding/base64"
    "fmt"
    "io/ioutil"
    "net/http"
    "strings"
    "sync"
    "time"
)


func assert(expr bool, msg string) {
    if !expr {
        panic(msg)
    }
}


const MaxRTreeNodes = 50
const MinFillRatio = 0.35
const DefaultMapSize = 500
const CoronerTimeout = time.Duration(1) * time.Minute


type Consumer struct {
    ID           string
    Area         rtree.Rect
    Coroner      *time.Timer
    TreeNode     *rtree.RTreeNode
    Broker       *Broker
}

func NewConsumer() *Consumer {
    consumer := new(Consumer)
    return consumer
}

type Broker struct {
    URL      string
    TreeNode *rtree.RTreeNode
    Coroner  *time.Timer
    Active   bool
}

func NewBroker() *Broker {
    broker := new(Broker)
    broker.URL = ""
    broker.Active = false
    return broker
}

type Registry struct {
    ConsumerTree *rtree.RTree
    BrokerMap    map[string]*Broker
    ConsumerMap  map[string]*Consumer
    RWLock       sync.RWMutex
}

func NewRegistry() *Registry {
    minFill := MaxRTreeNodes * MinFillRatio
    return &Registry{
        ConsumerTree: rtree.New(MaxRTreeNodes, int(minFill)),
        ConsumerMap:  make(map[string]*Consumer, DefaultMapSize),
        BrokerMap:    make(map[string]*Broker, DefaultMapSize),
    }
}

func (r *Registry) RemoveConsumer(consumer *Consumer) {
    consumer.Coroner.Stop()

    if _, ok := r.ConsumerMap[consumer.ID]; !ok {
        // We only remove consumers under the broker wlock,
        // so this should never happen.
        panic("consumer has already been removed!")
    }

    delete(r.ConsumerMap, consumer.ID)
    consumer.TreeNode.Remove()
}

func (r *Registry) SpinUpBroker(node *rtree.RTreeNode) *Broker {
    for _, broker := range r.BrokerMap {
        if !broker.Active {
            // Let's spin it up
            broker.Active = true
            node.Value = broker
            return broker
        }
    }

    return nil
}

func (r *Registry) AddConsumer(consumer *Consumer) error {
    if consumer.Broker != nil {
        // This is a pre-existing consumer
        assert(consumer.TreeNode != nil, "active consumer lacks tree node")
        consumer.TreeNode.Remove()

        // Consumer node removal can cause deletion of broker
        if consumer.Broker.TreeNode.Parent == nil {
            consumer.Broker.Active = false
            consumer.Broker.TreeNode = nil
        }
    }

    node := r.ConsumerTree.Insert(consumer, consumer.Area)
    r.ConsumerMap[consumer.ID] = consumer

    if node.Parent.Value == nil {
        // We need to spin up a new broker to handle this new rtree split
        broker := r.SpinUpBroker(node.Parent)
        if broker == nil {
            // We're all out of brokers
            r.RemoveConsumer(consumer)
            return fmt.Errorf("No space!")
        }
        consumer.Broker = broker
    } else {
        consumer.Broker = node.Parent.Value.(*Broker)
    }

    return nil
}

type AnnounceConsumerMessage struct {
    Longitude float32 `json:"longitude"`
    Latitude  float32 `json:"latitude"`
    Radius    float32 `json:"radius"`
}

func (msg *AnnounceConsumerMessage) Bounds() rtree.Rect {
    return rtree.Rect{
        Left: msg.Longitude - msg.Radius,
        Top: msg.Latitude - msg.Radius,
        Bottom: msg.Latitude + msg.Radius,
        Right: msg.Longitude + msg.Radius,
    }
}

func (r *Registry) announceConsumer(w http.ResponseWriter, req *http.Request, body []byte) {
    registerConsumer := false
    consumerID := ""
    var consumer *Consumer

    var msg AnnounceConsumerMessage
    err := strict_json.Unmarshal(body, &msg)
    if err != nil {
        http.Error(w, err.Error(), 400)
        return
    }

    // Take the write lock - we will always be modifying the rtree
    r.RWLock.Lock()
    defer r.RWLock.Unlock()

    pathParts := strings.Split(req.URL.Path, "/")
    switch len(pathParts) {
    case 3:
        registerConsumer = true
    case 4:
        consumerID = pathParts[3]
        var ok bool
        consumer, ok = r.ConsumerMap[consumerID]
        if !ok {
            registerConsumer = true
        }
    }


    if registerConsumer {
        var consID [16]byte
        _, err = rand.Read(consID[:])
        if err != nil {
            http.Error(w, err.Error(), 500)
            return
        }
        consumer = NewConsumer()
        consumer.Area = msg.Bounds()
        consumer.ID = base64.URLEncoding.EncodeToString(consID[:])

        consumer.Coroner = time.AfterFunc(CoronerTimeout, func() {
            r.RemoveConsumer(consumer)
        })
    }

    err = r.AddConsumer(consumer)
    if err != nil {
        http.Error(w, err.Error(), 500)
        return
    }

    w.Write([]byte(fmt.Sprintf(`{"consumer_id": "%s", "broker_url": "%s"}`, consumer.ID, consumer.Broker.URL)))
}

type AnnounceBrokerMessage struct {
    URL string `json:"url"`
}

func (r *Registry) announceBroker(w http.ResponseWriter, req *http.Request, body []byte) {
    var msg AnnounceBrokerMessage
    err := strict_json.Unmarshal(body, &msg)
    if err != nil {
        http.Error(w, err.Error(), 400)
        return
    }

    r.RWLock.RLock()
    broker, ok := r.BrokerMap[msg.URL]

    if ok {
        r.RWLock.RUnlock()
        w.Write([]byte("OK"))
        return
    }

    // Need to add the broker
    r.RWLock.RUnlock()
    r.RWLock.Lock()
    defer r.RWLock.Unlock()

    // Recheck condition in case it changed after we dropped the lock
    broker, ok = r.BrokerMap[msg.URL]

    if ok {
        w.Write([]byte("OK"))
        return
    }

    broker = NewBroker()
    broker.URL = msg.URL
    broker.Active = false
    r.BrokerMap[msg.URL] = broker

    w.Write([]byte("OK"))
}

type AnnounceProducerMessage struct {
    Longitude float32 `json:"longitude"`
    Latitude  float32 `json:"latitude"`
}

func (r *Registry) announceProducer(w http.ResponseWriter, req *http.Request, body []byte) {
    var msg AnnounceProducerMessage
    err := strict_json.Unmarshal(body, &msg)
    if err != nil {
        http.Error(w, err.Error(), 400)
        return
    }

    r.RWLock.RLock()
    defer r.RWLock.RUnlock()

    pointsMap := make(map[string]int, 20)
    r.ConsumerTree.Visit(msg.Longitude, msg.Latitude, func(value interface{}, bounds rtree.Rect) {
        consumer := value.(*Consumer)
        broker := consumer.Broker
        points, ok := pointsMap[broker.URL]
        if !ok {
            pointsMap[broker.URL] = 1
        } else {
            pointsMap[broker.URL] = points + 1
        }
    })

    var bestURL string
    bestPoints := -1
    for k, v := range pointsMap {
        if v > bestPoints {
            bestURL = k
        }
    }

    w.Write([]byte(fmt.Sprintf(`{"broker_url": "%s"}`, bestURL)))
}

func (r *Registry) ServeHTTP(w http.ResponseWriter, req *http.Request) {
    bodyBytes, err := ioutil.ReadAll(req.Body)
    if err != nil {
        http.Error(w, "Error reading request body", 500)
        return
    }

    switch {
    case req.Method == "POST" && strings.HasPrefix(req.URL.Path, "/announce/broker"):
        r.announceBroker(w, req, bodyBytes)
    case req.Method == "POST" && strings.HasPrefix(req.URL.Path, "/announce/producer"):
        r.announceProducer(w, req, bodyBytes)
    case req.Method == "POST" && strings.HasPrefix(req.URL.Path, "/announce/consumer"):
        r.announceConsumer(w, req, bodyBytes)
    default:
        http.Error(w, "Endpoint not found", 404)
    }
}

func main() {
    registry := NewRegistry()
    http.Handle("/", registry)
    http.ListenAndServe(":80", nil)
}
