package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.shared.exception.ExceptionFactory;
import com.projectd.interpreter.shared.util.GrammarUtil;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.List;
import java.util.Set;

import static com.projectd.interpreter.shared.util.GrammarUtil.verifyLexInstanceOrException;

class SymbolTableImpl implements SymbolTable {
    private final Set<LexTokenCode> inputTokenCodes = Set.of(
            LexTokenCode.READ_INT,
            LexTokenCode.READ_REAL,
            LexTokenCode.READ_STRING);

    private final AstNode astRoot;

    public SymbolTableImpl(AstNode root) {
        GrammarUtil.verifyGramInstanceOrException(root, AstGrammarNodeType.PROGRAM);
        this.astRoot = root;
    }

    public StTable build() {
        StTable result = new StTable(null);

        for (AstNode firstLevelChild : astRoot.getChildren()) {
            GrammarUtil.verifyGramInstanceOrException(firstLevelChild, AstGrammarNodeType.STATEMENT);
            parseStatement(firstLevelChild, result);
        }

        return result;
    }

    private void parseStatement(AstNode node, StTable symbolTable) {
        switch (node) {
            case (AstGrammarNode g && g.getType().equals(AstGrammarNodeType.ASSIGNMENT)) -> {
                AstNode primary = getElementOrNull(g.getChildren(), 0);
                GrammarUtil.verifyGramInstanceOrException(primary, AstGrammarNodeType.PRIMARY);
                AstNode tokenNode = getElementOrNull(primary.getChildren(), 0);
                LexToken lexToken = verifyLexInstanceOrException(tokenNode);

                if (lexToken instanceof LexIdentifierToken identToken) {
                    if(inputTokenCodes.contains(lexToken.getCode())) {
                        LexTokenSpan span = lexToken.getSpan();
                        throw ExceptionFactory.illegalOverridingException(identToken.getIdentifier(), span.getLineNum(), span.getPos());
                    }

                    if(!symbolTable.containsNameInScope(identToken.getIdentifier())) {
                        LexTokenSpan span = lexToken.getSpan();
                        throw ExceptionFactory.undeclaredVariableException(identToken.getIdentifier(), span.getLineNum(), span.getPos());
                    }
                }



            }

            default -> {
                for (AstNode node : subtree.getChildren()) {
                    buildSymbolTable(parent, node);
                }
            }
        };
    }

    private AstNode getElementOrNull(List<AstNode> nodes, int i) {
        if(nodes.size() < i) {
            return nodes.get(i);
        }
        return null;
    }

}
