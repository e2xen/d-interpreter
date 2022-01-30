package com.projectd.interpreter.lex.token;

import java.util.Map;
import java.util.stream.Collectors;

import static com.projectd.interpreter.lex.token.LexTokenCode.*;

public class LexTokenBinder {
    private static final Map<LexTokenCode, String> BINDING_TABLE = Map.ofEntries(
            Map.entry(IF, "if"),
            Map.entry(THEN, "then"),
            Map.entry(ELSE, "else"),
            Map.entry(END, "end"),
            Map.entry(VAR, "var"),
            Map.entry(WHILE, "while"),
            Map.entry(LOOP, "loop"),
            Map.entry(FOR, "for"),
            Map.entry(IN, "in"),
            Map.entry(PRINT, "print"),
            Map.entry(OR, "or"),
            Map.entry(AND, "and"),
            Map.entry(XOR, "xor")
            // TODO: list all
    );

    private static final Map<String, LexTokenCode> REVERSE_BINDING_TABLE =
            BINDING_TABLE.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    // TODO: improve to work with identifiers and literals?
    public static LexTokenCode bindToken(String token) {
        return REVERSE_BINDING_TABLE.getOrDefault(token, null);
    }
}
