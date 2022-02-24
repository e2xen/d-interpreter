package com.projectd.interpreter.syntax.tree;

public enum AstGrammarNodeType {
    PROGRAM,
    STATEMENT,
    ASSIGNMENT,
    DECLARATION,
    PRINT,
    RETURN,
    IF,
    BODY,
    PRIMARY,
    TAIL,

    EXPRESSION,
    RELATION,
    FACTOR,
    TERM,
    UNARY,
    LITERAL,
    INTEGER_LITERAL,
    REAL_LITERAL,
    BOOLEAN_LITERAL,
    STRING_LITERAL,
    ARRAY_LITERAL,
    TUPLE_LITERAL,
    TYPE_INDICATOR,
}
