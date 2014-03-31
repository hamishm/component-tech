package rtree

import (
    "math/rand"
    "testing"
)

func randFloat(lower, upper float32) float32 {
    return lower + rand.Float32() * (upper - lower)
}

func randomRect() Rect {
    left := randFloat(0.0, 150.0)
    right := randFloat(left + 5.0, 200.0)
    top := randFloat(0.0, 150.0)
    bottom := randFloat(top + 5.0, 200.0)
    return Rect{
        Left:   left,
        Right:  right,
        Top:    top,
        Bottom: bottom,
    }
}


type Dummy struct {
    A int
}


func TestRTree(t *testing.T) {
    tree := New(10, 4)
    for n:= 0; n < 200; n++ {
        bounds := randomRect()
        dummy := &Dummy{A: 1}
        tree.Insert(dummy, bounds)
    }

    count := 0
    tree.VisitAll(func (value interface{}, bounds Rect) {
        count++
        //fmt.Println(value.(*Dummy).A)
    })

    if count != 200 {
        t.Error("Failed to visit all nodes")
    }
}
