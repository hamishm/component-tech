package strict_json


import (
    "errors"
    "encoding/json"
    "reflect"
    "fmt"
)

func unmarshalStruct(decoded map[string]interface{}, value interface{}) error {
    s := reflect.ValueOf(value)
    t := s.Type()

    fmt.Println("hi2")
    for i := 0; i < t.NumField(); i++ {
        tf := t.Field(i)
        sf := s.Field(i)
        jsonField := tf.Tag.Get("json")
        val, ok := decoded[jsonField]
        if !ok {
            return errors.New(fmt.Sprintf("Missing field %s", jsonField))
        }
        //if !f.CanSet() {
         //   return errors.New(fmt.Sprintf("Cannot set field %s", jsonField))
        //}
        switch reflect.ValueOf(val).Kind() {
        case reflect.Float64:
            fmt.Println("Is integering ish!")
            switch sf.Kind() {
            case reflect.Int, reflect.Int8, reflect.Int16, reflect.Int32, reflect.Int64:
                sf.SetInt(int64(val.(float64)))
            case reflect.Float32, reflect.Float64:
                sf.SetFloat(float64(val.(float64)))
            default:
                return errors.New(fmt.Sprintf("Cannot unmarshal type %q into %q", reflect.TypeOf(val), sf.Kind()))
            }
        }
        fmt.Println(reflect.TypeOf(val))
        fmt.Println(tf.Type)
    }
    return nil
}

func Unmarshal(data []byte, value interface{}) error {
    var val interface{}
    err := json.Unmarshal(data, &val)
    if err != nil {
        return err
    }
    fmt.Println("hi")
    switch val.(type) {
    case map[string]interface{}:
        fmt.Println("hi")
        return unmarshalStruct(val.(map[string]interface{}), value)
    default:
        panic("only use with structs please")
    }
}