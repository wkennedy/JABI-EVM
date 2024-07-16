package com.github.wkennedy.abi.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DecodedLog {
    private String name;
    private String address;
    private List<Param> events;

    public DecodedLog() {
    }

    public DecodedLog(String name, String address, List<Param> events) {
        this.name = name;
        this.address = address;
        this.events = events;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Param> getEvents() {
        return events;
    }

    public void setEvents(List<Param> events) {
        this.events = events;
    }

    public Param getEvent(String name) {
        return Optional.ofNullable(events)
                .flatMap(eventList ->
                        eventList.stream().filter(event -> event.getName().equals(name)).findFirst())
                .orElse(null);
    }

    public Map<String, Param> getEventsMap() {
        Map<String, Param> eventsMap = new HashMap<>();
        if (events != null) {
            for (Param event : events) {
                eventsMap.put(event.getName(), event);
            }
        }
        return eventsMap;
    }

    @Override
    public String toString() {
        return "DecodedLog{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", events=" + events +
                '}';
    }
}
