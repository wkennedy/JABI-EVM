package com.github.wkennedy.abi;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SolidityTypeTest {

    @Test
    public void testEncodeList() {
        SolidityType.StaticArrayType staticArrayType = new SolidityType.StaticArrayType("uint[3]");
        List<Integer> list = Arrays.asList(0, 1, 2);
        byte[] result = staticArrayType.encodeList(list);
        assertNotNull(result);
    }

    @Test
    public void testEncodeListMismatchSize() {
        SolidityType.StaticArrayType staticArrayType = new SolidityType.StaticArrayType("uint[2]");
        List<Integer> list = Arrays.asList(0, 1, 2);
        assertThrowsExactly(RuntimeException.class, () -> staticArrayType.encodeList(list));
    }

    @Test
    public void testDecode() {
        SolidityType.StaticArrayType staticArrayType = new SolidityType.StaticArrayType("uint[1]");
        byte[] bytes = new byte[]{Integer.valueOf(3).byteValue()};
        Object[] result = staticArrayType.decode(bytes, 0);
        assertNotNull(result);
    }

    @Test
    public void testFixedSize() {
        SolidityType.StaticArrayType staticArrayType = new SolidityType.StaticArrayType("uint[3]");
        int size = staticArrayType.getFixedSize();
        assertEquals(96, size);
    }

    @Test
    public void testIsDynamicType() {
        SolidityType.StaticArrayType staticArrayType = new SolidityType.StaticArrayType("uint[3]");
        boolean result = staticArrayType.isDynamicType();
        assertFalse(result);
    }
    @Test
    public void testEncodeListDynamicType() {
        SolidityType.DynamicArrayType dynamicArrayType = new SolidityType.DynamicArrayType("uint[]");
        List<Integer> list = Arrays.asList(0, 1, 2);
        byte[] result = dynamicArrayType.encodeList(list);
        assertNotNull(result);
    }

    @Test
    public void testEncodeListDynamicTypeMismatchSize() {
        SolidityType.DynamicArrayType dynamicArrayType = new SolidityType.DynamicArrayType("uint[]");
        List<Integer> list = Arrays.asList(0, 1, 2, 3);
        byte[] result = dynamicArrayType.encodeList(list);
        assertNotNull(result);
    }

    @Test
    public void testEncodeListDynamicTypeEmptyList() {
        SolidityType.DynamicArrayType dynamicArrayType = new SolidityType.DynamicArrayType("uint[]");
        List<Integer> list = new ArrayList<>();
        byte[] result = dynamicArrayType.encodeList(list);
        assertNotNull(result);
        assertNotNull(result);
    }

    @Test
    public void testStringTypeEncodeValidInput() {
        SolidityType.StringType stringType = new SolidityType.StringType();
        String value = "TestString";
        byte[] result = stringType.encode(value);
        assertNotNull(result);
    }

    @Test
    public void testStringTypeEncodeInvalidInput() {
        SolidityType.StringType stringType = new SolidityType.StringType();
        Integer value = 12345;
        assertThrows(RuntimeException.class, () -> stringType.encode(value));
    }
    @Test
    public void testBoolTypeEncodeValidInputTrue() {
        SolidityType.BoolType boolType = new SolidityType.BoolType();
        Boolean value = true;
        byte[] result = boolType.encode(value);
        assertNotNull(result);
    }

    @Test
    public void testBoolTypeEncodeValidInputFalse() {
        SolidityType.BoolType boolType = new SolidityType.BoolType();
        Boolean value = false;
        byte[] result = boolType.encode(value);
        assertNotNull(result);
    }

    @Test
    public void testBoolTypeEncodeInvalidInput() {
        SolidityType.BoolType boolType = new SolidityType.BoolType();
        Integer value = 12345;
        assertThrows(RuntimeException.class, () -> boolType.encode(value));
    }
    @Test
    public void testAddressTypeEncodeValidInput() {
        SolidityType.AddressType addressType = new SolidityType.AddressType();
        String value = "abcd1234abcd1234abcd1234abcd1234abcd1234";
        byte[] result = addressType.encode(value);
        assertNotNull(result);
    }

    @Test
    public void testAddressTypeEncodeInvalidHexInput() {
        SolidityType.AddressType addressType = new SolidityType.AddressType();
        String value = "GHIJKLMNOP";
        assertThrows(RuntimeException.class, () -> addressType.encode(value));
    }

    @Test
    public void testDecodeStaticArrayTypeInvalidInput() {
        SolidityType.StaticArrayType staticArrayType = new SolidityType.StaticArrayType("uint[3]");
        byte[] bytes = new byte[]{0, 0, 0, 1, 0, 0, 0, 2};
        assertThrows(RuntimeException.class, () -> staticArrayType.decode(bytes, 0));
    }

    @Test
    public void testDecodeBytesTypeValidInput() {
        SolidityType.BytesType bytesType = new SolidityType.BytesType();
        byte[] value = "TestBytes".getBytes(StandardCharsets.UTF_8);
        byte[] encoded = bytesType.encode(value);
        byte[] result = (byte[]) bytesType.decode(encoded, 0);
        assertArrayEquals(value, result);
    }

    @Test
    public void testDecodeBytesTypeEmptyInput() {
        SolidityType.BytesType bytesType = new SolidityType.BytesType();
        byte[] value = new byte[0];
        byte[] encoded = bytesType.encode(value);
        byte[] result = (byte[]) bytesType.decode(encoded, 0);
        assertArrayEquals(value, result);
    }

    @Test
    public void testDecodeBytesTypeInvalidInput() {
        SolidityType.BytesType bytesType = new SolidityType.BytesType();
        byte[] encoded = new byte[0];
        assertThrows(RuntimeException.class, () -> bytesType.decode(encoded, 100));
    }

    @Test
    public void testUintArrayMatches() {
        SolidityType type = SolidityType.getType("uint256[]");
        assertTrue(type.isDynamicType());
    }

}
