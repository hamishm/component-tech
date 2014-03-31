package queue

import (
    "container/list"
    "sync"
)

type Queue interface {
    Poll() []interface{}
    Put(value interface{})
    PutMany(value []interface{})
}

type ListQueue struct {
    Items   list.List
    Lock    sync.Mutex
    CondVar *sync.Cond
}


func NewListQueue() *ListQueue {
    listQ := new(ListQueue)
    listQ.Items.Init()
    // Lock default initialized unlocked
    listQ.CondVar = sync.NewCond(&listQ.Lock)
    return listQ
}

func (q *ListQueue) Poll() []interface{} {
    q.Lock.Lock()
    defer q.Lock.Unlock()

    for q.Items.Len() == 0 {
        q.CondVar.Wait()
    }
    ret := make([]interface{}, q.Items.Len())

    i := 0
    for e := q.Items.Front(); e != nil; {
        ret[i] = e.Value
        prev := e
        e = prev.Next()
        q.Items.Remove(prev)
        i++
    }
    return ret
}

func (q *ListQueue) Put(value interface{}) {
    q.Lock.Lock()
    defer q.Lock.Unlock()

    q.Items.PushBack(value)
    q.CondVar.Signal()
}

func (q *ListQueue) PutMany(values []interface{}) {
    q.Lock.Lock()
    defer q.Lock.Unlock()

    for _, v := range(values) {
        q.Items.PushBack(v)
    }
    q.CondVar.Signal()
}
