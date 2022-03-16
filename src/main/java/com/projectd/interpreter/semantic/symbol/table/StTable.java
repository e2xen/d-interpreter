package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.lex.token.LexTokenSpan;
import com.projectd.interpreter.shared.exception.ExceptionFactory;

import java.util.ArrayList;
import java.util.List;

public class StTable {
    private final StTable parent;
    private List<StNode> children = new ArrayList<>();
    private List<String> childrenIdentifiers = new ArrayList<>();

    public StTable(StTable parent) {
        this.parent = parent;
    }

    public void addChild(StNode node) {
        if (childrenIdentifiers.contains(node.getName())) {
            LexTokenSpan span = node.getLink().getSpan();
            throw ExceptionFactory.illegalDefinitionOfVariable(node.getName(), span.getLineNum(), span.getPos());
        }
        this.children.add(node);
    }

    public boolean containsNameInCurrentScope(String identifier) {
        return childrenIdentifiers.contains(identifier);
    }

    public boolean containsNameInScope(String identifier) {
        if (parent == null) {
            return childrenIdentifiers.contains(identifier);
        }
        return childrenIdentifiers.contains(identifier) || parent.containsNameInScope(identifier);
    }

    public List<StNode> getChildren() {
        return children;
    }

}
