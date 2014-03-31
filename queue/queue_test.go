package queue_test

import (
    "component-tech/queue"
    "testing"
    "sync"
)


const maxNum = 500000

func TestQueue(t *testing.T) {
    queue := queue.NewListQueue()

    var wg sync.WaitGroup
    wg.Add(2)

    consume := func() {
        consumeNum := 0
        Outer:
        for {
            items := queue.Poll()
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
