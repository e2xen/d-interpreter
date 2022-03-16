package com.projectd.interpreter.runtime.environment;

import com.projectd.interpreter.lex.token.LexIdentifierToken;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.List;

public class RuntimeFunction {

    private final List<LexIdentifierToken> parameters;
    private final List<AstNode> body;

    public RuntimeFunction(List<LexIdentifierToken> parameters, List<AstNode> body) {
        this.parameters = parameters;
        this.body = body;
    }

}
