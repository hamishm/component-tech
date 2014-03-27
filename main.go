package main

import (
    "fmt"
    "net/http"
    "rtree"
)


type Coords struct {
    Lat  float
    Long float
}



type Struct struct {
    Greeting    string
    Punctuation string
    Who         string
}


func (s String) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "%s", s)
}

func (s Struct) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "%s %s %s", s.Greeting, s.Who, s.Punctuation)
}


func main() {
    http.HandleFunc("/produce", produce)
    http.HandleFunc("/consume", consume)
    http.ListenAndServe(":80", nil)
}
