package com.projectd.interpreter.shared.exception;

public class AlreadyDefinedVariableException extends RuntimeInterpreterException {

    AlreadyDefinedVariableException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
