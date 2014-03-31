package main

import (
    "component-tech/queue"
    "component-tech/rtree"
    "crypto/rand"
    "encoding/json"
    "fmt"
    "io/ioutil"
    "net/http"
    "strings"
)


const MaxRTreeNodes = 50


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
    consumerTree *rtree.RTree
    consumerMap  map[string]*Consumer
}

type RegisterConsumerMessage struct {
    Longitude float32 `json:"longitude"`
    Latitude  float32 `json:"latitude"`
    Radius    float32 `json:"radius"`
}

func (b *Broker) handleRegConsumer(w http.ResponseWriter, r *http.Request, body []byte) {
    var msg RegisterConsumerMessage
    err := json.Unmarshal(body, &msg)
    if err != nil {
        http.Error(w, err.Error(), 400)
        return
    }

    left := msg.Longitude - msg.Radius
    right := msg.Longitude + msg.Radius
    top := msg.Latitude - msg.Radius
    bottom := msg.Latitude + msg.Radius

    var consID [16]byte
    _, err = rand.Read(consID[:])
    if err != nil {
        http.Error(w, err.Error(), 500)
        return
    }

    consumer := NewConsumer()
    consumer.Area = rtree.Rect{
        Left: left,
        Top: top,
        Bottom: bottom,
        Right: right,
    }
    consumer.ID = string(consID[:])

    // TODO: lock
    b.consumerMap[consumer.ID] = consumer
    b.consumerTree.Insert(consumer, consumer.Area)

    w.Write([]byte(fmt.Sprintf("{\"consumer_id\": \"%s\"}\n", consumer.ID)))
}

type SensorMessage struct {
    String string  `json:"some_string"`
    Value  float32 `json:"value"`
}

func (b *Broker) handleConsume(w http.ResponseWriter, r *http.Request, body string) {
    pathParts := strings.Split(r.URL.Path, "/")
    if len(pathParts) != 2 {
        http.Error(w, "Bad request for API", 400)
        return
    }
    consumerID := pathParts[1]

    consumer := b.consumerMap[consumerID]
    msgs := consumer.MessageQueue.Poll()
    w.Write([]byte("[\n  "))
    for _, msg := range(msgs) {
        sensorMsg := msg.(SensorMessage)
        bytes, err := json.Marshal(sensorMsg)
        if err != nil {
            http.Error(w, "Error marshaling message", 500)
            return
        }
        w.Write(bytes)
        w.Write([]byte(",\n  "))
    }
    w.Write([]byte("\n]\n"))
}

func (b *Broker) handleRegProducer(w http.ResponseWriter, r *http.Request, body string) {
    http.Error(w, "Not yet implemented", 500)
}

func (b *Broker) handleProduce(w http.ResponseWriter, r *http.Request, body string) {
    http.Error(w, "Not yet implemented", 500)
}

func (b *Broker) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    bodyBytes, err := ioutil.ReadAll(r.Body)
    if err != nil {
        http.Error(w, "Error reading request body", 500)
        return
    }

    body := string(bodyBytes)

    switch {
    case r.Method == "POST" && r.URL.Path == "/consume":
        b.handleRegConsumer(w, r, bodyBytes)
    case r.Method == "POST" && r.URL.Path == "/produce":
        b.handleRegProducer(w, r, body)
    case r.Method == "GET" && strings.HasPrefix(r.URL.Path, "/consume/"):
        b.handleConsume(w, r, body)
    case r.Method == "POST" && strings.HasPrefix(r.URL.Path, "/produce/"):
        b.handleProduce(w, r, body)
    default:
        http.Error(w, "Endpoint not found", 404)
    }
}


func newBroker() *Broker {
    minFill := MaxRTreeNodes * 0.35
    return &Broker{
        consumerTree: rtree.New(MaxRTreeNodes, int(minFill)),
    }
}


func main() {
    broker := newBroker()
    http.Handle("/", broker)
    http.ListenAndServe(":80", nil)
}
