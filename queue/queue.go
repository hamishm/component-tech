package queue

import (
    "container/list"
    "sync"
    "time"
)

type Queue interface {
    Poll(timeout time.Duration) []interface{}
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

func (q *ListQueue) Poll(timeout time.Duration) []interface{} {
    // Timeout ignored by list queue
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


type ChannelQueue struct {
    c chan interface{}
}

func NewChannelQueue(capacity int) *ChannelQueue {
    q := new(ChannelQueue)
    q.c = make(chan interface{}, capacity)
    return q
}

func (q *ChannelQueue) Poll(timeout time.Duration) []interface{} {
    items := make([]interface{}, 0, 20)

    t := time.NewTimer(timeout)

    select {
    case item := <- q.c:
        items = append(items, item)
    case <- t.C:
        return nil
    }

    outer:
    for {
        select {
        case item := <- q.c:
            items = append(items, item)
        default:
            break outer
        }
    }

    t.Stop()
    return items
}


func (q *ChannelQueue) Put(value interface{}) {
    outer:
    for {
        select {
        case q.c <- value:
            break outer
        default:
            select {
                case <-q.c:
                default:
            }
        }
    }
}


func (q *ChannelQueue) PutMany(values []interface{}) {
    for _, val := range values {
        q.Put(val)
    }
}
