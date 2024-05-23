package com.github.wkennedy.abi.models;

import java.util.List;

public class Log {
    private String data;
    private List<String> topics;
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
}
