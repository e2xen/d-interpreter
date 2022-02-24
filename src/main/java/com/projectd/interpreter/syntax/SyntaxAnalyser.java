package com.projectd.interpreter.syntax;

import com.projectd.interpreter.shared.exception.SyntaxAnalyzerParseException;
import com.projectd.interpreter.syntax.tree.AstNode;

public interface SyntaxAnalyser {
    AstNode buildAstTree() throws SyntaxAnalyzerParseException;
}
