package com.projectd.interpreter.semantic;

import com.projectd.interpreter.semantic.symbol.table.StNode;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SemanticAnalyserImpl implements SemanticAnalyser {
    ArrayList<AstNode> visited = new ArrayList<>();
    List<StNode> symbolTable = new ArrayList<>();

    @Override
    // DFS
    public void analyse(AstNode node) {
        visited.add(node);
        // проверка IDENTIFIER или нет
        // если да - добавляем в таблицу, если нет итерируем дальше
        Iterator<AstNode> children = node.getChildren().listIterator();
        while(children.hasNext()){
            AstNode current = children.next();
            if(!visited.contains(current)){
                analyse(current);
            }
        }
    }
}
