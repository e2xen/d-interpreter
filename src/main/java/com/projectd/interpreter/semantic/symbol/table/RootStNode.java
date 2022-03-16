package com.projectd.interpreter.semantic.symbol.table;

public class RootStNode extends StNode {

    public RootStNode() {
        super(null, null, null, StNodeScope.GLOBAL);
    }
}
