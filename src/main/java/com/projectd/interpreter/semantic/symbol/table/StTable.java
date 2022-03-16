package com.projectd.interpreter.semantic.symbol.table;

import java.util.ArrayList;
import java.util.List;

public class StTable {
    private final StNode parent;
    private final StNodeScope scope;
    private List<StNode> children = new ArrayList<>();
    private List<String> childrenNames = new ArrayList<>();


    protected boolean containsNameInCurrentScope(String identifier) {
        if (scope == StNodeScope.GLOBAL) {
            return parent.containsNameInCurrentScope(identifier);
        }
        return childrenNames.contains(identifier);
    }

    protected List<StNode> getChildren() {
        return children;
    }

}
