package com.projectd.interpreter.syntax.tree;

import com.projectd.interpreter.lex.token.LexToken;

import java.util.List;

/*
    * Token node has no children.
 */
public class AstTokenNode extends AstNode {
    private final LexToken data;

    public AstTokenNode(LexToken data, AstNode parent) {
        super(parent);
        this.data = data;
    }

    @Override
    public String getContent() {
        return data.toString();
    }

    @Override
    public void addChild(AstNode child) {
        throw new IllegalStateException("Token node cannot have any amount of children.");
    }

    @Override
    public void addChildren(List<AstNode> children) {
        throw new IllegalStateException("Token node cannot have any amount of children.");
    }
}
