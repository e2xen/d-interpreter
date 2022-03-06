package com.projectd.interpreter.syntax.contract;

import com.projectd.interpreter.syntax.tree.AstNode;

public interface SyntaxAnalyserParseGrammar {
    AstNode parse(AstNode parent);
}
