package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.shared.utils.Tree;

import java.util.List;

public interface SyntaxAnalyser {
    Tree<LexToken> buildAstTree(List<LexToken> tokens);
}
