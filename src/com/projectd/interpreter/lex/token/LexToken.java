package com.projectd.interpreter.lex.token;

public class LexToken {
    protected final LexTokenSpan span;
    protected final LexTokenCode code;


    public LexToken(LexTokenSpan span, LexTokenCode code) {
        this.span = span;
        this.code = code;
    }

    @Override
    public String toString() {
        return "LexToken{" +
                "span=" + span +
                ", code=" + code +
                '}';
    }
}
