package com.projectd.interpreter.syntax.exception;

import com.projectd.interpreter.shared.exception.BaseInterpreterException;

public class UnbalancedBracketsException extends BaseInterpreterException {
    public UnbalancedBracketsException(String message) {
        super(message);
    }
}
