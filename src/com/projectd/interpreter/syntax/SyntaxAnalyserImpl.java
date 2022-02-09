package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.syntax.exception.SyntaxAnalyzerParseException;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTree;

import java.util.List;

public class SyntaxAnalyserImpl implements SyntaxAnalyser {

    private final List<LexToken> tokens;
    private int currentPosition;

    public SyntaxAnalyserImpl(List<LexToken> tokens) {
        this.tokens = tokens;
        this.currentPosition = 0;
    }

    @Override
    public AstTree buildAstTree() throws SyntaxAnalyzerParseException {
        AstTree result = new AstTree(new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.PROGRAM));

        return result;
    }

    private AstNode parseExpression(AstNode parent) throws SyntaxAnalyzerParseException {
        AstNode firstTermTemp = parseTerm(null);

        if(!possibleIncrementCurrentPosition()) return new AstNode(firstTermTemp.getData(), parent);
        incrementCurrentPosition();

        LexToken nextToken = tokens.get(currentPosition);

        AstNode currentExpressionTemp = null;
        switch (nextToken.getCode()) {
            case ADDITION, SUBTRACTION -> {
                AstNode op = new AstNode(nextToken, null);
                AstNode firstTerm = new AstNode(firstTermTemp.getData(), op);
                op.addChild(firstTerm);
                incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of token list. But expected term.");
                AstNode secondTerm = parseTerm(op);
                op.addChild(secondTerm);
                currentExpressionTemp = op;
            }

            default -> {
                return new AstNode(firstTermTemp.getData(), parent);
            }
        }

        while (true) {
            if(!possibleIncrementCurrentPosition()) {
                // TODO: fix children pts
                AstNode result = new AstNode(currentExpressionTemp.getData(), parent);
                result.addChildren(currentExpressionTemp.getChildren());
            }
            incrementCurrentPosition();

            LexToken next = tokens.get(currentPosition);
            switch (next.getCode()) {
                case ADDITION, SUBTRACTION -> {
                    AstNode op = new AstNode(next, currentExpressionTemp);
                    op.addChild(currentExpressionTemp);
                    incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of token list. But expected term.");
                    AstNode secondTerm = parseTerm(op);
                    op.addChild(secondTerm);
                    currentExpressionTemp = op;
                }

                default -> {
                    return new AstNode(currentExpressionTemp.getData(), parent);
                }
            }
        }
    }

    private AstNode parseTerm(AstNode parent) throws SyntaxAnalyzerParseException {
        AstNode factor = parseFactor(parent);

        if(!possibleIncrementCurrentPosition()) return factor;
        incrementCurrentPosition();

        LexToken currentToken = tokens.get(currentPosition);
        switch (currentToken.getCode()) {
            case MULTIPLICATION, DIVISION -> {
                AstNode resOp = new AstNode(currentToken, parent);
                AstNode newFactor = new AstNode(factor.getData(), resOp);
                resOp.addChild(newFactor);
                AstNode term = parseTerm(resOp);
                resOp.addChild(term);
                return resOp;
            }

            default -> {
                return factor;
            }
        }
    }

    private AstNode parseFactor(AstNode parent) throws SyntaxAnalyzerParseException {
        LexToken currentToken = tokens.get(currentPosition);
        switch (currentToken.getCode()) {
            case IDENTIFIER -> {
                if (currentToken instanceof LexIdentifierToken) {
                    return new AstNode(currentToken, parent);
                } else {
                    throw new IllegalStateException(String.format("Unexpected instance of class for %s lex currentToken", LexTokenCode.IDENTIFIER));
                }
            }

            case LITERAL -> {
                if (currentToken instanceof LexLiteralToken) {
                    return new AstNode(currentToken, parent);
                } else {
                    throw new IllegalStateException(String.format("Unexpected instance of class for %s lex currentToken", LexTokenCode.LITERAL));
                }
            }

            case OPEN_CURLY_BRACKET -> {
                // TODO: verify
                AstNode expression = parseExpression(parent);
                incrementCurrentPositionOrError("Couldn't parse expression factor, unexpected end of currentToken list.");
                LexToken nextToken = tokens.get(currentPosition);
                if(nextToken.getCode() == LexTokenCode.CLOSED_CURLY_BRACKET) {
                    // TODO: fix
                    return expression;
                } else {
                    throw new SyntaxAnalyzerParseException(String.format("Couldn't parse expression, expected currentToken %s, but got %s",
                            LexTokenCode.CLOSED_CURLY_BRACKET, nextToken.getCode()));
                }
            }

            case SUBTRACTION, ADDITION -> {
                AstNode addOp = new AstNode(currentToken, parent);
                AstNode factor = parseFactor(addOp);
                addOp.addChild(factor);
                return addOp;
            }

            default ->
                throw new SyntaxAnalyzerParseException(String.format("Expected factor, but got %s", currentToken));
        }
    }

    private void incrementCurrentPositionOrError(String exceptionMsg) throws SyntaxAnalyzerParseException {
        if(possibleIncrementCurrentPosition()) {
            incrementCurrentPosition();
        }
        else {
            throw new SyntaxAnalyzerParseException(exceptionMsg);
        }
    }

    private boolean possibleIncrementCurrentPosition() {
        return currentPosition + 1 < tokens.size();
    }

    private void incrementCurrentPosition() {
        setCurrentPosition(currentPosition + 1);
    }

    private void setCurrentPosition(int position) {
        if (position >= tokens.size()) throw new IllegalStateException("Out of list with lex tokens.");
        this.currentPosition = position;
    }
}
