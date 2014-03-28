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

type Point struct {
    X, Y float32
}

type Rect struct {
    // Right = left + width
    // Bottom = top + height
    Left, Right, Top, Bottom float32
}

func Min(a, b int) int {
    if a <= b {
        return a
    } else {
        return b
    }
}

func Max(a, b int) int {
    if a >= b {
        return a
    } else {
        return b
    }
}

func (rect *Rect) Contains(point Point) bool {
    return point.Y >= rect.Top &&
        point.Y < rect.Bottom &&
        point.X >= rect.Left &&
        point.X < rect.Right
}

func (rect *Rect) Circumference() float32 {
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

func MinBoundingBox(nodes []*RTreeNode) Rect {
    rect := Rect{0.0, 0.0, 0.0, 0.0}

    for node := range nodes {
        rect.Top = min(rect.Top, node.BoundingBox.Top)
        rect.Left = min(rect.Left, node.BoundingBox.Left)
        rect.Right = max(rect.Right, node.BoundingBox.Right)
        rect.Bottom = max(rect.Bottom, node.BoundingBox.Bottom)
    }

    return rect
}

/* Data types */

type RTreeNode struct {
    Children    []*RTreeNode
    BoundingBox Rect
    Value       interface{}

    parent         *RTreeNode
    parentOfLeaves bool
}

type RTree struct {
    Root RTreeNode

    maxNodes int
    minFill  int
}

/* Helper functions and methods */

func newRTreeNode(numNodes int) *RTreeNode {
    return &RTreeNode{
        Children:       make([]*RTreeNode, 0, numNodes + 1),
        BoundingBox:    Rect{0.0, 0.0, 0.0, 0.0},
        Value:          nil,
        parent:         nil,
        parentOfLeaves: true,
    }
}

func (node *RTreeNode) AddChild(node *RTreeNode) {
    n := len(node.Children)
    assert(n < cap(node.Children), "extending full children array")
    node.Children = node.Children[:n + 1]
    node.Children[n] = node
}

/*
 * Starting at node 'root', finds the best node at which to insert
 * 'node' and returns it.
 */
func (root *RTreeNode) ChooseInsertionPoint(node *RTreeNode) *RTreeNode {
    for {
        if node.parentOfLeaves {
            return node
        }

        var selectedNode *RTreeNode

        minArea := math.Inf(1)
        minEnlargement := math.Inf(1)

        // The best insertion point is the one that minimizes the enlargement
        // of the target subtree's bounding box. Or, if enlargements are the
        // same, the smaller of the resulting areas.
        for child := range node.Children {
            area := child.BoundingBox
            containingBox := node.BoundingBox.Union(child.BoundingBox)
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

func (a *ByLeft) Len() int           { return len(a) }
func (a *ByLeft) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a *ByLeft) Less(i, j int) bool { return a[i].BoundingBox.Left < a[j].BoundingBox.Left }

type ByTop []*RTreeNode

func (a *ByTop) Len() int           { return len(a) }
func (a *ByTop) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a *ByTop) Less(i, j int) bool { return a[i].BoundingBox.Top < a[j].BoundingBox.Top }

func (node *RTreeNode) TotalPerimeter(minFill int) float32 {
    count := len(node.Children)

    lowerRect := MinBoundingBox(node.Children[:minFill])
    upperBegin := count - minFill
    upperRect := MinBoundingBox(node.Children[upperBegin:])

    perimeter := firstRect.Perimeter() + secondRect.Perimeter()

    // Iterate the possible distributions for the first rectangle.
    for i := minFill; i < upperBegin; i++ {
        lowerRect = lowerRect.Union(node.Children[i].BoundingBox)
        perimeter += lowerRect.Perimeter()
    }

    // Iterate possible distributions for the second, expanding down from
    // the lower bound on the upper rectangle.
    for i := upperBegin - 1; i >= minFill; i-- {
        upperRect = upperRect.Union(node.Children[i].BoundingBox)
        perimeter += upperRect.Perimeter()
    }

    return perimeter
}

func (node *RTreeNode) SortOnSplitAxis(minFill int) {
    sort.Sort(ByLeft(node.Children))
    xPerim := node.TotalPerimeter(minFill)

    sort.Sort(ByTop(node.Children))
    yPerim := node.TotalPerimeter(minFill)

    // Also sort by bottom and right?

    if xPerim < yPerim {
        sort.Sort(ByLeft(node.Children))
    } else {
        // We're already sorted by Y
    }
}

func (node *RTreeNode) ChooseSplitPoint(minFill int) int {
    minOverlap := math.Inf(1)
    minArea := math.Inf(1)

    point := -1

    for i := minFill; i < len(node.Children)-minFill; i++ {
        lowerRect := MinBoundingBox(node.Children[:i])
        upperRect := MinBoundingBox(node.Children[i:])

        overlap := lowerRect.Intersection(upperRect).Area()
        area := lowerRect.Area() + upperRect.Area()

        if overlap < minOverlap || (overlap == minOverlap && area < minArea) {
            minOverlap = overlap
            point = index
            minArea = Min(area, minArea)
        }
    }

    return point
}

func (node *RTreeNode) Split(minFill, maxNodes int) *RTreeNode {
    node.SortOnSplitAccess(minFill, maxNodes)
    splitPoint := node.ChooseSplitPoint(minFill, maxNodes)

    newNode := newRTreeNode(maxNodes)
    newNode.parentOfLeaves = node.parentOfLeaves
    newNode.parent = node.parent

    for i := 0; i < splitPoint; i++ {
        newNode.Children[i] = node.Children[i]
        node.Children[i] = nil
    }

    node.Children = node.Children[:i]
    return newNode
}

func (rtree *RTree) Insert(value interface{}, boundingBox Rect) {
    newNode := newRTreeNode(rtree.maxNodes)
    newNode.Value = value
    newNode.parentOfLeaves = false

    insertionNode := rtree.Root.ChooseInsertionPoint()
    insertionNode.AddChild(newNode)

    currentNode := insertionNode

    for len(currentNode.Children) > rtree.maxNodes {
        splitNode := currentNode.Split(rtree.minFill)

        if currentNode.parent != nil {
            currentNode.parent.AddChild(splitNode)
            currentNode := currentNode.parent
        } else {
            newRoot := newRTreeNode(rtree.maxNodes)
            newRoot.parentOfLeaves = false
            newRoot.AddChild(currentNode)
            newRoot.AddChild(splitNode)
        }
    }
}

/* Public interface */

func New(maxNodes int, minFill int) *RTree {
    root := newRTreeNode(maxNodes)

    return &RTree{
        Root:     root,
        maxNodes: maxNodes,
        minFill:  minFill,
    }
}

func (rtree *RTree) Visit(point Point, cb func(*RTreeNode)) {
    recurse := func(node *RTreeNode) {
        for child := range node.Children {
            if child.BoundingBox.Contains(point) {
                if node.parentOfLeaves {
                    cb(child)
                } else {
                    recurse(child)
                }
            }
        }
    }

    recurse(rtree.Root)
}

func (rtree *RTree) Search(point Point) []*RTreeNode {
    result := make([]*RTreeNode, 0, 10)
    rtree.Visit(point, func(node *RTreeNode) {
        result = append(result, node)
    })
    return result
}
