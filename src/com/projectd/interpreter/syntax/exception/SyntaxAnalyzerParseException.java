package com.projectd.interpreter.syntax.exception;

import com.projectd.interpreter.shared.exception.BaseInterpreterException;

public class SyntaxAnalyzerParseException extends BaseInterpreterException {

    public SyntaxAnalyzerParseException(String message) {
        super(message);
    }
}
