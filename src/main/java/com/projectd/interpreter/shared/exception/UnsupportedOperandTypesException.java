package com.projectd.interpreter.shared.exception;

public class UnsupportedOperandTypesException extends RuntimeInterpreterException {

    UnsupportedOperandTypesException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
