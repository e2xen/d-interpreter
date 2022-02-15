package com.projectd.interpreter.shared.exception;

import com.projectd.interpreter.shared.exception.BaseInterpreterException;

public abstract class SyntaxAnalyzerParseException extends BaseInterpreterException {

    SyntaxAnalyzerParseException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
