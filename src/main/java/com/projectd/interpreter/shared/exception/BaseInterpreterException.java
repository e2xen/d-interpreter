package com.projectd.interpreter.shared.exception;

public abstract class BaseInterpreterException extends RuntimeException {
    private int lineNum;
    private int pos;

    BaseInterpreterException(String message, int lineNum, int pos) {
        super(message);
        this.lineNum = lineNum;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return super.toString() +
                " at lineNum=" + (lineNum+1) +
                ", pos=" + (pos+1) +
                ';';
    }
}
