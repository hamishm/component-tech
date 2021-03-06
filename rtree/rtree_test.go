package rtree_test

import (
    "fmt"
    "component-tech/rtree"
    "math/rand"
    "testing"
)


const testCount = 200

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


type Dummy struct {
    A int
}


func TestRTree(t *testing.T) {
    tree := rtree.New(10, 4)

    nodes := make([]*rtree.RTreeNode, 0, testCount)
    for n:= 0; n < testCount; n++ {
        bounds := randomRect()
        fmt.Println(bounds)
        dummy := &Dummy{A: 1}
        node := tree.Insert(dummy, bounds)
        nodes = append(nodes, node)
    }

    count := 0
    tree.VisitAll(func (value interface{}, bounds rtree.Rect) {
        count++
        //fmt.Println(value.(*Dummy).A)
    })

    if count != testCount {
        t.Error("Failed to visit all nodes")
    }

    for _, n := range nodes {
        n.Remove()
    }

    count = 0
    tree.VisitAll(func (value interface{}, bounds rtree.Rect) {
        count++
    })

    if count != 0 {
        t.Error("Failed to remove all nodes")
    }

    for n:= 0; n < testCount; n++ {
        bounds := randomRect()
        fmt.Println(bounds)
        dummy := &Dummy{A: 1}
        nodes[n] = tree.Insert(dummy, bounds)
    }

    count = 0
    tree.VisitAll(func (value interface{}, bounds rtree.Rect) {
        count++
        //fmt.Println(value.(*Dummy).A)
    })

    if count != testCount {
        t.Error("Failed to visit all nodes")
    }

}
