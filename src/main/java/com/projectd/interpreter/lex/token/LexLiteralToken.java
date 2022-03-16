package com.projectd.interpreter.lex.token;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class LexLiteralToken extends LexToken {

    private final LexLiteralTokenType type;
    private final Object value;


    private LexLiteralToken(LexTokenSpan span, LexTokenCode code, LexLiteralTokenType type, Object value) {
        super(span, code);
        this.type = type;
        this.value = value;
    }

    public static LexLiteralToken ofValue(int value, LexTokenSpan span) {
        return new LexLiteralToken(span, LexTokenCode.LITERAL, LexLiteralTokenType.INT, value);
    }

    public static LexLiteralToken ofValue(double value, LexTokenSpan span) {
        return new LexLiteralToken(span, LexTokenCode.LITERAL, LexLiteralTokenType.REAL, value);
    }

    public static LexLiteralToken ofValue(String value, LexTokenSpan span) {
        return new LexLiteralToken(span, LexTokenCode.LITERAL, LexLiteralTokenType.STRING, value);
    }

    public static LexLiteralToken ofValue(boolean value, LexTokenSpan span) {
        return new LexLiteralToken(span, LexTokenCode.LITERAL, LexLiteralTokenType.BOOLEAN, value);
    }

    @Override
    public String toString() {
        return "LexLiteralToken{" +
                "type=" + type +
                ", value=" + value +
                ", span=" + span +
                ", code=" + code +
                '}';
    }

    // TODO: implement value retrieval

    public LexLiteralTokenType getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
}
