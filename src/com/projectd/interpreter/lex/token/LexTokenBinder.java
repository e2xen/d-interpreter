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
            Map.entry(XOR, "xor"),
            Map.entry(IS, "is"),
            Map.entry(NOT, "not"),
            Map.entry(INTEGER, "integer"),
            Map.entry(REAL, "real"),
            Map.entry(STRING, "string"),
            Map.entry(EMPTY, "empty"),
            Map.entry(FUNC, "func"),
            Map.entry(TRUE, "true"),
            Map.entry(FALSE, "false"),
            Map.entry(BOOLEAN, "boolean"),
            Map.entry(RETURN, "return"),
            Map.entry(INPUT, "input"),
            Map.entry(MORE, ">"),
            Map.entry(LESS, "<"),
            Map.entry(MORE_OR_EQUAL, ">="),
            Map.entry(LESS_OR_EQUAL, "<="),
            Map.entry(EQUAL, "="),
            Map.entry(NOT_EQUAL, "/="),
            Map.entry(ADDITION, "+"),
            Map.entry(SUBSTRACTION, "-"),
            Map.entry(MULTIPLICATION, "*"),
            Map.entry(DIVISION, "/"),
            Map.entry(ASSIGNMENT, ":="),
            Map.entry(SEMICOLON, ";"),
            Map.entry(COMMA, ","),
            Map.entry(OPEN_CURLY_BRACKET, "{"),
            Map.entry(CLOSED_CURLY_BRACKET, "}"),
            Map.entry(OPEN_SQUARE_BRACKET, "["),
            Map.entry(CLOSE_SQUARE_BRACKET, "]"),
            Map.entry(QUOTES, "\""),
            Map.entry(DOT, "."),
            Map.entry(PLUS_EQUAL, "+="),
            Map.entry(MINUS_EQUAL, "-=")
    );

    private static final Map<String, LexTokenCode> REVERSE_BINDING_TABLE =
            BINDING_TABLE.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    // TODO: improve to work with identifiers and literals?
    public static LexTokenCode bindToken(String token) {
        return REVERSE_BINDING_TABLE.getOrDefault(token, null);
    }
}
