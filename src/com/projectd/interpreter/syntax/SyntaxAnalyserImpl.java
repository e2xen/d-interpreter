package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.shared.exception.AmbiguousGrammarException;
import com.projectd.interpreter.shared.exception.ExceptionFactory;
import com.projectd.interpreter.shared.exception.SyntaxAnalyzerParseException;
import com.projectd.interpreter.syntax.iterator.LexTokenIterator;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.beans.Expression;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.projectd.interpreter.syntax.tree.AstGrammarNodeType.*;

public class SyntaxAnalyserImpl implements SyntaxAnalyser {

    private final LexTokenIterator iterator;

    public SyntaxAnalyserImpl(List<LexToken> tokens) {
        this.iterator = new LexTokenIterator(tokens);
    }

    @Override
    public AstNode buildAstTree() {
        AstNode program = new AstGrammarNode(AstGrammarNodeType.PROGRAM, null);

        while (iterator.hasNext()) {
            program.addChild(parseStatement(program));
        }

        return program;
    }

    @SafeVarargs
    private AstNode parseAnyOf(AstNode parent, Function<AstNode, AstNode>... parseTries) {
        AstNode parseResult = null;
        for (Function<AstNode, AstNode> parseTry : parseTries) {
            iterator.checkpoint();
            try {
                parseResult = parseTry.apply(parent);
                iterator.discardCheckpoint();
            } catch (SyntaxAnalyzerParseException e) {
                iterator.rollback();
                continue;
            }

            if (parseResult != null) {
                break;
            }
        }
        return parseResult;
    }

    @SafeVarargs
    private List<AstNode> parseSeries(AstNode parent, Function<AstNode, AstNode>... parseSeries) {
        List<AstNode> parseResult = new ArrayList<>();
        for (Function<AstNode, AstNode> parse : parseSeries) {
            parseResult.add(parse.apply(parent));
        }
        return parseResult;
    }

    @SafeVarargs
    private List<AstNode> parseOptionalSeries(AstNode parent, Function<AstNode, AstNode>... parseSeries) {
        List<AstNode> parseResult = new ArrayList<>();
        iterator.checkpoint();
        for (Function<AstNode, AstNode> parse : parseSeries) {
            try {
                parseResult.add(parse.apply(parent));
            } catch (SyntaxAnalyzerParseException e) {
                iterator.rollback();
                parseResult = new ArrayList<>();
            }
        }

        if (parseResult.size() > 0) {
            iterator.discardCheckpoint();
        }

        return parseResult;
    }

    @SafeVarargs
    private List<AstNode> parseRepeated(AstNode parent, Function<AstNode, AstNode>... parsePattern) {
        List<AstNode> parseResult = new ArrayList<>();
        while (iterator.hasNext()) {
            iterator.checkpoint();
            try {
                parseResult.addAll(parseSeries(parent, parsePattern));
                iterator.discardCheckpoint();
            } catch (SyntaxAnalyzerParseException e) {
                iterator.rollback();
                break;
            }
        }
        return parseResult;
    }

    private AstNode parseToken(Set<LexTokenCode> expectedTokens, AstNode parent) {
        if (!iterator.hasNext()) {
            throw ExceptionFactory.noToken(expectedTokens);
        }
        LexToken token = iterator.next();
        if (!expectedTokens.contains(token.getCode())) {
            LexTokenSpan nextSpan = token.getSpan();
            throw ExceptionFactory.unexpectedToken(expectedTokens, token.getCode(), nextSpan.getLineNum(), nextSpan.getPos());
        }

        return new AstTokenNode(token, parent);
    }

    private Function<AstNode, AstNode> parseToken(Set<LexTokenCode> expectedTokens) {
        return parent -> parseToken(expectedTokens, parent);
    }

    private Function<AstNode, AstNode> parseToken(LexTokenCode expectedToken) {
        return parent -> parseToken(Set.of(expectedToken), parent);
    }

    /** Statement : { Assignment | Declaration | Print | Return | If | Loop } */
    private AstNode parseStatement(AstNode parent) {
        AstGrammarNode statement = new AstGrammarNode(STATEMENT, parent);

        AstNode child = parseAnyOf(statement,
                this::parseAssignment,
                this::parseDeclaration,
                this::parsePrint
                // TODO: implement others
                );

        if (child == null) {
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(STATEMENT, span.getLineNum(), span.getPos());
        }
        statement.addChild(child);
        return statement;
    }

    /** Assignment : Primary := Expression */
    private AstNode parseAssignment(AstNode parent) {
        AstGrammarNode assignment = new AstGrammarNode(ASSIGNMENT, parent);

        List<AstNode> children = parseSeries(assignment,
                this::parsePrimary,
                parseToken(LexTokenCode.ASSIGNMENT),
                this::parseExpression);

        assignment.addChildren(children);
        return assignment;
    }

    /** Declaration : var Identifier [ := Expression ] ; */
    private AstNode parseDeclaration(AstNode parent) {
        AstGrammarNode declaration = new AstGrammarNode(DECLARATION, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(declaration,
                parseToken(LexTokenCode.VAR),
                parseToken(LexTokenCode.IDENTIFIER)));

        children.addAll(parseOptionalSeries(declaration,
                parseToken(LexTokenCode.ASSIGNMENT),
                this::parseExpression));

        children.addAll(parseSeries(declaration,
                parseToken(LexTokenCode.SEMICOLON)));

        declaration.addChildren(children);
        return declaration;
    }

    /** Print : print Expression { , Expression } */
    private AstNode parsePrint(AstNode parent) {
        AstGrammarNode print = new AstGrammarNode(PRINT, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(print,
                parseToken(LexTokenCode.PRINT),
                this::parseExpression));
        children.addAll(parseRepeated(print,
                parseToken(LexTokenCode.COMMA),
                this::parseExpression));

        print.addChildren(children);
        return print;
    }

    private AstNode parsePrimary(AstNode parent) {
        return null;
    }

    private AstNode parseExpression(AstNode parent) {
//        AstNode firstTermTemp = parseTerm(null);
//
//        if(!iterator.hasNext()) return new AstNode(firstTermTemp.getData(), parent);
//        incrementCurrentPosition();
//
//        LexToken nextToken = tokens.get(currentPosition);
//
//        AstNode currentExpressionTemp = null;
//        switch (nextToken.getCode()) {
//            case ADDITION, SUBTRACTION -> {
//                AstNode op = new AstNode(nextToken, null);
//                AstNode firstTerm = new AstNode(firstTermTemp.getData(), op);
//                op.addChild(firstTerm);
//                incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of token list. But expected term.");
//                AstNode secondTerm = parseTerm(op);
//                op.addChild(secondTerm);
//                currentExpressionTemp = op;
//            }
//
//            default -> {
//                decrementCurrentPosition();
//                return new AstNode(firstTermTemp.getData(), parent);
//            }
//        }
//
//        while (true) {
//            if(!possibleIncrementCurrentPosition()) {
//                AstNode result = new AstNode(currentExpressionTemp.getData(), parent);
//                result.addChildren(currentExpressionTemp.getChildren().stream().map(x -> new AstNode(x.getData(), result, x.getChildren())).toList());
//                return result;
//            }
//            incrementCurrentPosition();
//
//            LexToken next = tokens.get(currentPosition);
//            switch (next.getCode()) {
//                case ADDITION, SUBTRACTION -> {
//                    AstNode op = new AstNode(next, currentExpressionTemp);
//                    op.addChild(currentExpressionTemp);
//                    incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of token list. But expected term.");
//                    AstNode secondTerm = parseTerm(op);
//                    op.addChild(secondTerm);
//                    currentExpressionTemp = op;
//                }
//
//                default -> {
//                    decrementCurrentPosition();
//                    AstNode result = new AstNode(currentExpressionTemp.getData(), parent);
//                    result.addChildren(currentExpressionTemp.getChildren().stream().map(x -> new AstNode(x.getData(), result, x.getChildren())).toList());
//                    return result;
//                }
//            }
//        }
        return null;
    }

    private AstNode parseRelations(AstNode parent) {
        return null;
    }

    private AstNode parseFactor(AstNode parent) {
        return null;
    }

    private AstNode parseTypeIndicator(AstNode parent) {
//        if (!iterator.hasNext()) {
//            throw new SyntaxAnalyzerParseException("Expected type indicator, but found nothing.");
//        }
//
//        AstNode result = new AstGrammarNode(TYPE_INDICATOR, parent);
//        LexToken nextToken = iterator.next();
//        switch (nextToken.getCode()) {
//            case LITERAL, EMPTY, FUNC -> {
//                result.addChild(new AstTokenNode(nextToken, result));
//                return result;
//            }
//
//            case OPEN_CURLY_BRACKET -> {
//                if(!iterator.hasNext() || iterator.next().getCode() != LexTokenCode.CLOSED_CURLY_BRACKET) {
//                    throw new SyntaxAnalyzerParseException("Expected ")
//                }
//            }
//
//        }
        return null;
    }

    private AstNode parseTerm(AstNode parent) {
//        AstNode factor = parseFactor(parent);
//
//        if(!possibleIncrementCurrentPosition()) return factor;
//        incrementCurrentPosition();
//
//        LexToken currentToken = tokens.get(currentPosition);
//        switch (currentToken.getCode()) {
//            case MULTIPLICATION, DIVISION -> {
//                AstNode resOp = new AstNode(currentToken, parent);
//                AstNode newFactor = new AstNode(factor.getData(), resOp);
//                resOp.addChild(newFactor);
//                AstNode term = parseTerm(resOp);
//                resOp.addChild(term);
//                return resOp;
//            }
//
//            default -> {
//                decrementCurrentPosition();
//                return factor;
//            }
//        }
        return null;
    }

//    private AstNode parseFactor(AstNode parent) {
//        LexToken currentToken = tokens.get(currentPosition);
//        switch (currentToken.getCode()) {
//            case IDENTIFIER -> {
//                if (currentToken instanceof LexIdentifierToken) {
//                    return new AstNode(currentToken, parent);
//                } else {
//                    throw new IllegalStateException(String.format("Unexpected instance of class for %s lex currentToken", LexTokenCode.IDENTIFIER));
//                }
//            }
//
//            case LITERAL -> {
//                if (currentToken instanceof LexLiteralToken) {
//                    return new AstNode(currentToken, parent);
//                } else {
//                    throw new IllegalStateException(String.format("Unexpected instance of class for %s lex currentToken", LexTokenCode.LITERAL));
//                }
//            }
//
//            case OPEN_ROUND_BRACKET -> {
//                incrementCurrentPositionOrError("Couldn't parse expression. Unexpected end of lex list.");
//                AstNode expression = parseExpression(parent);
//                incrementCurrentPositionOrError("Couldn't parse expression factor, unexpected end of currentToken list.");
//                LexToken nextToken = tokens.get(currentPosition);
//                if(nextToken.getCode() == LexTokenCode.CLOSED_ROUND_BRACKET) {
//                    return expression;
//                } else {
//                    throw new SyntaxAnalyzerParseException(String.format("Couldn't parse expression, expected currentToken %s, but got %s",
//                            LexTokenCode.CLOSED_ROUND_BRACKET, nextToken.getCode()));
//                }
//            }
//
//            case SUBTRACTION, ADDITION -> {
//                AstNode addOp = new AstNode(currentToken, parent);
//                AstNode factor = parseFactor(addOp);
//                addOp.addChild(factor);
//                return addOp;
//            }
//
//            default ->
//                throw new SyntaxAnalyzerParseException(String.format("Expected factor, but got %s", currentToken));
//            return null;
//    }

}
