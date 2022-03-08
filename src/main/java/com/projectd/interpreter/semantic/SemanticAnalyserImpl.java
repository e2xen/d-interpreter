package com.projectd.interpreter.semantic;

import com.projectd.interpreter.semantic.symbol.table.StNode;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyserImpl implements SemanticAnalyser {

    @Override
    public void analyse(AstNode root) {
        // Traverse tree
        // TODO: analyse all scopes
        List<StNode> symbolTable = new ArrayList<>();
    }
}
