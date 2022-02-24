package com.projectd.interpreter.shared.exception;

public class NoTokenException extends SyntaxAnalyzerParseException {

    NoTokenException(String message) {
        super(message, 0, 0);
    }
}
