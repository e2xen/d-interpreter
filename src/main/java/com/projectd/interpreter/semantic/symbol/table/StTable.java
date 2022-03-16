package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.lex.token.LexTokenSpan;
import com.projectd.interpreter.shared.exception.ExceptionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StTable {
    private final StTable parent;
    private Map<String, StNode> children = new HashMap<>();
    private List<StTable> innerFunctions = new ArrayList<>();

    public StTable(StTable parent) {
        this.parent = parent;
    }

    public void addChild(StNode node) {
        if (this.containsNameInCurrentScope(node.getName())) {
            throw ExceptionFactory.illegalDefinitionOfVariable(node.getName(),0, 0);
        }

        this.children.put(node.getName(), node);
    }

    public void addFunction(StTable stTable) {
        innerFunctions.add(stTable);
    }

    public boolean containsNameInCurrentScope(String identifier) {
        return children.containsKey(identifier);
    }

    public boolean containsNameInScope(String identifier) {
        if (parent == null) {
            return children.containsKey(identifier);
        }
        return children.containsKey(identifier) || parent.containsNameInScope(identifier);
    }

}
