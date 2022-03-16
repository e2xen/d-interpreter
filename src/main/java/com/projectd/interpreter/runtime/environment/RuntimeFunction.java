package com.projectd.interpreter.runtime.environment;

import com.projectd.interpreter.lex.token.LexIdentifierToken;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.List;

public class RuntimeFunction {

    private final List<LexIdentifierToken> parameters;
    private final List<AstNode> body;
    private final boolean isLambda;

    public RuntimeFunction(List<LexIdentifierToken> parameters, List<AstNode> body, boolean isLambda) {
        this.parameters = parameters;
        this.body = body;
        this.isLambda = isLambda;
    }

    public List<LexIdentifierToken> getParameters() {
        return parameters;
    }

    public List<AstNode> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "<func>";
    }

    public boolean isLambda() {
        return isLambda;
    }
}
