package com.projectd.interpreter.shared.exception;

public class MultipleDeclarationException extends SemanticAnalyserException {

    MultipleDeclarationException(String message, int lineNum, int linePos) {
        super(message, lineNum, linePos);
    }
}
