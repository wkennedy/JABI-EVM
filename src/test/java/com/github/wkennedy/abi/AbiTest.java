package com.github.wkennedy.abi;

import com.github.wkennedy.abi.entry.AbiConstructor;
import com.github.wkennedy.abi.entry.AbiEvent;
import com.github.wkennedy.abi.entry.AbiFunction;
import com.github.wkennedy.abi.entry.AbiParam;
import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbiTest {

    @Test
    void findEvent() {
        Abi abi = new Abi();
        AbiParam param = new AbiParam(true, "param", new SolidityType.BoolType());
        AbiEvent event = new AbiEvent(false, "event", Collections.singletonList(param), Collections.singletonList(param));

        abi.add(event);

        Optional<AbiEvent> result;

        Predicate<AbiEvent> exists = ev -> ev.name.equals("event");
        result = abi.findEvent(exists);
        assertTrue(result.isPresent());
        assertEquals(event, result.get());

        Predicate<AbiEvent> notExists = ev -> ev.name.equals("does not exist");
        result = abi.findEvent(notExists);
        assertTrue(result.isEmpty());
    }

    @Test
    void findFunction() {
        Abi abi = new Abi();
        AbiParam param = new AbiParam(true, "param", new SolidityType.BoolType());
        AbiFunction function = new AbiFunction(false, "function", Collections.singletonList(param), Collections.singletonList(param), false);

        abi.add(function);

        Optional<AbiFunction> result;

        Predicate<AbiFunction> exists = fn -> fn.name.equals("function");
        result = abi.findFunction(exists);
        assertTrue(result.isPresent());
        assertEquals(function, result.get());

        Predicate<AbiFunction> notExists = fn -> fn.name.equals("does not exist");
        result = abi.findFunction(notExists);
        assertTrue(result.isEmpty());
    }

    @Test
    void findConstructor() {
        Abi abi = new Abi();
        AbiConstructor constructor = new AbiConstructor(List.of(new AbiParam(false, "param", new SolidityType.BoolType())),
                List.of(new AbiParam(false, "param", new SolidityType.BoolType())));
        abi.add(constructor);

        Optional<AbiConstructor> result;

        result = abi.findConstructor();
        assertTrue(result.isPresent());
        assertEquals(constructor, result.get());
    }
    @Test
    void toJsonTest() {
        Abi abi = new Abi();
        String result;

        AbiParam param = new AbiParam(true, "param", new SolidityType.BoolType());
        AbiFunction function = new AbiFunction(false, "function", Collections.singletonList(param), Collections.singletonList(param), false);
        abi.add(function);

        result = abi.toJson();
        assertTrue(result.contains("\"type\":\"function\""));
        assertTrue(result.contains("\"name\":\"function\""));
        assertTrue(result.contains("\"inputs\":[{\"indexed\":true,\"name\":\"param\",\"type\":\"bool\",\"typeDefinition\":\"bool\"}]"));
        assertTrue(result.contains("\"outputs\":[{\"indexed\":true,\"name\":\"param\",\"type\":\"bool\",\"typeDefinition\":\"bool\"}]"));
    }

    @Test
    void toStringTest() {
        Abi abi = new Abi();
        AbiParam param = new AbiParam(true, "param", new SolidityType.BoolType());
        AbiFunction function = new AbiFunction(false, "function", Collections.singletonList(param), Collections.singletonList(param), false);
        abi.add(function);

        String result = abi.toString();
        assertTrue(result.contains("\"type\":\"function\""));
        assertTrue(result.contains("\"name\":\"function\""));
        assertTrue(result.contains("\"inputs\":[{\"indexed\":true,\"name\":\"param\",\"type\":\"bool\",\"typeDefinition\":\"bool\"}]"));
        assertTrue(result.contains("\"outputs\":[{\"indexed\":true,\"name\":\"param\",\"type\":\"bool\",\"typeDefinition\":\"bool\"}]"));
    }
}
