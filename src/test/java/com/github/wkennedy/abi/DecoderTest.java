package com.github.wkennedy.abi;

import com.github.wkennedy.abi.entry.AbiEntry;
import com.github.wkennedy.abi.models.DecodedFunctions;
import com.github.wkennedy.abi.models.DecodedLog;
import com.github.wkennedy.abi.models.Log;
import com.github.wkennedy.abi.models.Param;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.wkennedy.util.Constants.HEX_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecoderTest {

    @Test
    public void getAbis() {
        Decoder decoder = new Decoder();
        Map<String, Abi> abis = decoder.getAbis();
        assertEquals(0, abis.size());
    }

    @Test
    public void addAbis() throws IOException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/simple_abi.json")));

        Decoder decoder = new Decoder();
        decoder.addAbi("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114de5", abiJson);
        Map<String, Abi> abis = decoder.getAbis();
        assertEquals(1, abis.size());
        Map<String, AbiEntry> methodIDs = decoder.getMethodIDs();
        assertEquals(5, methodIDs.size());
    }

    @Test
    public void decodeFunction() throws IOException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/simple_abi.json")));

        String testData = "0x53d9d9100000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114de5000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114daa";
        Decoder decoder = new Decoder();
        decoder.addAbi("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114de5", abiJson);
        DecodedFunctions decodedFunctions = decoder.decodeFunction(testData);

        assertEquals(3, decodedFunctions.getParams().size());

        Object array = decodedFunctions.getParams().getFirst().getValue();

        if (array instanceof List<?> list) {
            assertEquals("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114de5", HEX_PREFIX + Hex.toHexString((byte[]) list.get(0)));
            assertEquals("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114daa", HEX_PREFIX + Hex.toHexString((byte[]) list.get(1)));
        }

        assertEquals("_owners", decodedFunctions.getParams().get(0).getName());
        assertEquals("address[]", decodedFunctions.getParams().get(0).getType());

        assertEquals(BigInteger.ONE, decodedFunctions.getParams().get(1).getValue());
        assertEquals("_required", decodedFunctions.getParams().get(1).getName());
        assertEquals("uint256", decodedFunctions.getParams().get(1).getType());

        assertEquals(BigInteger.ZERO, decodedFunctions.getParams().get(2).getValue());
        assertEquals("_dailyLimit", decodedFunctions.getParams().get(2).getName());
        assertEquals("uint256", decodedFunctions.getParams().get(2).getType());
    }

    @Test
    public void testDecodeFunctionCallUniswap() throws IOException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/uniswap_abi.json")));

        //https://etherscan.io/tx/0xde2b61c91842494ac208e25a2a64d99997c382f6aaf0719d6a719b5cff1f8a07
        /*
         * #	Name	      Type	     Data
         * 0	amountIn	  uint256	 10000000
         * 1	amountOutMin  uint256	 6283178947560620
         * 2	path	      address[]	 0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48
         *                               0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2
         * 3	to	          address	 0xD4CF8e47BeAC55b42Ae58991785Fa326d9384Bd1
         * 4	deadline	uint256	1659426897
         */
        String inputData = "0x18cbafe5000000000000000000000000000000000000000000000000000000000098968000000000000000000000000000000000000000000000000000165284993ac4ac00000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000d4cf8e47beac55b42ae58991785fa326d9384bd10000000000000000000000000000000000000000000000000000000062e8d8510000000000000000000000000000000000000000000000000000000000000002000000000000000000000000a0b86991c6218b36c1d19d4a2e9eb0ce3606eb48000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";
        Decoder decoder = new Decoder();
        decoder.addAbi("0x7a250d5630b4cf539739df2c5dacb4c659f2488d", abiJson);
        DecodedFunctions decodedFunctions = decoder.decodeFunction(inputData);

        assertEquals("swapExactTokensForETH", decodedFunctions.getName());

        List<Param> paramList = decodedFunctions.getParams();

        Param param0 = paramList.getFirst();
        assertEquals("amountIn", param0.getName());
        assertEquals("uint256", param0.getType());
        assertEquals(BigInteger.valueOf(10000000), param0.getValue());

        Param param1 = paramList.get(1);
        assertEquals("amountOutMin", param1.getName());
        assertEquals("uint256", param1.getType());
        assertEquals(new BigInteger("6283178947560620"), param1.getValue());

        Param param2 = paramList.get(2);
        assertEquals("path", param2.getName());
        assertEquals("address[]", param2.getType());
        assertEquals(Arrays.toString(new String[]{"0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48", "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"}), Arrays.toString((Object[]) param2.getValue()));

        Param param3 = paramList.get(3);
        assertEquals("to", param3.getName());
        assertEquals("address", param3.getType());
        assertEquals("0xd4cf8e47beac55b42ae58991785fa326d9384bd1", param3.getValue());

        Param param4 = paramList.get(4);
        assertEquals("deadline", param4.getName());
        assertEquals("uint256", param4.getType());
        assertEquals(BigInteger.valueOf(1659426897), param4.getValue());

        String circleAbiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/circle_usd_abi.json")));

//        0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef( 0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef, 0x000000000000000000000000d4cf8e47beac55b42ae58991785fa326d9384bd1, 0x000000000000000000000000b4e16d0168e52d35cacd2c6185b44281ec28c9dc, 0000000000000000000000000000000000000000000000000000000000989680 )
        decoder.addAbi("0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48", circleAbiJson);
        //method id 0x095ea7b3
        //swapExactTokensForETH(uint256,uint256,address[],address,uint256)
        Log log = new Log();
        List<String> topics = new ArrayList<>();
        topics.add("0x8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925");
        topics.add("0x000000000000000000000000D4CF8e47BeAC55b42Ae58991785Fa326d9384Bd1");
        topics.add("0x000000000000000000000000b4e16d0168e52d35cacd2c6185b44281ec28c9dc");

        log.setTopics(topics);
        log.setAddress("0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48");
        log.setData("0x0000000000000000000000000000000000000000000000000000000000989680");
        List<DecodedLog> decodedLogs = decoder.decodeLogs(new Log[]{log});
        //10000000
        //Transfer (index_topic_1 address from, index_topic_2 address to, uint256 value)
    }


    @Test
    void decodeLogsTest() throws IOException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/log_test_abi.json")));

        Decoder decoder = new Decoder(abiJson);
        List<String> topics = new ArrayList<>();
        topics.add("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");
        topics.add("0x00000000000000000000000066f044b68f2808753b99a7d31820fbc751342d9c");
        topics.add("0x0000000000000000000000004056140bc49cb36dd0b5bc4b4530ef56781cc365");
        Log log = new Log("0x00000000000000000000000000000000000000000000000003aa8e4f70c34000",
                topics,
                "0x4056140BC49cb36dd0b5bC4b4530ef56781Cc365");
        Log[] logs = new Log[]{log};
        List<DecodedLog> result = decoder.decodeLogs(logs);

        assertEquals(result.size(), 1);
        DecodedLog decodedLog = result.getFirst();
        assertEquals("Transfer", decodedLog.getName());
        assertEquals("0x4056140BC49cb36dd0b5bC4b4530ef56781Cc365", decodedLog.getAddress());
        assertEquals("src", decodedLog.getEvents().getFirst().getName());
        assertEquals("dst", decodedLog.getEvents().get(1).getName());
        assertEquals("wad", decodedLog.getEvents().get(2).getName());
    }

    @Test
    public void testDecodeFunctionTupleContainingDynamicTypes() throws IOException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/tuple_test_abi.json")));
        Decoder decoder = new Decoder();
        decoder.addAbi("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef", abiJson);

        // https://testnet.bscscan.com/tx/0x73b80d49777f0c32d45a0a5a7c3487eb9e8da2c93922540c260cdafc3e81a165
        String inputData = "0x005575f20000000000000000000000000000000000000000000000000000000000000080967c9812e5f939318262ccbd023be072015c3ad2f470d47ab5e6b13e1ca810a540274bf9ce7b9da08b0003fc05e67d74e993f3381bf00bcba0fef022bf3b8d6a000000000000000000000000000000000000000000000000000000000000001b000000000000000000000000ddcfc6f09a26413c2b0d6224b29738e74102de04000000000000000000000000cbb869911c0acd242c15a03c42ce3ddcdd82ea1b000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000038d7ea4c68000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000c000000000000000000000000000000000000000000000000000000000000001e4216f62d8000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000001a0000000000000000000000000000000000000000000000000000000000000003b6261666b7265696263796c746f36667974667336746f796f6f716f6b6272366e333566673767683236646e6f3464766a716c6d743464687a34716d0000000000000000000000000000000000000000000000000000000000000000000000003b6261666b726569647533356c64727965703433797574337275616a747936743278346b3773787077737365347572616b7676366d723763657a696d0000000000000000000000000000000000000000000000000000000000000000000000003b6261666b7265696567616e657563727a6e646b6676726a64346a63346235356174646b707475326d6d3779327372783235617068626b623666373400000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000ddcfc6f09a26413c2b0d6224b29738e74102de0400000000000000000000000000000000000000000000000000000000";
        DecodedFunctions decodedFunction = decoder.decodeFunction(inputData);
        decodedFunction.getParams();
//        assertEquals(DecodedFunctions.MULTICALL, decodedFunction.getName());
    }

    @Test
    public void testGetFunctionSignature() {
        String functionSignature = Decoder.getFunctionSignature("0xac9650d80000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000008413ead5620000000000000000000000005c8cd1c2f2997f7a041026cc29de8177b4c6d8ec00000000000000000000000089e54f174ca5ff39cf53ab58004158e2ca012eac0000000000000000000000000000000000000000000000000000000000000bb8000000000000000000000000000000000035f2482336c0d4c2ba6e94faa1d66f000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000164883164560000000000000000000000005c8cd1c2f2997f7a041026cc29de8177b4c6d8ec00000000000000000000000089e54f174ca5ff39cf53ab58004158e2ca012eac0000000000000000000000000000000000000000000000000000000000000bb8fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff2764c00000000000000000000000000000000000000000000000000000000000a11a8000000000000000000000000000000000000000000000000000000e8d4a510000000000000000000000000000000000000000000000a56d35c029fd16645e079000000000000000000000000000000000000000000000000000000e840308c030000000000000000000000000000000000000000000a503344abc0fbe23670910000000000000000000000005a2b5cb4ce921abd65f0c66c2c839894bfc2076c000000000000000000000000000000000000000000000000000000006244356a00000000000000000000000000000000000000000000000000000000");
        assertEquals("0x73b80d49777f0c32d45a0a5a7c3487eb9e8da2c93922540c260cdafc3e81a165", functionSignature);
    }

    @Test
    public void testDecodeMulticall() throws IOException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/multicall_example.json")));
        Decoder sereshForwarderAbi = new Decoder();
        sereshForwarderAbi.addAbi("0x73b80d49777f0c32d45a0a5a7c3487eb9e8da2c93922540c260cdafc3e81a165", abiJson);

        // https://testnet.bscscan.com/tx/0x73b80d49777f0c32d45a0a5a7c3487eb9e8da2c93922540c260cdafc3e81a165
        String inputData = "0xac9650d80000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000008413ead5620000000000000000000000005c8cd1c2f2997f7a041026cc29de8177b4c6d8ec00000000000000000000000089e54f174ca5ff39cf53ab58004158e2ca012eac0000000000000000000000000000000000000000000000000000000000000bb8000000000000000000000000000000000035f2482336c0d4c2ba6e94faa1d66f000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000164883164560000000000000000000000005c8cd1c2f2997f7a041026cc29de8177b4c6d8ec00000000000000000000000089e54f174ca5ff39cf53ab58004158e2ca012eac0000000000000000000000000000000000000000000000000000000000000bb8fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff2764c00000000000000000000000000000000000000000000000000000000000a11a8000000000000000000000000000000000000000000000000000000e8d4a510000000000000000000000000000000000000000000000a56d35c029fd16645e079000000000000000000000000000000000000000000000000000000e840308c030000000000000000000000000000000000000000000a503344abc0fbe23670910000000000000000000000005a2b5cb4ce921abd65f0c66c2c839894bfc2076c000000000000000000000000000000000000000000000000000000006244356a00000000000000000000000000000000000000000000000000000000";
        DecodedFunctions decodedFunctions = sereshForwarderAbi.decodeFunction(inputData);
        assertEquals(DecodedFunctions.MULTICALL, decodedFunctions.getName());

        assertEquals(2, decodedFunctions.getNestedDecodedFunctions().size());
        assertEquals("createAndInitializePoolIfNecessary", decodedFunctions.getNestedDecodedFunctions().getFirst().getName());
        assertEquals("address", decodedFunctions.getNestedDecodedFunctions().getFirst().getParam("token0").getType());
        assertEquals("address", decodedFunctions.getNestedDecodedFunctions().getFirst().getParam("token1").getType());
        assertEquals("uint24", decodedFunctions.getNestedDecodedFunctions().getFirst().getParam("fee").getType());
        assertEquals("uint160", decodedFunctions.getNestedDecodedFunctions().getFirst().getParam("sqrtPriceX96").getType());

        assertEquals("mint", decodedFunctions.getNestedDecodedFunctions().get(1).getName());
        assertEquals("tuple", decodedFunctions.getNestedDecodedFunctions().get(1).getParam("params").getType());

    }

    @Test
    public void testMulticallWithUniswapABI() throws IOException {
        String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/UniswapV3SwapRouter.json")));
        Decoder decoder = new Decoder();
        decoder.addAbi("0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6", abiJson);
        // https://etherscan.io/tx/0x731847de5b19b26039f283826ae5218ac7e070ed1b7fff689c2253a3035d8bd6
        String inputData = "0x5ae401dc0000000000000000000000000000000000000000000000000000000062ed6b0d000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000016000000000000000000000000000000000000000000000000000000000000000e4472b43f3000000000000000000000000000000000000000000000000000008c75ee6fb3900000000000000000000000000000000000000000000000001cb1a1493ed3d4b0000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000020000000000000000000000009bbe10ba8ad02c2a54963b3e2a64f1754c90f411000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004449404b7c00000000000000000000000000000000000000000000000001cb1a1493ed3d4b000000000000000000000000c0da58d88e967d883ef0540db458381e9f5e9c8000000000000000000000000000000000000000000000000000000000";
        DecodedFunctions decodedFunctionCalls = decoder.decodeFunction(inputData);
        decodedFunctionCalls.getName();
    }
    @Test
    void padZerosWithAddressOfCorrectLength() {
        String address = "0x0123456789abcdef0123456789abcdef01234567";
        assertEquals(address, new Decoder().padZeros(address));
    }

    @Test
    void padZerosWithAddressOfIncorrectLength() {
        String address = "0x0123456789abcdef0123456789abcdef01234";
        String expectedPaddedAddress = "0x0000123456789abcdef0123456789abcdef01234";
        String paddedAddress = new Decoder().padZeros(address);
        assertEquals(42, paddedAddress.length());
        assertEquals(expectedPaddedAddress, paddedAddress);
        assertEquals(40, paddedAddress.replaceFirst(HEX_PREFIX, "").length());

    }

    @Test
    void padZerosWithEmptyAddress() {
        String address = "0x";
        String paddedAddress = "0x0000000000000000000000000000000000000000";
        assertEquals(paddedAddress, new Decoder().padZeros(address));
    }

}
