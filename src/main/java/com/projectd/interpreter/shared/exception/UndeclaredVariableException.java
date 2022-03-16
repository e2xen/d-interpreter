package com.projectd.interpreter.shared.exception;

public class UndeclaredVariableException extends SemanticAnalyserException {

    UndeclaredVariableException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
