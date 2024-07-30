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
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/uniswap_abi.json")));
        Decoder decoder = new Decoder();
        decoder.addAbi("0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6", abiJson);
        Predicate<AbiFunction> exists = fn -> fn.name.equals("getAmountsOut");
        Optional<AbiFunction> function = decoder.getAbis().get("0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6").findFunction(exists);
        byte[] bytes = org.apache.commons.codec.binary.Hex.decodeHex("0x0000000000000000000000000000000000000000000000000000000000000012".replace(HEX_PREFIX, "").toUpperCase());
        List<?> decode = function.get().decodeResult(bytes);
        assertEquals(1, decode.size());
        assertEquals(BigInteger.valueOf(18), decode.getFirst());
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
}
