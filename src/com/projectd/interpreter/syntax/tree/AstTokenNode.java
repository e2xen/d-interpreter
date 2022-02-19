package com.projectd.interpreter.syntax.tree;

import com.projectd.interpreter.lex.token.LexToken;

import java.util.List;

public class AstTokenNode extends AstNode {
    private final LexToken token;

    public AstTokenNode(LexToken token, AstNode parent) {
        super(parent);
        this.token = token;
    }

    public LexToken getToken() {
        return token;
    }

    @Override
    public String getContent() {
        return token.getCode().toString();
    }

    @Override
    public void addChild(AstNode child) {
        throw new IllegalStateException("Token node cannot have children");
    }

    @Override
    public void addChildren(List<AstNode> children) {
        throw new IllegalStateException("Token node cannot have children");
    }

    @Override
    public List<AstNode> getChildren() {
        throw new IllegalStateException("Token node cannot have children");
    }
}
