package com.projectd.interpreter.syntax.tree;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class AstGrammarNode extends AstNode {
    @Getter
    private final AstGrammarNodeType type;

    public AstGrammarNode(AstGrammarNodeType type, AstNode parent) {
        super(parent);
        this.type = type;
    }

    public AstGrammarNode(AstGrammarNodeType type, AstNode parent, List<AstNode> children) {
        super(parent, children);
        this.type = type;
    }

    public AstGrammarNodeType getGrammarType() {
        return type;
    }

    @Override
    public String getContent() {
        return type.toString();
    }
}
