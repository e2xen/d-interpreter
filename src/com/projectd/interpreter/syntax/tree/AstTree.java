package com.projectd.interpreter.syntax.tree;

import com.projectd.interpreter.lex.token.LexToken;

public class AstTree {
    private AstNode root;

    public AstTree(LexToken rootData) {
        root = new AstNode(rootData, null);
    }

    public AstNode getRoot() {
        return root;
    }

    public String toString() {
        return this.root.toString();
    }
}
