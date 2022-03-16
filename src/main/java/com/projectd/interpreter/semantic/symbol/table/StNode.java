package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.shared.exception.ExceptionFactory;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StNode {

    private final StNode parent;
    private final String name;
    private final StNodeScope scope;
    private int usedTimes;
    private List<StNode> children = new ArrayList<>();
    private List<String> childrenNames = new ArrayList<>();
    private AstNode link;

    public static StNode valueOfFunParam(StNode parent, String name, AstNode link) {
        return new StNode(parent, name, link, StNodeScope.PROCEDURE_PARAMETER);
    }

    public static StNode valueOf(StNode parent, String name, AstNode link) {
        if (parent instanceof RootStNode) {
            return new StNode(parent, name, link, StNodeScope.GLOBAL);
        }
        return new StNode(parent, name, link, StNodeScope.LOCAL);
    }

    protected StNode(StNode parent, String name, AstNode link, StNodeScope scope) {
        this.parent = parent;
        this.name = name;
        this.link = link;
        this.scope = scope;
    }

    void incrementUsedTimes() {
        usedTimes++;
    }

    public void addChild(StNode child) {
        if (containsNameInCurrentScope(child.name)) {
            // TODO: 0, 0
            throw ExceptionFactory.illegalDefinitionOfVariable(child.name, 0, 0);
        }
        children.add(child);
    }

    public void addChildren(List<StNode> children) {
        this.children.addAll(children);
    }

    public boolean isUsed() {
        return usedTimes > 0;
    }

    protected boolean containsNameInCurrentScope(String identifier) {
        if (scope == StNodeScope.GLOBAL) {
            return parent.containsNameInCurrentScope(identifier);
        }
        return childrenNames.contains(identifier);
    }

    protected List<StNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }
}
