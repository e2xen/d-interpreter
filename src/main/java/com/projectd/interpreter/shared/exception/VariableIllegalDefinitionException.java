package com.projectd.interpreter.shared.exception;

public class VariableIllegalDefinitionException extends SemanticAnalyserException {

    VariableIllegalDefinitionException(String message, int lineNum, int linePos) {
        super(message, lineNum, linePos);
    }
}
