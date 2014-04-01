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
