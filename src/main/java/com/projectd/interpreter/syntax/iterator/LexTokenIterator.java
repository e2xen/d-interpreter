package com.projectd.interpreter.syntax.iterator;

import com.projectd.interpreter.lex.token.LexToken;

import java.util.*;

public class LexTokenIterator implements RollbackableIterator<LexToken> {

    private final List<LexToken> tokens;
    private int cursor = 0;
    private final Stack<Integer> cursorStack = new Stack<>();

    public LexTokenIterator(List<LexToken> tokens) {
        this.tokens = List.copyOf(tokens);
    }

    @Override
    public boolean hasNext() {
        return this.tokens.size() > cursor;
    }

    @Override
    public LexToken next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return tokens.get(cursor++);
    }

    @Override
    public void checkpoint() {
        cursorStack.push(cursor);
    }

    public void discardCheckpoint() {
        if (cursorStack.empty()) {
            throw new IllegalStateException("No checkpoints to discard");
        }
        cursorStack.pop();
    }

    @Override
    public void rollback() {
        if (cursorStack.empty()) {
            throw new IllegalStateException("No more checkpoints to rollback to");
        }
        cursor = cursorStack.pop();
    }

//    public LexToken ensureNext(Set<LexTokenCode> expectedTokens, LexTokenSpan currentSpan) {
//        if (!hasNext()) {
//            throw ExceptionBuilder.noToken(expectedTokens, currentSpan.getLineNum(), currentSpan.getPos());
//        }
//        LexToken next = innerIterator.next();
//        if (!expectedTokens.contains(next.getCode())) {
//            LexTokenSpan nextSpan = next.getSpan();
//            throw ExceptionBuilder.unexpectedToken(expectedTokens, next.getCode(), nextSpan.getLineNum(), nextSpan.getPos());
//        }
//        return next;
//    }
}
