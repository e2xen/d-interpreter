package com.projectd.interpreter.shared.exception;

import com.projectd.interpreter.lex.token.LexTokenSpan;
import com.projectd.interpreter.runtime.environment.RuntimeValue;

import java.util.List;
import java.util.stream.Collectors;

public class RuntimeExceptionFactory {

    public static UndefinedVariableException undefinedVariable(String identifier, LexTokenSpan span) {
        String message = String.format("Variable %s is not defined", identifier);
        return new UndefinedVariableException(message, span.getLineNum(), span.getPos());
    }

    public static UnsupportedOperandTypesException invalidOperandTypes(String op, List<RuntimeValue.RuntimeValueType> providedTypes, LexTokenSpan span) {
        List<String> types = providedTypes.stream().map(RuntimeValue.RuntimeValueType::toString).collect(Collectors.toList());
        String message = String.format("Operation <%s> does not support provided types: %s", op, types.toString());
        return new UnsupportedOperandTypesException(message, span.getLineNum(), span.getPos());
    }

    public static AlreadyDefinedVariableException alreadyDefined(String identifier, LexTokenSpan span) {
        String message = String.format("Variable %s is already defined in the current scope", identifier);
        return new AlreadyDefinedVariableException(message, span.getLineNum(), span.getPos());
    }

    public static GenericRuntimeException generic(String message, LexTokenSpan span) {
        return new GenericRuntimeException(message, span.getLineNum(), span.getPos());
    }

    public static ImmutableObjectException immutableObject(RuntimeValue.RuntimeValueType object, LexTokenSpan span) {
        String message = String.format("Object of %s can't be modified", object.toString());
        return new ImmutableObjectException(message, span.getLineNum(), span.getPos());
    }
}
