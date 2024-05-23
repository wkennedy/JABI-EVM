package com.github.wkennedy.abi.entry;

import com.github.wkennedy.abi.SolidityType;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.collections4.ListUtils.select;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.apache.commons.lang3.StringUtils.join;

public class AbiEvent extends AbiEntry {

    public AbiEvent(boolean anonymous, String name, List<AbiParam> inputs, List<AbiParam> outputs) {
        super(anonymous, null, name, inputs, outputs, AbiType.event, false);
    }

    public List<?> decode(byte[] data, byte[][] topics) {
        byte[][] argTopics = anonymous ? topics : subarray(topics, 1, topics.length);
        List<Object> indexed = decodeIndexedArguments(argTopics);
        List<?> notIndexed = AbiParam.decodeList(filteredInputs(false), data);

        List<Object> result = new ArrayList<>(inputs.size());
        for (AbiParam input : inputs) {
            result.add(input.indexed ? indexed.removeFirst() : notIndexed.removeFirst());
        }
        return result;
    }

    private List<Object> decodeIndexedArguments(byte[][] argTopics) {
        List<AbiParam> indexedParams = filteredInputs(true);
        List<Object> indexed = new ArrayList<>();
        for (int i = 0; i < indexedParams.size(); i++) {
            indexed.add(decodeIndexedArgument(indexedParams.get(i), argTopics[i]));
        }
        return indexed;
    }

    private Object decodeIndexedArgument(AbiParam indexedParam, byte[] argTopic) {
        if (indexedParam.type.isDynamicType()) {
            return SolidityType.Bytes32Type.decodeBytes32(argTopic, 0);
        } else {
            return indexedParam.type.decode(argTopic);
        }
    }
    private List<AbiParam> filteredInputs(final boolean indexed) {
        return select(inputs, param -> param.indexed == indexed);
    }

    @Override
    public String toString() {
        return format("event %s(%s);", name, join(inputs, ", "));
    }
}
