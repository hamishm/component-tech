package main

import (
    "fmt"
    "math/rand"
    "net/http"
    "component-tech/rtree"
)


type Struct struct {
    Greeting    string
    Punctuation string
    Who         string
}


func (s Struct) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "%s %s %s", s.Greeting, s.Who, s.Punctuation)
}


func randFloat(lower, upper float32) float32 {
    return lower + rand.Float32() * (upper - lower)
}

func randomRect() rtree.Rect {
    left := randFloat(0.0, 150.0)
    right := randFloat(left + 5.0, 200.0)
    top := randFloat(0.0, 150.0)
    bottom := randFloat(top + 5.0, 200.0)
    return rtree.Rect{
        Left:   left,
        Right:  right,
        Top:    top,
        Bottom: bottom,
    }
}

type BS struct {
    a int
}

func main() {
    //http.HandleFunc("/produce", produce)
    //http.HandleFunc("/consume", consume)
    //http.ListenAndServe(":80", nil)

    tree := rtree.NewRTree(10, 4)
    for n:= 0; n < 200; n++ {
        fmt.Println("Inserting a node")
        bounds := randomRect()
        bs := &BS{a: 1}
        tree.Insert(bs, bounds)
    }

    count := 0
    tree.VisitAll(func (value interface{}, bounds rtree.Rect) {
        count++
        fmt.Println(value.(*BS).a)
    })
    fmt.Println(count)
}
