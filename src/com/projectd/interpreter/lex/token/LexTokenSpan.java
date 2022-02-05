package com.projectd.interpreter.lex.token;

public class LexTokenSpan {
    final int lineNum;
    final int pos;

    public LexTokenSpan(int lineNum, int pos) {
        this.lineNum = lineNum;
        this.pos = pos;
    }

    public static LexTokenSpan of(int lineNum, int pos) {
        return new LexTokenSpan(lineNum, pos);
    }
}
