package com.github.wkennedy.abi.models;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        List<Param> events = List.of(param1);

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

    @Test
    void getEventsMapTestNoEvents() {
        DecodedLog decodedLog = new DecodedLog("name", "address", null);

        Map<String, Param> eventsMap = decodedLog.getEventsMap();

        assertTrue(eventsMap.isEmpty());
    }

    @Test
    void getEventsMapTestSingleEvent() {
        Param param1 = new Param("event1", "type1", "value1");
        List<Param> events = List.of(param1);

        DecodedLog decodedLog = new DecodedLog("name", "address", events);

        Map<String, Param> eventsMap = decodedLog.getEventsMap();

        assertEquals(1, eventsMap.size());
        assertTrue(eventsMap.containsKey("event1"));
        assertEquals(param1, eventsMap.get("event1"));
    }

    @Test
    void getEventsMapTestMultipleEvents() {
        Param param1 = new Param("event1", "type1", "value1");
        Param param2 = new Param("event2", "type2", "value2");
        List<Param> events = Arrays.asList(param1, param2);

        DecodedLog decodedLog = new DecodedLog("name", "address", events);

        Map<String, Param> eventsMap = decodedLog.getEventsMap();

        assertEquals(2, eventsMap.size());
        assertTrue(eventsMap.containsKey("event1"));
        assertTrue(eventsMap.containsKey("event2"));
        assertEquals(param1, eventsMap.get("event1"));
        assertEquals(param2, eventsMap.get("event2"));
    }

    @Test
    void equalsTestSameObject() {
        DecodedLog decodedLog1 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        assertTrue(decodedLog1.equals(decodedLog1));
    }

    @Test
    void equalsTestDifferentObjectSameValues() {
        DecodedLog decodedLog1 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        DecodedLog decodedLog2 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        assertTrue(decodedLog1.equals(decodedLog2));
    }

    @Test
    void equalsTestDifferentObjectDifferentValues() {
        DecodedLog decodedLog1 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        DecodedLog decodedLog2 = new DecodedLog("name2", "address2", Arrays.asList(new Param("event2", "type2", "value2")));
        assertFalse(decodedLog1.equals(decodedLog2));
    }

    @Test
    void equalsTestObjectOfTypeOtherThanDecodedLog() {
        DecodedLog decodedLog = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        String otherObject = "Not a DecodedLog";
        assertFalse(decodedLog.equals(otherObject));
    }

    @Test
    void equalsTestObjectToNull() {
        DecodedLog decodedLog = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        assertFalse(decodedLog.equals(null));
    }

    @Test
    void hashCodeTestEqualObjects() {
        DecodedLog decodedLog1 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        DecodedLog decodedLog2 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        assertEquals(decodedLog1.hashCode(), decodedLog2.hashCode());
    }

    @Test
    void hashCodeTestNotEqualObjects() {
        DecodedLog decodedLog1 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        DecodedLog decodedLog2 = new DecodedLog("name2", "address2", Arrays.asList(new Param("event2", "type2", "value2")));
        assertNotEquals(decodedLog1.hashCode(), decodedLog2.hashCode());
    }

    @Test
    void hashCodeTestSameObject() {
        DecodedLog decodedLog1 = new DecodedLog("name1", "address1", Arrays.asList(new Param("event1", "type1", "value1")));
        assertEquals(decodedLog1.hashCode(), decodedLog1.hashCode());
    }
}
