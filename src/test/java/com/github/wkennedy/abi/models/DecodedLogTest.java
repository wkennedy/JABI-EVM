package com.github.wkennedy.abi.models;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
  
public class DecodedLogTest {

    @Test
    void getEventTestEventPresent() {
        Param param1 = new Param("event1", "type1", "value1");
        Param param2 = new Param("event2", "type2", "value2");
        List<Param> events = Arrays.asList(param1, param2);
        
        DecodedLog decodedLog = new DecodedLog("name", "address", events);
        
        Param foundEvent = decodedLog.getEvent("event2");
        
        assertEquals(param2, foundEvent);
    }

    @Test
    void getEventTestEventNotPresent() {
        Param param1 = new Param("event1", "type1", "value1");
        List<Param> events = Arrays.asList(param1);
        
        DecodedLog decodedLog = new DecodedLog("name", "address", events);
        
        Param foundEvent = decodedLog.getEvent("event2");
        
        assertNull(foundEvent);
    }

    @Test
    void getEventTestNoEvents() {
        DecodedLog decodedLog = new DecodedLog("name", "address", null);
        
        Param foundEvent = decodedLog.getEvent("event1");
        
        assertNull(foundEvent);
    }
}