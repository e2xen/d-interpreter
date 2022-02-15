package com.projectd.interpreter.shared.exception;

public abstract class BaseInterpreterException extends RuntimeException {
    private final int lineNum;
    private final int pos;

    BaseInterpreterException(String message, int lineNum, int pos) {
        super(message);
        this.lineNum = lineNum;
        this.pos = pos;
    }
}
