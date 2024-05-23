package com.github.wkennedy.abi.entry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.web3j.crypto.Hash;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbiEntry {

    public final Boolean anonymous;
    public final Boolean constant;
    public final String name;
    public final List<AbiParam> inputs;
    public final List<AbiParam> outputs;
    public final AbiType type;
    public final Boolean payable;

    public AbiEntry(Boolean anonymous, Boolean constant, String name, List<AbiParam> inputs, List<AbiParam> outputs, AbiType type, Boolean payable) {
        this.anonymous = anonymous;
        this.constant = constant;
        this.name = name;
        this.inputs = inputs;
        this.outputs = outputs;
        this.type = type;
        this.payable = payable;
    }

    public String formatSignature() {
        return String.format(
                "%s(%s)",
                name,
                inputs.stream()
                        .map(AbiParam::getTypeDefinition)
                        .collect(Collectors.joining(","))
        );
    }

    public byte[] fingerprintSignature() {
        return Hash.sha3(formatSignature().getBytes());
    }

    public byte[] encodeSignature() {
        return fingerprintSignature();
    }

    @JsonCreator
    public static AbiEntry create(@JsonProperty("anonymous") boolean anonymous,
                                  @JsonProperty("constant") boolean constant,
                                  @JsonProperty("name") String name,
                                  @JsonProperty("inputs") List<AbiParam> inputs,
                                  @JsonProperty("outputs") List<AbiParam> outputs,
                                  @JsonProperty("type") AbiType type,
                                  @JsonProperty(value = "payable", defaultValue = "false") Boolean payable) {

        return switch (type) {
            case constructor -> new AbiConstructor(inputs, outputs);
            case function, fallback, receive -> new AbiFunction(constant, name, inputs, outputs, payable);
            case event -> new AbiEvent(anonymous, name, inputs, outputs);
            case error -> new AbiError(name, inputs);
        };
    }
}
