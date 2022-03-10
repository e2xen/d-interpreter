package com.projectd.interpreter.shared.exception;

public abstract class LexicalAnalyserParseException extends BaseInterpreterException {

    LexicalAnalyserParseException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
