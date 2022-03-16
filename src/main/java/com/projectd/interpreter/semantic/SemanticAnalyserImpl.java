package com.projectd.interpreter.semantic;

import com.projectd.interpreter.lex.token.LexLiteralToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.semantic.symbol.table.RootStNode;
import com.projectd.interpreter.semantic.symbol.table.StNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.util.ArrayList;

public class SemanticAnalyserImpl implements SemanticAnalyser {
    private final StNode stRoot = new RootStNode();

    @Override
    public void analyse(AstNode root) {
        if (! (root instanceof AstGrammarNode program && program.getType().equals(AstGrammarNodeType.PROGRAM))) {
            throw new IllegalStateException("To perform semantic analysis the root of the tree must be passed as a " +
                    "parameter (AstGrammarNodeType.PROGRAM)");
        }

        for (AstNode rootChild : root.getChildren()) {
            buildSymbolTable(stRoot, rootChild);
        }
    }

    private void buildSymbolTable(StNode parent, AstNode subtree) {
        switch (subtree) {
            case (AstGrammarNode g && g.getType().equals(AstGrammarNodeType.ASSIGNMENT)) -> {
                if(g.getChildren().size() == 0) {
                    String message = String.format("Grammar node( %s ) must have children (cannot be 0)",
                            AstGrammarNodeType.ASSIGNMENT);
                    throw new IllegalStateException(message);
                }

                AstNode primary = g.getChildren().get(0);
                if (primary instanceof AstGrammarNode p && p.getType().equals(AstGrammarNodeType.PRIMARY)) {
                    if(p.getChildren().size() == 0) {
                        String message = String.format("Grammar node( %s ) must have children (cannot be 0)",
                                AstGrammarNodeType.PRIMARY);
                        throw new IllegalStateException(message);
                    }
                    AstNode token = p.getChildren().get(0);

                    if(token instanceof AstTokenNode l && l.getToken() instanceof LexLiteralToken literalToken) {
                        parent.
                    }

                } else {
                    String message = String.format("Grammar node( %s ) must must have a primary node at position 0",
                            AstGrammarNodeType.ASSIGNMENT);
                    throw new IllegalStateException(message);
                }
            }

            default -> {
                for (AstNode node : subtree.getChildren()) {
                    buildSymbolTable(parent, node);
                }
            }
        };
    }
}
