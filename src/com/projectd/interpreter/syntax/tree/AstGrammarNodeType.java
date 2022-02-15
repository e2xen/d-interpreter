package com.projectd.interpreter.syntax.tree;

public enum AstGrammarNodeType {
    PROGRAM,
    STATEMENT,
    ASSIGNMENT,
    DECLARATION,
    PRINT,
    RETURN,

    EXPRESSION,
    TERM,
    FACTOR,
    TYPE_INDICATOR,
}
