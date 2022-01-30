package com.projectd.interpreter.lex.token;

public class LexIdentifierToken extends LexToken {
    private final String identifier;


    public LexIdentifierToken(String identifier, LexTokenSpan span) {
        super(span, LexTokenCode.IDENTIFIER);
        this.identifier = identifier;
    }
}
