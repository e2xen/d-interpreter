package com.projectd.interpreter.shared.exception;


public class UnexpectedTokenException extends SyntaxAnalyzerParseException {

    UnexpectedTokenException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
