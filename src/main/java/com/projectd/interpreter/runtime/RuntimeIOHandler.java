package com.projectd.interpreter.runtime;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.runtime.environment.RuntimeValue;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RuntimeIOHandler {

    public static RuntimeValue handleRead(LexToken readToken) {
        Scanner sc = new Scanner(System.in);
        switch (readToken.getCode()) {
            case READ_INT -> {
                return RuntimeValue.ofValue(sc.nextInt());
            }
            case READ_REAL -> {
                return RuntimeValue.ofValue(sc.nextDouble());
            }
            case READ_STRING -> {
                return RuntimeValue.ofValue(sc.nextLine());
            }
            default -> throw new IllegalStateException();
        }
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
