package com.projectd.interpreter.runtime.environment;

import java.util.HashMap;
import java.util.Map;

public class RuntimeScope {

    private final Map<String, RuntimeValue> idValueTable = new HashMap<>();

    public RuntimeValue getValueByIdentifier(String identifier) {
        return idValueTable.get(identifier);
    }

    public void storeValueByIdentifier(String identifier, RuntimeValue value) {
        idValueTable.put(identifier, value);
    }

    public boolean hasIdentifier(String identifier) {
        return idValueTable.containsKey(identifier);
    }
}
