package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.syntax.tree.AstNode;

public class StNode {

    private final String name;
    private final StNodeScope scope;

    public StNode(String name, StNodeScope scope) {
        this.name = name;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }
}
