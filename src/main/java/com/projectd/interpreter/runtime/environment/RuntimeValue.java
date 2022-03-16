package com.projectd.interpreter.runtime.environment;

import com.projectd.interpreter.lex.token.LexLiteralToken;

public class RuntimeValue {

    private final RuntimeValueType type;
    private final Object value;

    private RuntimeValue(RuntimeValueType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public RuntimeValueType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public static RuntimeValue empty() {
        return new RuntimeValue(RuntimeValueType.EMPTY, null);
    }

    public static RuntimeValue ofValue(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot construct RuntimeValue from null, for EMPTY use static empty() method");
        }
        if (obj instanceof Boolean bool) {
            return new RuntimeValue(RuntimeValueType.BOOLEAN, bool);
        } else if (obj instanceof Integer integer) {
            return new RuntimeValue(RuntimeValueType.INTEGER, integer);
        } else if (obj instanceof Double doubl) {
            return new RuntimeValue(RuntimeValueType.REAL, doubl);
        } else if (obj instanceof String str) {
            return new RuntimeValue(RuntimeValueType.STRING, str);
        } else if (obj instanceof SparseArray arr) {
            return new RuntimeValue(RuntimeValueType.ARRAY, arr);
        } else if (obj instanceof ImmutableTuple tuple) {
            return new RuntimeValue(RuntimeValueType.TUPLE, tuple);
        } else if (obj instanceof RuntimeFunction func) {
            return new RuntimeValue(RuntimeValueType.FUNCTION, func);
        } else {
            throw new IllegalArgumentException("Cannot construct RuntimeValue from object " + obj.toString());
        }
    }

    public static RuntimeValue ofLiteral(LexLiteralToken token) {
        RuntimeValueType typ;
        switch (token.getType()) {
            case INT -> typ = RuntimeValueType.INTEGER;
            case REAL -> typ = RuntimeValueType.REAL;
            case STRING -> typ = RuntimeValueType.STRING;
            case BOOLEAN -> typ = RuntimeValueType.BOOLEAN;
            default -> throw new IllegalStateException();
        }
        return new RuntimeValue(typ, token.getValue());
    }

    public enum RuntimeValueType {
        EMPTY,
        INTEGER,
        REAL,
        STRING,
        BOOLEAN,
        ARRAY,
        TUPLE,
        FUNCTION
    }
}
