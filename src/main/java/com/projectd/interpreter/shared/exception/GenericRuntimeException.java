package com.projectd.interpreter.shared.exception;

public class GenericRuntimeException extends RuntimeInterpreterException {

    GenericRuntimeException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
