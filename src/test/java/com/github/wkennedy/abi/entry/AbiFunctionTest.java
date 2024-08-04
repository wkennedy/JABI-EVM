package com.github.wkennedy.abi.entry;

import com.github.wkennedy.abi.Decoder;
import com.github.wkennedy.abi.SolidityType;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.wkennedy.util.Constants.HEX_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class AbiFunctionTest {

    @Test
    void decodeResult_withNullArgument_shouldReturnEmptyList() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.emptyList(), Collections.emptyList(), true);
        List<?> result = testedAbiFunction.decodeResult((String) null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void decodeResult_withEmptyStringArgument_shouldReturnEmptyList() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.emptyList(), Collections.emptyList(), true);
        List<?> result = testedAbiFunction.decodeResult("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void decodeResult_withInvalidEncodedResult_shouldThrowException() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.emptyList(), Collections.emptyList(), true);
        assertThrows(RuntimeException.class, () -> testedAbiFunction.decodeResult("invalid encoded result"));
    }
  
    @Test
    void decodeResult_withNullByteArgument_shouldReturnEmptyList() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.emptyList(), Collections.emptyList(), true);
        List<?> result = testedAbiFunction.decodeResult((byte[])null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void decodeResult_withEmptyByteArgument_shouldReturnEmptyList() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.emptyList(), Collections.emptyList(), true);
        List<?> result = testedAbiFunction.decodeResult(new byte[0]);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUniswapFunctionDecode() throws IOException, DecoderException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/erc20_abi.json")));
        Decoder decoder = new Decoder();
        decoder.addAbi("0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6", abiJson);
        Predicate<AbiFunction> exists = fn -> fn.name.equals("decimals");
        Optional<AbiFunction> function = decoder.getAbis().get("0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6").findFunction(exists);
        byte[] bytes = org.apache.commons.codec.binary.Hex.decodeHex("0x0000000000000000000000000000000000000000000000000000000000000012".replace(HEX_PREFIX, "").toUpperCase());
        List<?> decode = function.get().decodeResult(bytes);
        assertEquals(1, decode.size());
        assertEquals(BigInteger.valueOf(18), decode.getFirst());
    }

    @Test
    public void testUniswapFunctionEncode() throws IOException, DecoderException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/uniswap_abi.json")));
        Decoder decoder = new Decoder();
        decoder.addAbi("0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6", abiJson);
        Predicate<AbiFunction> exists = fn -> fn.name.equals("getAmountsOut");
        BigInteger amountIn = new BigInteger("1000000000000000000");
        String[] path = {"0x0000000000000000000000000000456E65726779", "0x45429A2255e7248e57fce99E7239aED3f84B7a53"};

        Optional<AbiFunction> function = decoder.getAbis().get("0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6").findFunction(exists);
        String encodeToHex = function.get().encodeToHex(amountIn, path);
        assertNotNull(encodeToHex);
        assertEquals("0xd06ca61f0000000000000000000000000000000000000000000000000de0b6b3a7640000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000456e6572677900000000000000000000000045429a2255e7248e57fce99e7239aed3f84b7a53", encodeToHex);
    }

    @Test
    void encode_withValidArgs_shouldReturnBytes() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.singletonList(new AbiParam(false, "param",  new SolidityType.IntType("int"))), Collections.emptyList(), true);
        byte[] result = testedAbiFunction.encode(5);
        assertNotNull(result);
    }

    @Test
    void encode_withInvalidArgs_shouldThrowException() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.singletonList(new AbiParam(false, "param", new SolidityType.BoolType())), Collections.emptyList(), true);
        assertThrows(RuntimeException.class, () -> testedAbiFunction.encode("invalid"));
    }

    @Test
    void encode_withEmptyArgs_shouldReturnEmptyBytes() {
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "", Collections.emptyList(), Collections.emptyList(), true);
        byte[] result = testedAbiFunction.encode();
        assertNotNull(result);
        assertEquals(4, result.length);
    }

    @Test
    void decode_functionResult_DynamicArray() {
        String functionResult = "0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000016345785d8a0000000000000000000000000000000000000000000000000000001e3503a1bb2d22";
        AbiFunction testedAbiFunction = new AbiFunction(
                true, "test", Collections.emptyList(), Collections.singletonList(new AbiParam(false, "param",  new SolidityType.DynamicArrayType("uint256[]"))), true);
        List<?> objects = testedAbiFunction.decodeResult(functionResult);
        assertNotNull(objects);
        assertEquals(1, objects.size());
        Object[] amounts = (Object[]) objects.getFirst();
        assertEquals(BigInteger.valueOf(100000000000000000L), amounts[0]);
        assertEquals(BigInteger.valueOf(8502539015892258L), amounts[1]);
    }
}
