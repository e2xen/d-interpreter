package com.projectd.interpreter.shared.exception;

public class EmptyFunctionException extends RuntimeInterpreterException {

    EmptyFunctionException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
