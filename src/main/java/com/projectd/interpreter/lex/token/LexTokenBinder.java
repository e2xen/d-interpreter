package com.projectd.interpreter.lex.token;

import java.util.Map;
import java.util.stream.Collectors;

public class LexTokenBinder {
    private static final Map<LexTokenCode, String> BINDING_TABLE = Map.ofEntries(
            Map.entry(LexTokenCode.IF, "if"),
            Map.entry(LexTokenCode.THEN, "then"),
            Map.entry(LexTokenCode.ELSE, "else"),
            Map.entry(LexTokenCode.END, "end"),
            Map.entry(LexTokenCode.VAR, "var"),
            Map.entry(LexTokenCode.WHILE, "while"),
            Map.entry(LexTokenCode.LOOP, "loop"),
            Map.entry(LexTokenCode.FOR, "for"),
            Map.entry(LexTokenCode.IN, "in"),
            Map.entry(LexTokenCode.PRINT, "print"),
            Map.entry(LexTokenCode.OR, "or"),
            Map.entry(LexTokenCode.AND, "and"),
            Map.entry(LexTokenCode.XOR, "xor"),
            Map.entry(LexTokenCode.IS, "is"),
            Map.entry(LexTokenCode.NOT, "not"),
            Map.entry(LexTokenCode.INTEGER, "int"),
            Map.entry(LexTokenCode.REAL, "real"),
            Map.entry(LexTokenCode.STRING, "string"),
            Map.entry(LexTokenCode.EMPTY, "empty"),
            Map.entry(LexTokenCode.FUNC, "func"),
            Map.entry(LexTokenCode.TRUE, "true"),
            Map.entry(LexTokenCode.FALSE, "false"),
            Map.entry(LexTokenCode.BOOLEAN, "boolean"),
            Map.entry(LexTokenCode.RETURN, "return"),
            Map.entry(LexTokenCode.INPUT, "input"),
            Map.entry(LexTokenCode.MORE, ">"),
            Map.entry(LexTokenCode.LESS, "<"),
            Map.entry(LexTokenCode.MORE_OR_EQUAL, ">="),
            Map.entry(LexTokenCode.LESS_OR_EQUAL, "<="),
            Map.entry(LexTokenCode.EQUAL, "="),
            Map.entry(LexTokenCode.NOT_EQUAL, "/="),
            Map.entry(LexTokenCode.ADDITION, "+"),
            Map.entry(LexTokenCode.SUBTRACTION, "-"),
            Map.entry(LexTokenCode.MULTIPLICATION, "*"),
            Map.entry(LexTokenCode.DIVISION, "/"),
            Map.entry(LexTokenCode.ASSIGNMENT, ":="),
            Map.entry(LexTokenCode.SEMICOLON, ";"),
            Map.entry(LexTokenCode.COMMA, ","),
            Map.entry(LexTokenCode.OPEN_CURLY_BRACKET, "{"),
            Map.entry(LexTokenCode.CLOSED_CURLY_BRACKET, "}"),
            Map.entry(LexTokenCode.OPEN_SQUARE_BRACKET, "["),
            Map.entry(LexTokenCode.CLOSE_SQUARE_BRACKET, "]"),
            Map.entry(LexTokenCode.QUOTES, "\""),
            Map.entry(LexTokenCode.DOT, "."),
            Map.entry(LexTokenCode.PLUS_EQUAL, "+="),
            Map.entry(LexTokenCode.MINUS_EQUAL, "-="),
            Map.entry(LexTokenCode.OPEN_ROUND_BRACKET, "("),
            Map.entry(LexTokenCode.CLOSED_ROUND_BRACKET, ")"),
            Map.entry(LexTokenCode.READ_INT, "readInt"),
            Map.entry(LexTokenCode.READ_REAL, "readReal"),
            Map.entry(LexTokenCode.READ_STRING, "readString")
    );

    private static final Map<String, LexTokenCode> REVERSE_BINDING_TABLE =
            BINDING_TABLE.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    // TODO: improve to work with identifiers and literals?
    public static LexTokenCode bindToken(String token) {
        return REVERSE_BINDING_TABLE.getOrDefault(token, null);
    }

    public static String lexTokenString(LexTokenCode tokenCode) {
        return BINDING_TABLE.getOrDefault(tokenCode, null);
    }
}
