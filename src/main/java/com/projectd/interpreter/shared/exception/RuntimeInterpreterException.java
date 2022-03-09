package com.projectd.interpreter.shared.exception;

public abstract class RuntimeInterpreterException extends BaseInterpreterException {

    RuntimeInterpreterException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
