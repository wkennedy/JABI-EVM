
package com.github.wkennedy.abi.models;

import com.github.wkennedy.util.ByteUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParamTest {
    @Test
    public void initParamStringTest() {
        Param param1 = new Param("name1", "type1", "value1");
        Param param2 = new Param("name1", "type1", "value1");
        assertEquals(param1, param2);
    }

    @Test
    public void initParamStringTestNotEqual() {
        Param param1 = new Param("name1", "type1", "value1");
        Param param2 = new Param("name2", "type2", "value2");
        assertNotEquals(param1, param2);
    }

    @Test
    public void initParamNullTest() {
        Param param1 = new Param(null, null, null);
        Param param2 = new Param(null, null, null);
        assertEquals(param1, param2);
    }

    @Test
    public void initParamNullAndStringTest() {
        Param param1 = new Param(null, null, null);
        Param param2 = new Param("name1", "type1", "value1");
        assertNotEquals(param1, param2);
    }

    @Test
    public void initParamObjectTest() {
        Object value1 = ByteUtil.convertValue("value1");
        Param param1 = new Param("name1", "type1", value1);
        Param param2 = new Param("name1", "type1", value1);
        assertEquals(param1, param2);
    }

    @Test
    public void initParamObjectTestNotEqual() {
        Object value1 = ByteUtil.convertValue("value1");
        Object value2 = ByteUtil.convertValue("value2");
        Param param1 = new Param("name1", "type1", value1);
        Param param2 = new Param("name1", "type1", value2);
        assertNotEquals(param1, param2);
    }
    @Test
    public void testHashCodeForEqualObjects() {
        Param param1 = new Param("name1", "type1", "value1");
        Param param2 = new Param("name1", "type1", "value1");
        assertEquals(param1.hashCode(), param2.hashCode());
    }

    @Test
    public void testHashCodeForNotEqualObjects() {
        Param param1 = new Param("name1", "type1", "value1");
        Param param2 = new Param("name2", "type2", "value2");
        assertNotEquals(param1.hashCode(), param2.hashCode());
    }

    @Test
    public void testHashCodeForNullValues() {
        Param param1 = new Param(null, null, null);
        Param param2 = new Param(null, null, null);
        assertEquals(param1.hashCode(), param2.hashCode());
    }

    @Test
    public void testHashCodeForNullAndNonNullValues() {
        Param param1 = new Param(null, null, null);
        Param param2 = new Param("name1", "type1", "value1");
        assertNotEquals(param1.hashCode(), param2.hashCode());
    }
}
