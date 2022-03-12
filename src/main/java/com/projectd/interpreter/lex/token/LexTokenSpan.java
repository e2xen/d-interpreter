package com.projectd.interpreter.lex.token;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class LexTokenSpan {
    final int lineNum;
    final int pos;

    protected LexTokenSpan(int lineNum, int pos) {
        this.lineNum = lineNum;
        this.pos = pos;
    }

    public static LexTokenSpan of(int lineNum, int pos) {
        return new LexTokenSpan(lineNum, pos);
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return "LexTokenSpan{" +
                "lineNum=" + lineNum +
                ", pos=" + pos +
                '}';
    }
}
