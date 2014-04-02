package rtree

import (
    "math"
    "sort"
)

func assert(expr bool, msg string) {
    if !expr {
        panic(msg)
    }
}

/* Support data types and helper methods */

type Rect struct {
    // Right = left + width
    // Bottom = top + height
    Left, Right, Top, Bottom float32
}

func Min(a, b float32) float32 {
    if a <= b {
        return a
    } else {
        return b
    }
}

func Max(a, b float32) float32 {
    if a >= b {
        return a
    } else {
        return b
    }
}

func (rect *Rect) Contains(x, y float32) bool {
    return y >= rect.Top  && y < rect.Bottom &&
           x >= rect.Left && x < rect.Right
}

func (rect *Rect) Perimeter() float32 {
    return 2 * ((rect.Bottom - rect.Top) + (rect.Right - rect.Left))
}

func (rect *Rect) Area() float32 {
    return (rect.Bottom - rect.Top) * (rect.Right - rect.Left)
}

func (rect *Rect) Union(other *Rect) Rect {
    return Rect{
        Left:   Min(rect.Left, other.Left),
        Right:  Max(rect.Right, other.Right),
        Top:    Min(rect.Top, other.Top),
        Bottom: Max(rect.Bottom, other.Bottom),
    }
}

func (rect *Rect) Intersection(other *Rect) Rect {
    if rect.Right <= other.Left || rect.Bottom <= other.Top ||
        rect.Left >= other.Right || rect.Top >= other.Bottom {
        return Rect{}
    }

    return Rect{
        Left:   Max(rect.Left, other.Left),
        Right:  Min(rect.Right, other.Right),
        Top:    Max(rect.Top, other.Top),
        Bottom: Min(rect.Bottom, other.Bottom),
    }
}

func (rect *Rect) IntersectionArea(other *Rect) float32 {
    if rect.Right <= other.Left || rect.Bottom <= other.Top ||
        rect.Left >= other.Right || rect.Top >= other.Bottom {
        return 0.0
    }

    left :=   Max(rect.Left, other.Left)
    right :=  Min(rect.Right, other.Right)
    top :=    Max(rect.Top, other.Top)
    bottom := Min(rect.Bottom, other.Bottom)
    return (right - left) * (bottom - top)
}

func MinBounds(nodes []*RTreeNode) Rect {
    rect := nodes[0].Bounds

    for _, node := range nodes[1:] {
        rect.Top = Min(rect.Top, node.Bounds.Top)
        rect.Left = Min(rect.Left, node.Bounds.Left)
        rect.Right = Max(rect.Right, node.Bounds.Right)
        rect.Bottom = Max(rect.Bottom, node.Bounds.Bottom)
    }

    return rect
}

/* Data types */

type RTreeNode struct {
    Bounds   Rect
    Value    interface{}
    Parent   *RTreeNode

    children []*RTreeNode
    leaf     bool
}

type RTree struct {
    Root     *RTreeNode
    maxNodes int
    minFill  int
}

/* Helper functions and methods */

func newRTreeNode(numNodes int) *RTreeNode {
    return &RTreeNode{
        Bounds:   Rect{0.0, 0.0, 0.0, 0.0},
        Value:    nil,
        Parent:   nil,
        children: make([]*RTreeNode, 0, numNodes + 1),
        leaf:     true,
    }
}


func (node *RTreeNode) Remove() {
    assert(node.Parent != nil, "remove self on root node")

    found := false
    parent := node.Parent
    children := parent.children

    for i, c := range parent.children {
        if c == node {
            copy(children[i:], children[i+1:])
            children[len(children)-1] = nil
            parent.children = children[:len(children)-1]
            found = true
        }
    }

    assert(found, "failed to find self in parent")
    node.Parent = nil

    if len(parent.children) == 0 {
        if parent.Parent == nil {
            // Reset the root node to be a leaf
            parent.leaf = true
        } else {
            parent.Remove()
        }
    } else {
        for curNode := parent; curNode != nil; curNode = curNode.Parent {
            curNode.Bounds = MinBounds(curNode.children)
        }
    }
}

func (node *RTreeNode) AddChild(child *RTreeNode) {
    n := len(node.children)
    assert(n < cap(node.children), "extending full children array")
    node.children = node.children[:n + 1]
    node.children[n] = child
    child.Parent = node
}

/*
 * Starting at node 'root', finds the best node at which to insert
 * 'node' and returns it.
 */
func (node *RTreeNode) ChooseInsertionPoint(newNode *RTreeNode) *RTreeNode {
    for {
        if node.leaf {
            return node
        }

        var selectedNode *RTreeNode

        minArea := float32(math.Inf(1))
        minEnlargement := float32(math.Inf(1))

        // The best insertion point is the one that minimizes the enlargement
        // of the target subtree's bounding box. Or, if enlargements are the
        // same, the smaller of the resulting areas.
        for _, child := range node.children {
            area := child.Bounds.Area()
            containingBox := newNode.Bounds.Union(&child.Bounds)
            containingArea := containingBox.Area()
            enlargement := containingArea - area

            if enlargement < minEnlargement ||
                (enlargement == minEnlargement && area < minArea) {
                minEnlargement = enlargement
                minArea = Min(area, minArea)
                selectedNode = child
            }
        }

        node = selectedNode
    }
}

type ByLeft []*RTreeNode

func (a ByLeft) Len() int           { return len(a) }
func (a ByLeft) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByLeft) Less(i, j int) bool { return a[i].Bounds.Left < a[j].Bounds.Left }

type ByTop []*RTreeNode

func (a ByTop) Len() int           { return len(a) }
func (a ByTop) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByTop) Less(i, j int) bool { return a[i].Bounds.Top < a[j].Bounds.Top }

func (node *RTreeNode) TotalPerimeter(minFill int) float32 {
    count := len(node.children)

    lowerRect := MinBounds(node.children[:minFill])
    upperBegin := count - minFill
    upperRect := MinBounds(node.children[upperBegin:])

    perimeter := lowerRect.Perimeter() + upperRect.Perimeter()

    // Iterate the possible distributions for the first rectangle.
    for i := minFill; i < upperBegin; i++ {
        lowerRect = lowerRect.Union(&node.children[i].Bounds)
        perimeter += lowerRect.Perimeter()
    }

    // Iterate possible distributions for the second, expanding down from
    // the lower bound on the upper rectangle.
    for i := upperBegin - 1; i >= minFill; i-- {
        upperRect = upperRect.Union(&node.children[i].Bounds)
        perimeter += upperRect.Perimeter()
    }

    return perimeter
}

func (node *RTreeNode) SortOnSplitAxis(minFill int) {
    sort.Sort(ByLeft(node.children))
    xPerim := node.TotalPerimeter(minFill)

    sort.Sort(ByTop(node.children))
    yPerim := node.TotalPerimeter(minFill)

    // Also sort by bottom and right?

    if xPerim < yPerim {
        sort.Sort(ByLeft(node.children))
    } else {
        // We're already sorted by Y
    }
}

func (node *RTreeNode) ChooseSplitPoint(minFill int) int {
    minOverlap := float32(math.Inf(1))
    minArea := float32(math.Inf(1))
    point := -1

    for i := minFill; i < len(node.children)-minFill; i++ {
        lowerRect := MinBounds(node.children[:i])
        upperRect := MinBounds(node.children[i:])

        overlap := lowerRect.IntersectionArea(&upperRect)
        area := lowerRect.Area() + upperRect.Area()

        if overlap < minOverlap || (overlap == minOverlap && area < minArea) {
            minOverlap = overlap
            point = i
            minArea = Min(area, minArea)
        }
    }

    return point
}

func (node *RTreeNode) Split(minFill, maxNodes int) *RTreeNode {
    node.SortOnSplitAxis(minFill)
    splitPoint := node.ChooseSplitPoint(minFill)

    newNode := newRTreeNode(maxNodes)
    newNode.leaf = node.leaf
    //newNode.children = newNode.children[:len(node.children) - splitPoint]

    for i := splitPoint; i < len(node.children); i++ {
        newNode.AddChild(node.children[i])
        //newNode.children[j] = node.children[i]
        //newNode.children[j].Parent
        node.children[i] = nil
    }

    node.children = node.children[:splitPoint]

    node.Bounds = MinBounds(node.children)
    newNode.Bounds = MinBounds(newNode.children)

    return newNode
}

func (rtree *RTree) Insert(value interface{}, bounds Rect) *RTreeNode {
    newNode := newRTreeNode(rtree.maxNodes)
    newNode.Value = value
    newNode.Bounds = bounds
    newNode.leaf = false

    insertionNode := rtree.Root.ChooseInsertionPoint(newNode)
    insertionNode.AddChild(newNode)

    currentNode := insertionNode

    for len(currentNode.children) > rtree.maxNodes {
        splitNode := currentNode.Split(rtree.minFill, rtree.maxNodes)

        if currentNode.Parent != nil {
            currentNode.Parent.AddChild(splitNode)
            currentNode = currentNode.Parent
        } else {
            rtree.Root = newRTreeNode(rtree.maxNodes)
            rtree.Root.leaf = false
            rtree.Root.AddChild(currentNode)
            rtree.Root.AddChild(splitNode)
            currentNode = rtree.Root
            assert(len(currentNode.children) <= rtree.maxNodes, "oops")
        }
    }

    // Propogate bounds changes up the tree, handles both
    // split and non-split cases
    for currentNode != nil {
        // Or just union with newly added node's bounds rect?
        currentNode.Bounds = MinBounds(currentNode.children)
        currentNode = currentNode.Parent
    }

    return newNode
}

/* Public interface */

func New(maxNodes, minFill int) *RTree {
    root := newRTreeNode(maxNodes)

    return &RTree{
        Root:     root,
        maxNodes: maxNodes,
        minFill:  minFill,
    }
}

func (rtree *RTree) Visit(x, y float32, cb func(value interface{}, bounds Rect)) {
    var recurse func(node *RTreeNode)

    recurse = func(node *RTreeNode) {
        for _, child := range node.children {
            if child.Bounds.Contains(x, y) {
                if node.leaf {
                    cb(child.Value, child.Bounds)
                } else {
                    recurse(child)
                }
            }
        }
    }

    recurse(rtree.Root)
}

func (rtree *RTree) VisitAll(cb func(value interface{}, bounds Rect)) {
    var recurse func(node *RTreeNode)

    recurse = func(node *RTreeNode) {
        for _, child := range node.children {
            if node.leaf {
                cb(child.Value, child.Bounds)
            } else {
                recurse(child)
            }
        }
    }

    recurse(rtree.Root)
}
