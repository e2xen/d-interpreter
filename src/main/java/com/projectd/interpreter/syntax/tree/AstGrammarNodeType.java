package com.projectd.interpreter.syntax.tree;

public enum AstGrammarNodeType {
    PROGRAM,
    STATEMENT,
    ASSIGNMENT,
    DECLARATION,
    PRINT,
    RETURN,
    IF,

    LOOP,
    LOOP_BODY,

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

    FUNCTION_LITERAL,
    PARAMETERS,
    FUN_BODY,

    ARRAY_LITERAL,
    TUPLE_LITERAL,
    TUPLE_ELEMENT,
    TYPE_INDICATOR,
}
