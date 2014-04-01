package strict_json_test


import (
    "component-tech/strict_json"
    "fmt"
    "testing"
)


type Nested struct {
    X string `json:"x"`
    Y string `json:"y"`
}

type TestingType struct {
    A int     `json:"a"`
    B float32 `json:"b"`
    //C Nested  `json:"c"`
}


func TestStrictJSON(t *testing.T) {
    jsonblah := []byte("{\"a\": 5, \"b\": 23.45}")
    var te TestingType
    fmt.Println(strict_json.Unmarshal(jsonblah, te))
}
