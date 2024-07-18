package com.github.wkennedy.abi.models;

import java.util.ArrayList;
import java.util.List;

public class Log {
    private String data;
    private List<String> topics = new ArrayList<>();
    private String address;

    public Log() {
    }

    public Log(String data, List<String> topics, String address) {
        this.data = data;
        this.topics = topics;
        this.address = address;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Log{" +
                "data='" + data + '\'' +
                ", topics=" + topics +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Log log = (Log) o;
        return data.equals(log.data) && topics.equals(log.topics) && address.equals(log.address);
    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        result = 31 * result + topics.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }
}
