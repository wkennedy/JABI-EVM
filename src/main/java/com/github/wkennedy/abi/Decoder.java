package com.github.wkennedy.abi;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wkennedy.abi.entry.AbiEntry;
import com.github.wkennedy.abi.entry.AbiFunction;
import com.github.wkennedy.abi.entry.AbiParam;
import com.github.wkennedy.abi.models.DecodedLog;
import com.github.wkennedy.abi.models.DecodedFunctions;
import com.github.wkennedy.abi.models.Log;
import com.github.wkennedy.abi.models.Param;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.web3j.crypto.Hash;

import static com.github.wkennedy.util.Constants.HEX_PREFIX;

/**
 * The Decoder class represents a decoder that is used to decode function calls and logs based on ABIs.
 */
public class Decoder {
    private static final int FORMATTED_STRING_SIZE = 40;

    private final Map<String, Abi> abiCache = new HashMap<>();
    private final HashMap<String, AbiEntry> abiEntriesByMethodId = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Decoder() {
    }

    /**
     * The Decoder class provides methods for decoding function calls and logs based on the provided ABI.
     *
     * @param json The JSON string representing the ABI.
     */
    public Decoder(String json) {
        addAbi(json);
    }

    /**
     * The Decoder class provides methods for decoding function calls and logs based on the provided ABI.
     *
     * @param address The address associated with the ABI.
     * @param json The JSON string representing the ABI.
     * */
    public Decoder(String address, String json) {
        addAbi(address, json);
    }

    /**
     * Adds an ABI to the decoder cache using the given address and JSON.
     *
     * @param json The JSON string representing the ABI.
     */
    @SuppressWarnings("unused")
    public void addAbi(String json) {
        String abiHash = Hash.sha3(Hex.encodeHexString(json.getBytes()));
        addAbi(abiHash, json);
    }

    /**
     * Adds an ABI to the decoder cache using the given address and JSON.
     *
     * @param address The address associated with the ABI.
     * @param json The JSON string representing the ABI.
     */
    public void addAbi(String address, String json) {
        Optional<Abi> potentialAbi = Abi.fromJson(json);
        potentialAbi.ifPresent(abi -> {
            abi.forEach(entry -> {
                if (entry != null) {
                    if (entry.name != null) {
                        byte[] methodSignature = entry.encodeSignature();
                        String hexSig = Hex.encodeHexString(methodSignature);
                        abiEntriesByMethodId.put(hexSig, entry);
                    }
                }
            });
            abiCache.put(address, abi);
        });
    }

    /**
     * Retrieves the ABI cache.
     *
     * @return The ABI cache as a Map, where the key is a String representing the address and the value is an Abi object.
     */
    public Map<String, Abi> getAbis() {
        return abiCache;
    }

    /**
     * Retrieves the methodIDs.
     *
     * @return A map of methodIDs, where the key is a String representing the methodID and the value is an AbiEntry object.
     */
    public Map<String, AbiEntry> getMethodIDs() {
        return abiEntriesByMethodId;
    }

    /**
     * Decodes a function from the given byte array data.
     *
     * @param data The byte array data containing the function.
     * @return The decoded function represented by a DecodedFunctions object, or null if the function cannot be decoded.
     */
    public DecodedFunctions decodeFunction(byte[] data) {
        byte[] methodBytes = Arrays.copyOf(data, 4);
        String methodId = Hex.encodeHexString(methodBytes);
        AbiEntry abiEntry = abiEntriesByMethodId.get(methodId);
        if (abiEntry instanceof AbiFunction) {
            DecodedFunctions decodedFunction = decodeAbiFunction(data, (AbiFunction) abiEntry);

            if (decodedFunction.isMulticall()) {
                Object paramValue = decodedFunction.getParam("data").getValue();

                handleParamValue(decodedFunction, paramValue);
            }
            return decodedFunction;
        }

        return null;
    }

    private void handleParamValue(DecodedFunctions decodedFunction, Object paramValue) {
        if (paramValue instanceof String) {
            decodedFunction.addNestedDecodedFunction(decodeFunction((String) paramValue));
        } else if (paramValue instanceof byte[]) {
            decodedFunction.addNestedDecodedFunction(decodeFunction(org.bouncycastle.util.encoders.Hex.toHexString((byte[]) paramValue)));
        } else if (paramValue instanceof Object[] singleCallInputDataArray) {
            for (Object singleCallInputData : singleCallInputDataArray) {
                handleCallInputData(decodedFunction, singleCallInputData);
            }
        }
    }

    private void handleCallInputData(DecodedFunctions decodedFunction, Object singleCallInputData) {
        if (singleCallInputData instanceof String) {
            DecodedFunctions call = decodeFunction((String) singleCallInputData);
            if (call != null) decodedFunction.addNestedDecodedFunction(call);
        } else if (singleCallInputData instanceof byte[]) {
            DecodedFunctions call = decodeFunction((byte[]) singleCallInputData);
            if (call != null) decodedFunction.addNestedDecodedFunction(call);
        }
    }

    /**
     * Decodes a function from the given data string.
     *
     * @param data The data string containing the function.
     * @return The decoded function represented by a DecodedFunctions object.
     * @throws RuntimeException If there is an error decoding the function.
     */
    public DecodedFunctions decodeFunction(String data) {
        String noPrefix = data.replaceFirst(HEX_PREFIX, "");
        byte[] decodedDataInBytes;
        try {
            decodedDataInBytes = Hex.decodeHex(noPrefix.toUpperCase());
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }

        return decodeFunction(decodedDataInBytes);
    }

    private DecodedFunctions decodeAbiFunction(byte[] decodedDataInBytes, AbiFunction abiFunctionEntry) {
        List<?> decoded = abiFunctionEntry.decode(decodedDataInBytes);
        List<Param> params = extractParamsFromDecodedABI(decoded, abiFunctionEntry.inputs);
        return new DecodedFunctions(abiFunctionEntry.name, params);
    }

    private List<Param> extractParamsFromDecodedABI(List<?> decoded, List<AbiParam> inputs) {
        List<Param> params = new ArrayList<>();
        for (int index = 0; index < decoded.size(); index++) {
            String paramName = inputs.get(index).name;
            String paramType = inputs.get(index).type.toString();
            Object paramValue = decoded.get(index);
            Param param = new Param(paramName, paramType, paramValue);
            params.add(param);
        }
        return params;
    }

    /**
     * Pads zeros to the given address.
     * @param address The address to pad zeros to.
     * @return The padded address with zeros.
     */
    @SuppressWarnings("unused")
    public String padZeros(String address) {
        StringBuilder formatted = new StringBuilder(address.substring(2));
        for (int i = formatted.length(); i < FORMATTED_STRING_SIZE; ++i) {
            formatted.insert(0, "0");
        }
        return HEX_PREFIX + formatted;
    }

    /**
     * Decodes logs from the given Log array and returns a list of DecodedLog objects representing the decoded logs.
     *
     * @param logs The array of Log objects to decode.
     * @return A list of DecodedLog objects representing the decoded logs.
     */
    public List<DecodedLog> decodeLogs(Log[] logs) {
        List<DecodedLog> result = new ArrayList<>();

        for (Log log : logs) {
            var entry = getAbiEntryByMethodId(log);
            if (entry != null) {
                List<Param> decodedParams = generateDecodedParams(log, entry);
                result.add(new DecodedLog(entry.name, log.getAddress(), decodedParams));
            }
        }
        return result;
    }

    /**
     * Decodes logs from the given data string.
     *
     * @param data The data string containing the logs.
     * @return A list of DecodedLog objects representing the decoded logs.
     */
    public List<DecodedLog> decodeLogs(String data) {
        Log[] logs = fromJson(data);
        return decodeLogs(logs);
    }

    private Log[] fromJson(String data) {
        try {
            return objectMapper.readValue(data, Log[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getDecodedBytes(String str) {
        try {
            return Hex.decodeHex(str.replace(HEX_PREFIX, "").toUpperCase());
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    private AbiEntry getAbiEntryByMethodId(Log log) {
        byte[] methodBytes = getDecodedBytes(log.getTopics().getFirst());
        var methodIdHex = Hex.encodeHexString(methodBytes);
        return abiEntriesByMethodId.get(methodIdHex);
    }

    private List<Param> generateDecodedParams(Log log, AbiEntry entry) {
        List<AbiParam> nonIndexedInputs = entry.inputs.stream()
                .filter(input -> !input.indexed)
                .collect(Collectors.toList());
        byte[] bytes = getDecodedBytes(log.getData());

        List<Param> decodedParams = new ArrayList<>();
        int dataIndex = 0;
        int topicsIndex = 1;
        for (AbiParam input : entry.inputs) {
            List<?> decoded;
            if (input.indexed) {
                byte[] topicBytes = getDecodedBytes(log.getTopics().get(topicsIndex));
                decoded = AbiParam.decodeList(Collections.singletonList(input), topicBytes);
                decodedParams.add(new Param(input.name, input.type.toString(), decoded.getFirst()));
                topicsIndex++;
            } else {
                decoded = AbiParam.decodeList(nonIndexedInputs, bytes);
                Object value = input.indexed ? decoded.getFirst() : decoded.get(dataIndex);
                decodedParams.add(new Param(input.name, input.type.toString(), value));
                dataIndex++;
            }
        }
        return decodedParams;
    }
}
