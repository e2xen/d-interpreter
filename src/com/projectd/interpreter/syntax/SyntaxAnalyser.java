package com.projectd.interpreter.syntax;

import com.projectd.interpreter.syntax.exception.SyntaxAnalyzerParseException;
import com.projectd.interpreter.syntax.tree.AstTree;

public interface SyntaxAnalyser {
    AstTree buildAstTree() throws SyntaxAnalyzerParseException;
}
