package com.github.wkennedy.abi.entry;

import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.join;

public class AbiError extends AbiEntry {
    public AbiError(String name, List<AbiParam> inputs) {
        super(null, null, name, inputs, null, AbiType.error, false);
    }

    public List<?> decode(byte[] encoded) {
        return AbiParam.decodeList(inputs, encoded);
    }

    @Override
    public String toString() {
        return format("error %s(%s);", name, join(inputs, ", "));
    }
}
