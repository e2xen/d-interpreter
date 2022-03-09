package com.projectd.interpreter.shared.exception;

public class UndefinedVariableException extends RuntimeInterpreterException {

    UndefinedVariableException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
