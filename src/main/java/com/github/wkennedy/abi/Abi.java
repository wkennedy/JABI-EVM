package com.github.wkennedy.abi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.github.wkennedy.abi.entry.*;
import org.apache.commons.collections4.Predicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * The Abi class represents an Ethereum ABI (Application Binary Interface) definition.
 * It extends the ArrayList class to provide a list-like behavior for AbiEntry objects.
 */
public class Abi extends ArrayList<AbiEntry> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

    private static final AbiType FUNCTION_TYPE = AbiType.function;
    private static final AbiType EVENT_TYPE = AbiType.event;
    private static final AbiType CONSTRUCTOR_TYPE = AbiType.constructor;

    /**
     * Converts the given JSON string to an Abi object.
     *
     * @param json the JSON string to convert
     * @return an Optional object containing the converted Abi object, or an empty Optional if conversion fails
     */
    public static Optional<Abi> fromJson(String json) {
        try {
            return Optional.of(OBJECT_MAPPER.readValue(json, Abi.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Converts the object to its JSON representation.
     *
     * @return the JSON representation of the object
     * @throws RuntimeException if there is an error during JSON serialization
     */
    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbiEntry> Optional<T> find(Class<T> resultClass, final AbiType type, final Predicate<T> searchPredicate) {
        return (Optional<T>) this.stream().filter(entry -> entry.type == type && resultClass.isInstance(entry) &&
                searchPredicate.evaluate(resultClass.cast(entry))).findFirst();
    }

    /**
     * Finds an AbiFunction based on the given search predicate.
     *
     * @param searchPredicate the predicate to filter and match the AbiFunction
     * @return an Optional containing the found AbiFunction, or an empty Optional if not found
     */
    public Optional<AbiFunction> findFunction(Predicate<AbiFunction> searchPredicate) {
        return find(AbiFunction.class, FUNCTION_TYPE, searchPredicate);
    }

    /**
     * Finds an AbiEvent that satisfies the given search predicate.
     *
     * @param searchPredicate the predicate used to search for the AbiEvent
     * @return an Optional object that contains the found AbiEvent, or an empty Optional if not found
     */
    public Optional<AbiEvent> findEvent(Predicate<AbiEvent> searchPredicate) {
        return find(AbiEvent.class, EVENT_TYPE, searchPredicate);
    }

    /**
     * Finds the first occurrence of AbiConstructor in the list of AbiEntry objects.
     *
     * @return An Optional object containing the found AbiConstructor, or an empty Optional if not found.
     */
    public Optional<AbiConstructor> findConstructor() {
        return find(AbiConstructor.class, CONSTRUCTOR_TYPE, AbiConstructor.class::isInstance);
    }


    /**
     * The ParamSanitizer class is a converter class that sanitizes the AbiParam objects.
     * It extends the StdConverter class from the Jackson library.
     * This class is part of the Abi class and is used to convert AbiParam objects before or after deserialization.
     */
    public static class ParamSanitizer extends StdConverter<AbiParam, AbiParam> {
        public ParamSanitizer() {
        }

        @Override
        public AbiParam convert(AbiParam param) {
            if (param.type instanceof SolidityType.TupleType) {
                for (AbiParam.Component c : param.components) {
                    ((SolidityType.TupleType) param.type).types.add(c.getType());
                }
            }
            return param;
        }
    }

    @Override
    public String toString() {
        return toJson();
    }
}
