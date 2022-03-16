package com.projectd.interpreter.semantic;

import com.projectd.interpreter.semantic.symbol.table.SymbolTable;
import com.projectd.interpreter.semantic.symbol.table.SymbolTableImpl;
import com.projectd.interpreter.syntax.tree.AstNode;

public class SemanticAnalyserImpl implements SemanticAnalyser {
    private final AstNode astRoot;
    
    public SemanticAnalyserImpl(AstNode root) {
        this.astRoot = root;
    }

    @Override
    public void analyse() {
        SymbolTable symbolTable = new SymbolTableImpl(astRoot);
    }
}
