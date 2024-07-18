package com.github.wkennedy.abi.models;

import com.github.wkennedy.abi.models.DecodedFunctions;
import com.github.wkennedy.abi.models.Param;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class DecodedFunctionsTest {

    @Test
    void testEquals_sameObject_ReturnsTrue() {
        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Collections.emptyList());
        assertTrue(decodedFunctions1.equals(decodedFunctions1));
    }

    @Test
    void testEquals_sameValues_ReturnsTrue() {
        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Collections.emptyList());
        DecodedFunctions decodedFunctions2 = new DecodedFunctions("function1", Collections.emptyList());
        assertTrue(decodedFunctions1.equals(decodedFunctions2));
    }

    @Test
    void testEquals_differentNames_ReturnsFalse() {
        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Collections.emptyList());
        DecodedFunctions decodedFunctions2 = new DecodedFunctions("function2", Collections.emptyList());
        assertFalse(decodedFunctions1.equals(decodedFunctions2));
    }

    @Test
    void testEquals_differentParams_ReturnsFalse() {
        Param param1 = new Param("param1", "type1", "value");
        Param param2 = new Param("param2", "type1", "value");

        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Arrays.asList(param1));
        DecodedFunctions decodedFunctions2 = new DecodedFunctions("function1", Arrays.asList(param2));

        assertFalse(decodedFunctions1.equals(decodedFunctions2));
    }

    @Test
    void testEquals_nullObject_ReturnsFalse() {
        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Collections.emptyList());
        assertFalse(decodedFunctions1.equals(null));
    }

    @Test
    void testEquals_nonDecodedFunctionsObject_ReturnsFalse() {
        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Collections.emptyList());
        assertFalse(decodedFunctions1.equals("Not a DecodedFunctions object"));
    }

    @Test
    void testEquals_differentNestedDecodedFunctions_ReturnsFalse() {
        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Collections.emptyList());
        DecodedFunctions decodedFunctions2 = new DecodedFunctions("function1", Collections.emptyList());
        
        DecodedFunctions nestedDecodedFunctions = new DecodedFunctions("nestedFunction", Collections.emptyList());
        decodedFunctions1.addNestedDecodedFunction(nestedDecodedFunctions);

        assertFalse(decodedFunctions1.equals(decodedFunctions2));
    }

    @Test
    void testHashCode_sameValues_ReturnsSameHashCode() {
        DecodedFunctions decodedFunctions1 = new DecodedFunctions("function1", Collections.emptyList());
        DecodedFunctions decodedFunctions2 = new DecodedFunctions("function1", Collections.emptyList());
        assertEquals(decodedFunctions1.hashCode(), decodedFunctions2.hashCode());
    }
}
