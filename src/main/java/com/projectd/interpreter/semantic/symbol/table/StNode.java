package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.shared.exception.ExceptionFactory;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StNode {

    private final String name;
    private int usedTimes;
    private AstNode link;

    public StNode(String name, AstNode link) {
        this.name = name;
        this.link = link;
    }

    void incrementUsedTimes() {
        usedTimes++;
    }

    public boolean isUsed() {
        return usedTimes > 0;
    }

    public String getName() {
        return name;
    }
}
