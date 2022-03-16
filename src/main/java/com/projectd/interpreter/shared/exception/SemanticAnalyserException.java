package com.projectd.interpreter.shared.exception;

public abstract class SemanticAnalyserException extends BaseInterpreterException {

    SemanticAnalyserException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
