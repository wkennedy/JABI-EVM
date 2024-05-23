package com.github.wkennedy.abi.entry;

import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.join;

public class AbiConstructor extends AbiEntry {

    public AbiConstructor(List<AbiParam> inputs, List<AbiParam> outputs) {
        super(null, null, "", inputs, outputs, AbiType.constructor, false);
    }

    public List<?> decode(byte[] encoded) {
        return AbiParam.decodeList(inputs, encoded);
    }

    public String formatSignature(String contractName) {
        return format("function %s(%s)", contractName, join(inputs, ", "));
    }
}
