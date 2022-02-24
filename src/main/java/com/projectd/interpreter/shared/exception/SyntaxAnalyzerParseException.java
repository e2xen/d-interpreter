package com.projectd.interpreter.shared.exception;

public abstract class SyntaxAnalyzerParseException extends BaseInterpreterException {

    SyntaxAnalyzerParseException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
