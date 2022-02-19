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

    private AstTokenNode parseToken(Set<LexTokenCode> expectedTokens, AstNode parent) {
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

    private AstNode parseLiteralToken(Set<LexLiteralTokenType> expectedLiteralTypes, AstNode parent) {
        Set<LexTokenCode> expectedTokens = Set.of(LexTokenCode.LITERAL);
        AstTokenNode token = parseToken(expectedTokens, parent);

        if(!(token.getToken() instanceof LexLiteralToken literalToken)) {
            LexTokenSpan nextSpan = token.getToken().getSpan();
            throw ExceptionFactory.unexpectedToken(expectedTokens, token.getToken().getCode(), nextSpan.getLineNum(), nextSpan.getPos());
        }

        if(!expectedLiteralTypes.contains(literalToken.getType())) {
            LexTokenSpan nextSpan = token.getToken().getSpan();
            throw ExceptionFactory.unexpectedLiteralToken(expectedLiteralTypes, token.getToken().getCode(), nextSpan.getLineNum(), nextSpan.getPos());
        }

        return token;
    }

    private Function<AstNode, AstNode> parseToken(Set<LexTokenCode> expectedTokens) {
        return parent -> parseToken(expectedTokens, parent);
    }

    private Function<AstNode, AstNode> parseToken(LexTokenCode expectedToken) {
        return parent -> parseToken(Set.of(expectedToken), parent);
    }

    private Function<AstNode, AstNode> parseLiteralToken(LexLiteralTokenType expectedLiteralTokenType) {
        return parent -> parseLiteralToken(Set.of(expectedLiteralTokenType), parent);
    }

    /** Statement : { Assignment | Declaration | Print | Return | If | Loop } */
    private AstNode parseStatement(AstNode parent) {
        AstGrammarNode statement = new AstGrammarNode(STATEMENT, parent);
        // TODO: add child to parent

        AstNode child = parseAnyOf(statement,
                this::parseAssignment,
                this::parseDeclaration,
                this::parsePrint,
                this::parseReturn
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

    /** Return : return [ Expression ] */
    private AstNode parseReturn(AstNode parent) {
        AstGrammarNode returnNode = new AstGrammarNode(RETURN, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(parent,
                parseToken(LexTokenCode.RETURN),
                parseToken(LexTokenCode.OPEN_SQUARE_BRACKET),
                this::parseExpression,
                parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET)));

        return returnNode;
    }




    /** Primary: Identifier { Tail } | readInt | readReal | readString */
    private AstNode parsePrimary(AstNode parent) {
        AstGrammarNode primary = new AstGrammarNode(PRIMARY, parent);
        AstNode child = parseAnyOf(primary,
                this::parsePrimaryIdentifier,
                parseToken(LexTokenCode.READ_INT),
                parseToken(LexTokenCode.READ_REAL),
                parseToken(LexTokenCode.READ_STRING)
        );

        if (child == null) {
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(PRIMARY, span.getLineNum(), span.getPos());
        }

        primary.addChild(child);
        return primary;
    }

    /** Primary: Identifier { Tail } */
    private AstNode parsePrimaryIdentifier(AstNode parent) {
        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(parent,
                parseToken(LexTokenCode.IDENTIFIER)));
        children.addAll(parseRepeated(parent,
                this::parseTail));

        parent.addChildren(children);
        return parent;
    }




    /** Tail : .IntegerLiteral | .Identifier | [Expression] | (Expression {, Expression}) */
    private AstNode parseTail(AstNode parent) {
        AstGrammarNode tail = new AstGrammarNode(TAIL, parent);

        AstNode child = parseAnyOf(tail,
                this::parseTailUnnamedTupleElement,
                this::parseTailNamedTupleElement,
                this::parseTailArrayElement,
                this::parseTailFunctionCall
        );

        if (child == null) {
            // TODO: fail-safe
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(TAIL, span.getLineNum(), span.getPos());
        }
        tail.addChild(child);
        return tail;
    }

    /** Tail: .IntegerLiteral */
    private AstNode parseTailUnnamedTupleElement(AstNode parent) {
        List<AstNode> children = parseSeries(parent, parseToken(LexTokenCode.DOT), parseLiteralToken(LexLiteralTokenType.INTEGER));
        parent.addChildren(children);
        return parent;
    }

    /** Tail: .Identifier */
    private AstNode parseTailNamedTupleElement(AstNode parent) {
        List<AstNode> children = parseSeries(parent, parseToken(LexTokenCode.DOT), parseToken(LexTokenCode.IDENTIFIER));
        parent.addChildren(children);
        return parent;
    }

    /** Tail: [Expression] */
    private AstNode parseTailArrayElement(AstNode parent) {
        List<AstNode> children = parseSeries(parent, parseToken(LexTokenCode.OPEN_SQUARE_BRACKET),
                this::parseExpression,
                parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET));
        parent.addChildren(children);
        return parent;
    }

    /** Tail: (Expression {, Expression}) */
    private AstNode parseTailFunctionCall(AstNode parent) {
        List<AstNode> children = new ArrayList<>();

        children.addAll(parseSeries(parent,
                parseToken(LexTokenCode.OPEN_CURLY_BRACKET),
                this::parseExpression));
        children.addAll(parseRepeated(parent,
                parseToken(LexTokenCode.COMMA),
                this::parseExpression));

        children.addAll(parseSeries(parent,
                parseToken(LexTokenCode.CLOSED_CURLY_BRACKET)));

        parent.addChildren(children);
        return parent;
    }




    /** Expression: Relation { ( and | or | xor ) Relation } */
    private AstNode parseExpression(AstNode parent) {
        AstNode expression = new AstGrammarNode(RELATION, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(expression,
                this::parseRelation));
        children.addAll(parseRepeated(expression,
                parseToken(Set.of(LexTokenCode.AND, LexTokenCode.OR, LexTokenCode.XOR)),
                this::parseRelation));

        expression.addChildren(children);
        return expression;
    }

    /** Relation : Factor [ ( < | <= | > | >= | = | /= ) Factor ] */
    private AstNode parseRelation(AstNode parent) {
        AstNode relation = new AstGrammarNode(RELATION, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(relation,
                this::parseFactor));

        Set<LexTokenCode> codes = Set.of(LexTokenCode.LESS,
                LexTokenCode.LESS_OR_EQUAL,
                LexTokenCode.MORE,
                LexTokenCode.MORE_OR_EQUAL,
                LexTokenCode.EQUAL,
                LexTokenCode.NOT_EQUAL);
        children.addAll(parseOptionalSeries(relation,
                parseToken(codes),
                this::parseFactor));

        relation.addChildren(children);
        return relation;
    }

    /** Factor : Term { ( + | - ) Term } */
    private AstNode parseFactor(AstNode parent) {
        AstNode factor = new AstGrammarNode(FACTOR, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(factor,
                this::parseTerm));

        Set<LexTokenCode> codes = Set.of(LexTokenCode.ADDITION, LexTokenCode.SUBTRACTION);
        children.addAll(parseRepeated(factor,
                parseToken(codes),
                this::parseTerm));

        factor.addChildren(children);
        return factor;
    }

    /** Term : Unary { ( * | / ) Unary } */
    private AstNode parseTerm(AstNode parent) {
        AstNode term = new AstGrammarNode(TERM, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(term,
                this::parseUnary));

        Set<LexTokenCode> codes = Set.of(LexTokenCode.MULTIPLICATION, LexTokenCode.DIVISION);
        children.addAll(parseRepeated(term,
                parseToken(codes),
                this::parseUnary));

        term.addChildren(children);
        return term;
    }




    /** Unary : [ + | - | not ] Primary [ is TypeIndicator ] | Literal | ( Expression ) */
    private AstNode parseUnary(AstNode parent) {
        AstNode unary = new AstGrammarNode(UNARY, parent);

        AstNode child = parseAnyOf(unary,
                this::parseUnaryTypePrimary,
                parseToken(LexTokenCode.LITERAL),
                this::parseUnaryTypeExpression);

        if (child == null) {
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(UNARY, span.getLineNum(), span.getPos());
        }

        unary.addChild(child);
        return unary;
    }

    /** Unary : [ + | - | not ] Primary [ is TypeIndicator ] */
    private AstNode parseUnaryTypePrimary(AstNode parent) {
        List<AstNode> children = new ArrayList<>();
        Set<LexTokenCode> codes = Set.of(LexTokenCode.ADDITION, LexTokenCode.SUBTRACTION, LexTokenCode.NOT);
        children.addAll(parseOptionalSeries(parent,
                parseToken(codes)));

        children.addAll(parseSeries(parent,
                this::parsePrimary));

        children.addAll(parseOptionalSeries(parent,
                parseToken(LexTokenCode.IS),
                this::parseTypeIndicator));

        parent.addChildren(children);
        return parent;
    }

    /** Unary : ( Expression ) */
    private AstNode parseUnaryTypeExpression(AstNode parent) {
        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(parent,
                parseToken(LexTokenCode.OPEN_ROUND_BRACKET),
                this::parseExpression,
                parseToken(LexTokenCode.CLOSED_ROUND_BRACKET)));

        parent.addChildren(children);
        return parent;
    }




    /** TypeIndicator: int | real | bool | string | empty | [ ] | { } | func | Expression .. Expression */
    private AstNode parseTypeIndicator(AstNode parent) {
        AstNode typeIndicator = new AstGrammarNode(TYPE_INDICATOR, parent);

        AstNode child = parseAnyOf(typeIndicator,
                parseToken(LexTokenCode.INTEGER),
                parseToken(LexTokenCode.REAL),
                parseToken(LexTokenCode.BOOLEAN),
                parseToken(LexTokenCode.STRING),
                parseToken(LexTokenCode.EMPTY),
                this::parseTypeIndicatorArray,
                this::parseTypeIndicatorTuple,
                parseToken(LexTokenCode.FUNC),
                this::parseTypeIndicatorRange);

        if (child == null) {
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(TYPE_INDICATOR, span.getLineNum(), span.getPos());
        }

        typeIndicator.addChild(child);
        return typeIndicator;
    }

    /** TypeIndicator: [ ] */
    private AstNode parseTypeIndicatorArray(AstNode parent) {
        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(parent,
                parseToken(LexTokenCode.OPEN_SQUARE_BRACKET),
                parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET)));

        parent.addChildren(children);
        return parent;
    }

    /** TypeIndicator: { } */
    private AstNode parseTypeIndicatorTuple(AstNode parent) {
        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(parent,
                parseToken(LexTokenCode.OPEN_CURLY_BRACKET),
                parseToken(LexTokenCode.CLOSED_CURLY_BRACKET)));

        parent.addChildren(children);
        return parent;
    }

    /** TypeIndicator: { } */
    private AstNode parseTypeIndicatorRange(AstNode parent) {
        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(parent,
                this::parseExpression,
                parseToken(LexTokenCode.DOT),
                parseToken(LexTokenCode.DOT),
                this::parseExpression));

        parent.addChildren(children);
        return parent;
    }

//    private AstNode parseExpression(AstNode parent) {
//
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
//        return null;
//    }

//    private AstNode parseRelations(AstNode parent) {
//        return null;
//    }

//    private AstNode parseFactor(AstNode parent) {
//        return null;
//    }

//    private AstNode parseTypeIndicator(AstNode parent) {
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
//        return null;
//    }

//    private AstNode parseTerm(AstNode parent) {
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
//        return null;
//    }

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
