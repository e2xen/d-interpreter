package com.projectd.interpreter.shared.exception;

public class IllegalOverridingException extends SemanticAnalyserException {

    IllegalOverridingException(String message, int lineNum, int linePos) {
        super(message, lineNum, linePos);
    }
}
