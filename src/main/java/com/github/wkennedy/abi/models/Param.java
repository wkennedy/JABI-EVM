package com.github.wkennedy.abi.models;

import java.util.Arrays;
import java.util.Objects;

import com.github.wkennedy.util.ByteUtil;

public class Param {
    private String name;
    private String type;
    private Object value;
    private Object rawValue;

    public Param() {
    }

    public Param(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.rawValue = value;
        this.value = ByteUtil.convertValue(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getRawValue() {
        return rawValue;
    }

    public void setRawValue(Object rawValue) {
        this.rawValue = rawValue;
    }

    @Override
    public String toString() {
        return "Param{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                '}';
    }

    public String toDisplayString() {
        String valueString = value == null ? "null" : (value.getClass().isArray() ? Arrays.toString((Object[]) value) : value.toString());
        return this.getClass().getName() + "(name=" + this.name + ", type=" + this.getType() + ", value=" + valueString + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Param param = (Param) o;
        return Objects.equals(name, param.name) && Objects.equals(type, param.type) && Objects.equals(value, param.value) && Objects.equals(rawValue, param.rawValue);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(type);
        result = 31 * result + Objects.hashCode(value);
        result = 31 * result + Objects.hashCode(rawValue);
        return result;
    }
}
