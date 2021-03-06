package com.projectd.interpreter.lex.token;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class LexToken {
    protected final LexTokenSpan span;
    protected final LexTokenCode code;


    public LexToken(LexTokenSpan span, LexTokenCode code) {
        this.span = span;
        this.code = code;
    }

    public LexTokenSpan getSpan() {
        return span;
    }

    public LexTokenCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "LexToken{" +
                ", span=" + span +
                ", code=" + code +
                '}';
    }
}
