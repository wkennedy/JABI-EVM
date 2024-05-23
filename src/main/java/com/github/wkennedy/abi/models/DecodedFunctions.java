package com.github.wkennedy.abi.models;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DecodedFunctions {
    public static final String MULTICALL = "multicall";

    private String name;
    private List<Param> params;
    private List<DecodedFunctions> nestedDecodedFunctions;

    public DecodedFunctions() {
    }

    public DecodedFunctions(String name, List<Param> params) {
        this.name = name;
        this.params = params;
        nestedDecodedFunctions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public boolean isMulticall() {
        return MULTICALL.equalsIgnoreCase(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public Param getParam(String name) {
        return params.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public List<DecodedFunctions> getNestedDecodedFunctions() {
        return nestedDecodedFunctions;
    }

    public void setNestedDecodedFunctions(List<DecodedFunctions> nestedDecodedFunctions) {
        this.nestedDecodedFunctions = nestedDecodedFunctions;
    }

    public void addNestedDecodedFunction(DecodedFunctions decodedFunction) {
        this.nestedDecodedFunctions.add(decodedFunction);
    }

    @Override
    public String toString() {
        return "DecodedMethod{" +
                "name='" + name + '\'' +
                ", params=" + params +
                '}';
    }
}
