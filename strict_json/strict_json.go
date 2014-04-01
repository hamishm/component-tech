package strict_json


import (
    "encoding/json"
    "reflect"
    "fmt"
)

func unmarshalNumber(number float64, value reflect.Value) error {
    switch k := value.Kind(); k {
    case reflect.Int, reflect.Int8, reflect.Int16, reflect.Int32, reflect.Int64:
        intv := int64(number)
        if value.OverflowInt(intv) {
            fmt.Errorf("Number will overflow field of type %q", k)
        }
        value.SetInt(intv)
    case reflect.Float32, reflect.Float64:
        value.SetFloat(float64(number))
    default:
        return fmt.Errorf("Cannot unmarshal number into %q", k)
    }
    return nil
}

func unmarshalBoolean(boolean bool, value reflect.Value) error {
    switch k := value.Kind(); k {
    case reflect.Bool:
        value.SetBool(boolean)
    default:
        return fmt.Errorf("Cannot unmarshal boolean into %q", k)
    }
    return nil
}

func unmarshalString(str string, value reflect.Value) error {
    switch k := value.Kind(); k {
    case reflect.String:
        value.SetString(str)
    default:
        return fmt.Errorf("Cannot unmarshal string into %q", k)
    }
    return nil
}

func unmarshalStruct(decoded map[string]interface{}, value reflect.Value) error {
    if value.Kind() == reflect.Map {
        _, ok := value.Interface().(map[string]interface{})
        if !ok {
            return fmt.Errorf("Bad type of map for unmarshalling object")
        }
        value.Set(reflect.ValueOf(decoded))
        return nil
    }

    t := value.Type()

    for i := 0; i < t.NumField(); i++ {
        tf := value.Type().Field(i)
        sf := value.Field(i)

        jsonField := tf.Tag.Get("json")

        val, ok := decoded[jsonField]

        if !ok {
            return fmt.Errorf("Missing field %s", jsonField)
        }

        err := unmarshalValue(reflect.ValueOf(val), sf)
        if err != nil {
            return err
        }

        fmt.Println(reflect.TypeOf(val))
        fmt.Println(tf.Type)
    }
    return nil
}

func unmarshalValue(value reflect.Value, settable reflect.Value) error {
    if !settable.CanSet() {
        return fmt.Errorf("Value is not settable")
    }

    if settable.Kind() == reflect.Ptr {
        // If a nil pointer, allocate a new object to unmarshal into
        if settable.IsNil() {
            newPtr := reflect.New(settable.Type().Elem())
            settable.Set(newPtr)
        }
        // We want unmarshal into the pointed value
        settable = settable.Elem()
    }

    val := value.Interface()

    switch value.Kind() {
    case reflect.Float64:
        return unmarshalNumber(val.(float64), settable)
    case reflect.Bool:
        return unmarshalBoolean(val.(bool), settable)
    case reflect.String:
        return unmarshalString(val.(string), settable)
    case reflect.Map:
        return unmarshalStruct(val.(map[string]interface{}), settable)
    //case reflect.Slice:
    //    return unmarshalSlice(val.([]interface{}), settable)
    default:
        panic(value.Kind())
    }
}

func Unmarshal(data []byte, value interface{}) error {
    var decoded interface{}

    err := json.Unmarshal(data, &decoded)
    if err != nil {
        return err
    }

    settable := reflect.ValueOf(value).Elem()
    return unmarshalValue(reflect.ValueOf(decoded), settable)
}
