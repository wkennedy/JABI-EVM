
package com.github.wkennedy.abi.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogTest {

    @Test
    public void equals_sameObject_returnsTrue() {
        List<String> topics = new ArrayList<>();
        topics.add("topic1");
        Log log1 = new Log("data", topics, "address");
        assertTrue(log1.equals(log1));
    }

    @Test
    public void equals_nullObject_returnsFalse() {
        List<String> topics = new ArrayList<>();
        topics.add("topic1");
        Log log1 = new Log("data", topics, "address");
        assertFalse(log1.equals(null));
    }

    @Test
    public void equals_differentTypeObject_returnsFalse() {
        List<String> topics = new ArrayList<>();
        topics.add("topic1");
        Log log1 = new Log("data", topics, "address");
        assertFalse(log1.equals("String"));
    }

    @Test
    public void equals_sameValueObject_returnsTrue() {
        List<String> topics1 = new ArrayList<>();
        topics1.add("topic1");
        Log log1 = new Log("data", topics1, "address");

        List<String> topics2 = new ArrayList<>();
        topics2.add("topic1");
        Log log2 = new Log("data", topics2, "address");

        assertTrue(log1.equals(log2));
    }

    @Test
    public void equals_differentValueObject_returnsFalse() {
        List<String> topics1 = new ArrayList<>();
        topics1.add("topic1");
        Log log1 = new Log("data", topics1, "address");

        List<String> topics2 = new ArrayList<>();
        topics2.add("topic2");
        Log log2 = new Log("data", topics2, "address");

        assertFalse(log1.equals(log2));
    }
    @Test
    public void hashCode_sameValueObjects_returnSameHashcode() {
        List<String> topics1 = new ArrayList<>();
        topics1.add("topic1");
        Log log1 = new Log("data", topics1, "address");

        List<String> topics2 = new ArrayList<>();
        topics2.add("topic1");
        Log log2 = new Log("data", topics2, "address");

        assertEquals(log1.hashCode(), log2.hashCode());
    }

    @Test
    public void hashCode_differentValueObjects_returnDifferentHashcode() {
        List<String> topics1 = new ArrayList<>();
        topics1.add("topic1");
        Log log1 = new Log("data", topics1, "address");

        List<String> topics2 = new ArrayList<>();
        topics2.add("topic2");
        Log log2 = new Log("data", topics2, "address");

        assertNotEquals(log1.hashCode(), log2.hashCode());
    }
}
