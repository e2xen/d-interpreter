package com.projectd.interpreter.shared.exception;

public class AmbiguousGrammarException extends SyntaxAnalyzerParseException {

    AmbiguousGrammarException(String message, int lineNum, int pos) {
        super(message, lineNum, pos);
    }
}
