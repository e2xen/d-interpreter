package com.projectd.interpreter.syntax.tree;

public enum AstGrammarNodeType {
    PROGRAM,
    STATEMENT,
    ASSIGNMENT,
    DECLARATION,
    PRINT,
    RETURN,

    PRIMARY,
    TAIL,

    EXPRESSION,
    RELATION,
    FACTOR,
    TERM,
    UNARY,
    LITERAL,
    TYPE_INDICATOR,
}
