# JABI-EVM

[![codecov](https://codecov.io/gh/wkennedy/JABI-EVM/graph/badge.svg?token=9BTRNZ7F17)](https://codecov.io/gh/wkennedy/JABI-EVM)

This is a Java library to decode EVM functions and logs based on an ABI. There are other libraries like this one, just 
slightly different.

Installation
```xml
<!--    Add to your pom.xml repositories section-->
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
```
```xml
<!--    Add the JABI-EVM dependency-->
    <dependency>
        <groupId>com.github.wkennedy</groupId>
        <artifactId>JABI-EVM</artifactId>
        <version>1.0.0</version>
    </dependency>
```

Decoding Functions:
```java
    //Get the ABI as JSON (either from a file, web, etc...)
    String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/uniswap_abi.json")));
    
    //Get the raw input data from the transaction. See: https://etherscan.io/tx/0xde2b61c91842494ac208e25a2a64d99997c382f6aaf0719d6a719b5cff1f8a07
    String inputData = "0x18cbafe5000000000000000000000000000000000000000000000000000000000098968000000000000000000000000000000000000000000000000000165284993ac4ac00000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000d4cf8e47beac55b42ae58991785fa326d9384bd10000000000000000000000000000000000000000000000000000000062e8d8510000000000000000000000000000000000000000000000000000000000000002000000000000000000000000a0b86991c6218b36c1d19d4a2e9eb0ce3606eb48000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";
    //Create the Decoder and add the ABI (there are various ways to do this based on your needs. ABIs can be mapped to contract addresses and stored on the Decoder)
    Decoder decoder = new Decoder();
    decoder.addAbi("0x7a250d5630b4cf539739df2c5dacb4c659f2488d", abiJson);
    //Decode the function.
    DecodedFunctions decodedFunctions = decoder.decodeFunction(inputData);
```

Decoding Logs:
```java
    //Get the ABI as JSON (either from a file, web, etc...)
    String abiJson = new String(Files.readAllBytes(Paths.get("./src/test/resources/log_test_abi.json")));

    //Instantiate a Decoder with the ABI
    Decoder decoder = new Decoder(abiJson);
    //Create the logs to decode
    List<String> topics = new ArrayList<>();
    topics.add("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");
    topics.add("0x00000000000000000000000066f044b68f2808753b99a7d31820fbc751342d9c");
    topics.add("0x0000000000000000000000004056140bc49cb36dd0b5bc4b4530ef56781cc365");
    Log log = new Log("0x00000000000000000000000000000000000000000000000003aa8e4f70c34000",
            topics,
            "0x4056140BC49cb36dd0b5bC4b4530ef56781Cc365");
    Log[] logs = new Log[]{log};
    //Decode the logs
    List<DecodedLog> result = decoder.decodeLogs(logs);
```