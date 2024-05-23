package com.github.wkennedy.abi.entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.wkennedy.abi.Abi;
import com.github.wkennedy.abi.SolidityType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.wkennedy.abi.SolidityType.IntType.decodeInt;
import static java.lang.String.format;

@JsonDeserialize(converter = Abi.ParamSanitizer.class)  // invoked after class is fully deserialized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbiParam {
    public Boolean indexed;
    public String name;
    public SolidityType type;
    public List<Component> components;

    @SuppressWarnings("unused")
    public AbiParam() {
    }

    public AbiParam(Boolean indexed, String name, SolidityType type) {
        this.indexed = indexed;
        this.name = name;
        this.type = type;
    }

    public static List<?> decodeList(List<AbiParam> params, byte[] encoded) {
        List<Object> result = new ArrayList<>(params.size());

        int offset = 0;
        for (AbiParam param : params) {
            Object decoded = param.type.isDynamicType()
                    ? param.type.decode(encoded, decodeInt(encoded, offset).intValue())
                    : param.type.decode(encoded, offset);
            result.add(decoded);

            offset += param.type.getFixedSize();
        }

        return result;
    }

    @Override
    public String toString() {
        return format("%s%s%s", type.getCanonicalName(), (indexed != null && indexed) ? " indexed " : " ", name);
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public String getTypeDefinition() {
        if (type instanceof SolidityType.TupleType) {
            return "(" + components.stream()
                    .map(Component::getType)
                    .map(SolidityType::getCanonicalName)
                    .collect(Collectors.joining(","))
                    + ")";
        }
        return type.getCanonicalName();
    }

    public static class Component {
        private String name;
        private SolidityType type;

        public Component() {
        }

        public Component(String name, SolidityType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SolidityType getType() {
            return type;
        }

        public void setType(SolidityType type) {
            this.type = type;
        }
    }
}
