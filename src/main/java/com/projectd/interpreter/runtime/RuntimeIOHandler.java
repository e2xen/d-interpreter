package com.projectd.interpreter.runtime;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.runtime.environment.RuntimeValue;

import java.util.Arrays;
import java.util.List;

public class RuntimeIOHandler {

    public static RuntimeValue handleRead(LexToken readToken) {
        throw new UnsupportedOperationException();
    }

    public static void handlePrint(List<RuntimeValue> values) {
        StringBuilder out = new StringBuilder();
        values.forEach(v -> {
            out.append(v.toString());
            out.append(' ');
        });
        System.out.println(out.toString());
    }
}
