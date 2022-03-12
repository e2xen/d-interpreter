package com.projectd.interpreter.shared.exception;

public class LexBadIdentifierException extends LexicalAnalyserParseException {

    LexBadIdentifierException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
