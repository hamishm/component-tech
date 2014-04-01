package strict_json_test


import (
    "component-tech/strict_json"
    "fmt"
    "reflect"
    "testing"
)


type Nested struct {
    X string `json:"x"`
    Y string `json:"y"`
}

type TestingType struct {
    A int     `json:"a"`
    B float32 `json:"b"`
    C Nested  `json:"c"`
}


type TestingPtrType struct {
    A int     `json:"a"`
    B float32 `json:"b"`
    C *Nested `json:"c"`
}

type TestingMapType struct {
    A int                    `json:"a"`
    B float32                `json:"b"`
    C map[string]interface{} `json:"c"`
}


func TestStrictJSON(t *testing.T) {
    validJSON := []byte("{\"a\": 5, \"b\": 23.45, \"c\": {\"x\": \"hi\", \"y\": \"sup\"}}")
    expected := TestingType{A: 5, B: 23.45, C: Nested{X: "hi", Y: "sup"}}

    var testVal TestingType
    err := strict_json.Unmarshal(validJSON, &testVal)

    if err != nil {
        t.Error("unmarshaling valid JSON failed")
    } else if testVal != expected {
        t.Error("non-matching unmarshalling.\nGot", testVal, "expected", expected)
    }

    wrongFieldType := []byte("{\"a\": \"aaa\", \"b\": 23.45, \"c\": {\"x\": \"hi\", \"y\": \"sup\"}}")
    err = strict_json.Unmarshal(wrongFieldType, &testVal)

    if err == nil {
        t.Error("wrong field type - should have returned error")
    }
    fmt.Println(err)

    missingField := []byte("{\"b\": 23.45, \"c\": {\"x\": \"hi\", \"y\": \"sup\"}}")
    err = strict_json.Unmarshal(missingField, &testVal)

    if err == nil {
        t.Error("missing field - should have returned error")
    }
    fmt.Println(err)

    var testPtrVal TestingPtrType
    expectedPtr := TestingPtrType{A: 5, B: 23.45, C: &Nested{X: "hi", Y: "sup"}}
    err = strict_json.Unmarshal(validJSON, &testPtrVal)

    if err != nil {
        t.Error("unmarshing valid JSON into struct with pointer failed")
    } else if !reflect.DeepEqual(testPtrVal, expectedPtr) {
        t.Error("non-matching unmarshalling with pointer")
    }

    fmt.Println(expectedPtr)
    fmt.Println(expectedPtr.C)

    expectedMap := TestingMapType{A: 5, B: 23.45, C: map[string]interface{}{"x": "hi", "y": "sup"}}
    var testMapVal TestingMapType
    err = strict_json.Unmarshal(validJSON, &testMapVal)

    if err != nil {
        t.Error("unmarshing valid JSON into struct with map failed")
    } else if !reflect.DeepEqual(testMapVal, expectedMap) {
        t.Error("non-matching unmarshalling with pointer")
    }

    fmt.Println(testMapVal)
}

type TestingSlice struct {
    Slice []Nested `json:"slice"`
}

type TestingArray struct {
    Slice [5]Nested `json:"slice"`
}

func TestJSONArray(t *testing.T) {
    validJSON := []byte("{\"slice\": [{\"x\": \"a\", \"y\": \"b\"}, " +
        "{\"x\": \"c\", \"y\": \"d\"}, {\"x\": \"e\", \"y\": \"f\"}, " +
        "{\"x\": \"g\", \"y\": \"h\"}, {\"x\": \"i\", \"y\": \"j\"}] }")

    expected := TestingSlice{
        Slice: []Nested{
            {X: "a", Y: "b"},
            {X: "c", Y: "d"},
            {X: "e", Y: "f"},
            {X: "g", Y: "h"},
            {X: "i", Y: "j"},
        },
    }

    var testVal TestingSlice
    err := strict_json.Unmarshal(validJSON, &testVal)

    if err != nil {
        fmt.Println(err)
        t.Error("unmarshing valid JSON into struct with slice failed")
    } else if !reflect.DeepEqual(testVal, expected) {
        t.Error("non-matching unmarshalling with slice")
    }

    expectedArray := TestingArray{
        Slice: [5]Nested{
            {X: "a", Y: "b"},
            {X: "c", Y: "d"},
            {X: "e", Y: "f"},
            {X: "g", Y: "h"},
            {X: "i", Y: "j"},
        },
    }

    var testArrayVal TestingArray
    err = strict_json.Unmarshal(validJSON, &testArrayVal)

    if err != nil {
        fmt.Println(err)
        t.Error("unmarshing valid JSON into struct with array failed")
    } else if !reflect.DeepEqual(testArrayVal, expectedArray) {
        t.Error("non-matching unmarshalling with array")
    }

    tooManyJSON := []byte("{\"slice\": [{\"x\": \"a\", \"y\": \"b\"}, " +
        "{\"x\": \"c\", \"y\": \"d\"}, {\"x\": \"e\", \"y\": \"f\"}, " +
        "{\"x\": \"g\", \"y\": \"h\"}, {\"x\": \"i\", \"y\": \"j\"}, " +
        "{\"x\": \"k\", \"y\": \"l\"}]}")

    var testTooManyVal TestingArray
    err = strict_json.Unmarshal(tooManyJSON, &testTooManyVal)

    if err == nil {
        t.Error("array too short - should have returned error")
    }
    fmt.Println(err)
}
