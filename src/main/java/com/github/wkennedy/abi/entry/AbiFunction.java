package com.github.wkennedy.abi.entry;

import com.github.wkennedy.abi.SolidityType;
import com.github.wkennedy.util.ByteUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.github.wkennedy.abi.SolidityType.IntType.encodeInt;
import static com.github.wkennedy.util.Constants.HEX_PREFIX;
import static java.lang.String.format;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * The AbiFunction class represents a function in an ABI (Application Binary Interface)
 * definition. It extends the AbiEntry class and provides methods for encoding and decoding
 * function calls.
 */
public class AbiFunction extends AbiEntry {

    private static final int ENCODED_SIGN_LENGTH = 4;

    public AbiFunction(boolean constant, String name, List<AbiParam> inputs, List<AbiParam> outputs, Boolean payable) {
        super(null, constant, name, inputs, outputs, AbiType.function, payable);
    }

    public byte[] encode(Object... args) {
        return ByteUtil.merge(encodeSignature(), encodeArguments(args));
    }

    private ImmutablePair<Integer, Integer> computeStaticSizeAndDynamicParams(Object... args) {
        int staticSize = 0;
        int dynamicCnt = 0;
        for (int i = 0; i < args.length; i++) {
            SolidityType inputType = inputs.get(i).type;
            if (inputType.isDynamicType()) {
                dynamicCnt++;
            }
            staticSize += inputType.getFixedSize();
        }

        return new ImmutablePair<>(staticSize, dynamicCnt);
    }

    private byte[] encodeArguments(Object... args) {
        if (args.length > inputs.size())
            throw new RuntimeException("Too many arguments: " + args.length + " > " + inputs.size());

        Pair<Integer, Integer> pair = computeStaticSizeAndDynamicParams(args);
        int staticSize = pair.getKey();
        int dynamicCnt = pair.getValue();

        byte[][] encodedArgs = new byte[args.length + dynamicCnt][];

        for (int curDynamicPtr = staticSize, curDynamicCnt = 0, i = 0; i < args.length; i++) {
            Object arg = args[i];
            SolidityType inputType = inputs.get(i).type;
            if (inputType.isDynamicType()) {
                byte[] dynBB = inputType.encode(arg);
                encodedArgs[i] = encodeInt(curDynamicPtr);
                encodedArgs[args.length + curDynamicCnt] = dynBB;
                curDynamicCnt++;
                curDynamicPtr += dynBB.length;
            } else {
                encodedArgs[i] = inputType.encode(arg);
            }
        }

        return ByteUtil.merge(encodedArgs);
    }

    /**
     * Decodes the result of an encoded ABI function call.
     *
     * @param encoded the byte array containing the encoded result
     * @return a List containing the decoded result
     * @throws RuntimeException if the decoding fails
     */
    public List<?> decode(byte[] encoded) {
        return AbiParam.decodeList(inputs, subarray(encoded, ENCODED_SIGN_LENGTH, encoded.length));
    }

    /**
     * Decodes the result of an encoded ABI function call.
     *
     * @param encoded the String containing the hex-encoded result
     * @return a List<?> containing the decoded result
     * @throws RuntimeException if the decoding fails
     */
    public List<?> decodeResult(String encoded) {
        if(encoded == null || encoded.isEmpty()) {
            return new ArrayList<>();
        }

        byte[] bytes;
        try {
            bytes = org.apache.commons.codec.binary.Hex.decodeHex(encoded.replace(HEX_PREFIX, "").toUpperCase());
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        return decodeResult(bytes);
    }

    /**
     * Decodes the result of an encoded ABI function call.
     *
     * @param encoded the byte array containing the encoded result
     * @return a List containing the decoded result
     */
    public List<?> decodeResult(byte[] encoded) {
        return AbiParam.decodeList(outputs, encoded);
    }

    /**
     * Encodes the signature of an AbiFunction.
     *
     * @return a byte array containing the encoded signature
     */
    @Override
    public byte[] encodeSignature() {
        return extractSignature(super.encodeSignature());
    }

    /**
     * Extracts the signature from a given byte array.
     *
     * @param data the byte array to extract the signature from
     * @return a byte array containing the extracted signature
     */
    public static byte[] extractSignature(byte[] data) {
        return subarray(data, 0, ENCODED_SIGN_LENGTH);
    }

    @Override
    public String toString() {
        String returnTail = "";
        if (constant) {
            returnTail += " constant";
        }
        if (!outputs.isEmpty()) {
            List<String> types = new ArrayList<>();
            for (AbiParam output : outputs) {
                types.add(output.type.getCanonicalName());
            }
            returnTail += format(" returns(%s)", join(types, ", "));
        }

        return format("function %s(%s)%s;", name, join(inputs, ", "), returnTail);
    }
}
