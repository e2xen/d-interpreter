package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.syntax.tree.AstNode;

public class StNode {

    private final String name;
    private final StNodeScope scope;
    private int usedTimes;
    private AstNode link;

    public StNode(String name, StNodeScope scope, AstNode link) {
        this.name = name;
        this.scope = scope;
        this.link = link;
    }

    public void incrementUsedTimes() {
        usedTimes++;
    }

    public boolean isUsed() {
        return usedTimes > 0;
    }

    public String getName() {
        return name;
    }

    public AstNode getLink() {
        return link;
    }
}
