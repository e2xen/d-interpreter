package com.projectd.interpreter.shared.exception;

public class ArgumentsMismatchException extends RuntimeInterpreterException {

    ArgumentsMismatchException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
