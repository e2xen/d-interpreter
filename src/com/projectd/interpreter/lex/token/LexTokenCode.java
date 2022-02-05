package com.projectd.interpreter.lex.token;

public enum LexTokenCode {
    IDENTIFIER,
    LITERAL,
    IF,
    THEN,
    ELSE,
    END,
    VAR,
    WHILE,
    LOOP,
    FOR,
    IN,
    PRINT,
    OR,
    AND,
    XOR,
    IS,
    NOT,
    INTEGER,
    REAL,
    STRING,
    EMPTY,
    FUNC,
    TRUE,
    FALSE,
    BOOLEAN,
    RETURN,
    INPUT,
    // starting from here done
    MORE,
    LESS,
    MORE_OR_EQUAL,
    LESS_OR_EQUAL,
    EQUAL,
    NOT_EQUAL,
    ASSIGNMENT,
    ADDITION,
    SUBSTRACTION,
    MULTIPLICATION,
    DIVISION,
    SEMICOLON,
    COMMA,
    OPEN_CURLY_BRACKET,
    CLOSED_CURLY_BRACKET,
    OPEN_SQUARE_BRACKET,
    CLOSE_SQUARE_BRACKET,
    QUOTES,
    DOT,
    PLUS_EQUAL,
    MINUS_EQUAL
}
