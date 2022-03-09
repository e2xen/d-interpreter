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
}
