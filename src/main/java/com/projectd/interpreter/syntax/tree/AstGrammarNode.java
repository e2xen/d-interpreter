package com.projectd.interpreter.syntax.tree;

import java.util.List;

public class AstGrammarNode extends AstNode {
    private final AstGrammarNodeType type;

    public AstGrammarNode(AstGrammarNodeType type, AstNode parent) {
        super(parent);
        this.type = type;
    }

    public AstGrammarNode(AstGrammarNodeType type, AstNode parent, List<AstNode> children) {
        super(parent, children);
        this.type = type;
    }

    @Override
    public String getContent() {
        return type.toString();
    }
}
