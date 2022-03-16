package com.projectd.interpreter.shared.exception;

public class ImmutableObjectException extends RuntimeInterpreterException {

    ImmutableObjectException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
