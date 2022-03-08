package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.List;

public class StNode {

    private final StNode parent;
    private final String name;
    private final StNodeScope scope;
    private int usedTimes;
    private List<StNode> children;
    private AstNode link;

    public StNode(StNode parent, String name, StNodeScope scope, AstNode link) {
        this.parent = parent;
        this.name = name;
        this.scope = scope;
        this.link = link;
    }

    void incrementUsedTimes() {
        usedTimes++;
    }

    void addChild(StNode child) {
        children.add(child);
    }

    void addChildren(List<StNode> children) {
        this.children.addAll(children);
    }

    boolean isUsed() {
        return usedTimes > 0;
    }
}
