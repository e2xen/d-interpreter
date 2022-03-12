package com.projectd.interpreter.lex.token;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class LexIdentifierToken extends LexToken {
    private final String identifier;


    public LexIdentifierToken(String identifier, LexTokenSpan span) {
        super(span, LexTokenCode.IDENTIFIER);
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "LexIdentifierToken{" +
                "identifier='" + identifier + '\'' +
                "span=" + span +
                ", code=" + code +
                '}';
    }

    public String getIdentifier() {
        return identifier;
    }
}
