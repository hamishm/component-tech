package queue_test

import (
    "component-tech/queue"
    "sync"
    "testing"
    "time"
)


const maxNum = 500000

func TestListQueue(t *testing.T) {
    queue := queue.NewListQueue()
    testQueue(queue, t)
}

func TestChannelQueue(t *testing.T) {
    queue := queue.NewChannelQueue(maxNum)
    testQueue(queue, t)
}

func testQueue(queue queue.Queue, t *testing.T) {
    var wg sync.WaitGroup
    wg.Add(2)

    consume := func() {
        consumeNum := 0
        Outer:
        for {
            items := queue.Poll(time.Duration(9999) * time.Hour)
            for _, item := range(items) {
                num := item.(int)
                if num != consumeNum {
                    t.Fatal("polled wrong number from queue")
                }
                if num == maxNum {
                    break Outer
                }
                consumeNum++
            }
        }
        wg.Done()
    }

    produce := func() {
        for i := 0; i < 150; i++ {
            queue.Put(i)
        }
        for i := 150; i < 350; i += 10 {
            var nums [10]interface{}
            for j := 0; j < 10; j++ {
                nums[j] = i + j
            }
            queue.PutMany(nums[:])
        }
        for i := 350; i < maxNum + 1; i++ {
            queue.Put(i)
        }
        wg.Done()
    }

    go consume()
    go produce()

    wg.Wait()
}

func TestChannelQueueTimeout(t *testing.T) {
    q := queue.NewChannelQueue(50)
    q.Put("abc")
    q.Put("def")
    vals := q.Poll(time.Second * time.Duration(5))
    if vals[0].(string) != "abc" || vals[1].(string) != "def" {
        t.Error("got wrong values")
    }

    var wg sync.WaitGroup
    wg.Add(1)

    wait := func() {
        vals := q.Poll(time.Second * time.Duration(1))
        if vals != nil {
            t.Error("poll on empty returned values!")
        }
        wg.Done()
    }

    go wait()
    wg.Wait()

    q.Put("abc")
    q.Put("def")
    vals = q.Poll(time.Millisecond * time.Duration(1))
    if vals[0].(string) != "abc" || vals[1].(string) != "def" {
        t.Error("got wrong values")
    }
}
