package com.projectd.interpreter.semantic.symbol.table;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.shared.exception.ExceptionFactory;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.projectd.interpreter.shared.util.GrammarUtil.verifyGramInstanceOrException;
import static com.projectd.interpreter.shared.util.GrammarUtil.verifyLexInstanceOrException;

public class SymbolTableImpl implements SymbolTable {
    private final boolean EXPERIMENTAL = false;
    private final Set<LexTokenCode> readTokenCodes = Set.of(
            LexTokenCode.READ_INT,
            LexTokenCode.READ_REAL,
            LexTokenCode.READ_STRING);
    private final AstNode astRoot;

    public SymbolTableImpl(AstNode root) {
        verifyGramInstanceOrException(root, AstGrammarNodeType.PROGRAM);
        this.astRoot = root;
    }

    public StTable build() {
        if(!EXPERIMENTAL) return new StTable(null);

        StTable result = new StTable(null);

        for (AstNode firstLevelChild : astRoot.getChildren()) {
            verifyGramInstanceOrException(firstLevelChild, AstGrammarNodeType.STATEMENT);
            parseStatement(firstLevelChild, result);
        }

        return result;
    }

    private void parseStatement(AstNode node, StTable symbolTable) {
        AstGrammarNode statement = verifyGramInstanceOrException(node, AstGrammarNodeType.STATEMENT);
        switch (statement.getType()) {
            case ASSIGNMENT ->
                parseAssignment(node, symbolTable);
        };
    }

    private void parseAssignment(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.EXPRESSION);
        AstNode primary = getElementOrNull(node.getChildren(), 0);

        verifyGramInstanceOrException(primary, AstGrammarNodeType.PRIMARY);
        parsePrimaryDisallowReading(primary, symbolTable);

        AstNode expression = getElementOrNull(node.getChildren(), 2);
        verifyGramInstanceOrException(expression, AstGrammarNodeType.EXPRESSION);
        parseExpression(expression, symbolTable);
    }

    private void parseExpression(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.EXPRESSION);
        node.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .forEach(e -> this.parseConjunction(e, symbolTable));
    }

    private void parseConjunction(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.CONJUNCTION);
        node.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .forEach(e -> this.parseRelation(e, symbolTable));
    }

    private void parseRelation(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.RELATION);
        node.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .forEach(e -> this.parseFactor(e, symbolTable));
    }

    private void parseFactor(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.FACTOR);
        node.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .forEach(e -> this.parseTerm(e, symbolTable));
    }

    private void parseTerm(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.TERM);
        node.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .forEach(e -> this.parseUnary(e, symbolTable));
    }

    private void parseUnary(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.UNARY);
        AstNode child = getElementOrNull(node.getChildren(), 0);
        AstGrammarNode childGram = verifyGramInstanceOrException(child);
        switch (childGram.getType()) {
            case PRIMARY -> parsePrimaryAllowReading(childGram, symbolTable);
            case LITERAL -> this.parseLiteral(childGram, symbolTable);
            case EXPRESSION -> this.parseExpression(childGram, symbolTable);
        }
    }

    private void parsePrimaryAllowReading(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.PRIMARY);

        AstNode tokenNode = getElementOrNull(node.getChildren(), 0);
        LexToken lexToken = verifyLexInstanceOrException(tokenNode);
        if (!readTokenCodes.contains(lexToken.getCode()) && (lexToken instanceof LexIdentifierToken identToken)) {
            if(!symbolTable.containsNameInScope(identToken.getIdentifier())) {
                LexTokenSpan span = lexToken.getSpan();
                throw ExceptionFactory.undeclaredVariableException(identToken.getIdentifier(), span.getLineNum(), span.getPos());
            }
        }
    }

    private void parsePrimaryDisallowReading(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.PRIMARY);

        AstNode tokenNode = getElementOrNull(node.getChildren(), 0);
        LexToken lexToken = verifyLexInstanceOrException(tokenNode);
        if (!readTokenCodes.contains(lexToken.getCode()) && (lexToken instanceof LexIdentifierToken identToken)) {
            if(readTokenCodes.contains(lexToken.getCode())) {
                LexTokenSpan span = lexToken.getSpan();
                throw ExceptionFactory.illegalOverridingException(identToken.getIdentifier(), span.getLineNum(), span.getPos());
            }

            if(!symbolTable.containsNameInScope(identToken.getIdentifier())) {
                LexTokenSpan span = lexToken.getSpan();
                throw ExceptionFactory.undeclaredVariableException(identToken.getIdentifier(), span.getLineNum(), span.getPos());
            }
        }
    }

    private void parseLiteral(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.LITERAL);

        if (getElementOrNull(node.getChildren(), 0) instanceof AstGrammarNode g) {
            switch (g.getType()) {
                case FUNCTION_LITERAL -> this.parseFunctionLiteral(g, symbolTable);
            }
        }
    }

    private void parseFunctionLiteral(AstNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.FUNCTION_LITERAL);

        StTable innerFuncStTable = new StTable(symbolTable);

        if(getElementOrNull(node.getChildren(), 1) instanceof AstGrammarNode g && g.getType().equals(AstGrammarNodeType.PARAMETERS)) {
            this.parseFunctionParameters(g, innerFuncStTable);
        }

        if(getElementOrNull(node.getChildren(), node.getChildren().size() - 1) instanceof AstGrammarNode g &&
                g.getType().equals(AstGrammarNodeType.BODY)) {
            this.parseFunctionBody(g, innerFuncStTable);
        }
    }

    private void parseFunctionBody(AstGrammarNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.FUN_BODY);

        List<AstNode> listGram = node.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .collect(Collectors.toList());

        if(getElementOrNull(listGram, 0) instanceof AstGrammarNode g && g.getType().equals(AstGrammarNodeType.EXPRESSION)) {
            this.parseExpression(g, symbolTable);
        }
        if(getElementOrNull(listGram, 0) instanceof AstGrammarNode g && g.getType().equals(AstGrammarNodeType.BODY)) {
            this.parseBody(g, symbolTable);
        }
    }

    private void parseBody(AstGrammarNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.BODY);

        node.getChildren().stream()
                .map(e -> (AstGrammarNode) e)
                .filter(e -> e.getType().equals(AstGrammarNodeType.STATEMENT))
                .forEach(e -> parseStatement(e, symbolTable));
    }

    private void parseFunctionParameters(AstGrammarNode node, StTable symbolTable) {
        verifyGramInstanceOrException(node, AstGrammarNodeType.PARAMETERS);

        node.getChildren().stream()
                .map(e -> (AstTokenNode) e)
                .map(AstTokenNode::getToken)
                .filter(e -> e instanceof LexIdentifierToken)
                .map(e -> (LexIdentifierToken) e)
                .forEach(e -> symbolTable.addChild(new StNode(e.getIdentifier(), StNodeScope.PROCEDURE_PARAMETER)));
    }


    private AstNode getElementOrNull(List<AstNode> nodes, int i) {
        if(nodes.size() < i) {
            return nodes.get(i);
        }
        return null;
    }

}
