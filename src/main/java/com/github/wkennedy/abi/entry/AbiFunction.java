package com.github.wkennedy.abi.entry;

import com.github.wkennedy.abi.SolidityType;
import com.github.wkennedy.util.ByteUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.github.wkennedy.abi.SolidityType.IntType.encodeInt;
import static java.lang.String.format;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.apache.commons.lang3.StringUtils.join;

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

    public List<?> decode(byte[] encoded) {
        return AbiParam.decodeList(inputs, subarray(encoded, ENCODED_SIGN_LENGTH, encoded.length));
    }

    public List<?> decodeResult(byte[] encoded) {
        return AbiParam.decodeList(outputs, encoded);
    }

    @Override
    public byte[] encodeSignature() {
        return extractSignature(super.encodeSignature());
    }

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
