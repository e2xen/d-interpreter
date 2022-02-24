package com.projectd.interpreter.syntax;

import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;
import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.shared.exception.ExceptionFactory;
import com.projectd.interpreter.shared.exception.SyntaxAnalyzerParseException;
import com.projectd.interpreter.syntax.iterator.LexTokenIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
                break;
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
        AstGrammarNode statement = new AstGrammarNode(AstGrammarNodeType.STATEMENT, parent);

        AstNode child = parseAnyOf(statement,
                this::parseAssignment,
                this::parseDeclaration,
                this::parsePrint,
                this::parseReturn,
                this::parseIf,
                this::parseLoop);

        if (child == null) {
            if(iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.STATEMENT, span.getLineNum(), span.getPos());
        }

        statement.addChild(child);
        return statement;
    }

    /** Assignment : Primary := Expression */
    private AstNode parseAssignment(AstNode parent) {
        AstGrammarNode assignment = new AstGrammarNode(AstGrammarNodeType.ASSIGNMENT, parent);

        List<AstNode> children = parseSeries(assignment,
                this::parsePrimary,
                parseToken(LexTokenCode.ASSIGNMENT),
                this::parseExpression);

        assignment.addChildren(children);
        return assignment;
    }

    /** Declaration : var Identifier [ := Expression ] ; */
    private AstNode parseDeclaration(AstNode parent) {
        AstGrammarNode declaration = new AstGrammarNode(AstGrammarNodeType.DECLARATION, parent);

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
        AstGrammarNode print = new AstGrammarNode(AstGrammarNodeType.PRINT, parent);

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
        AstGrammarNode returnNode = new AstGrammarNode(AstGrammarNodeType.RETURN, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(returnNode,
                parseToken(LexTokenCode.RETURN),
                parseToken(LexTokenCode.OPEN_SQUARE_BRACKET),
                this::parseExpression,
                parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET)));

        returnNode.addChildren(children);
        return returnNode;
    }

    /** If : if Expression then Body [ else Body ] end */
    private AstNode parseIf(AstNode parent) {
        AstGrammarNode ifNode = new AstGrammarNode(AstGrammarNodeType.IF, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(ifNode,
                parseToken(LexTokenCode.IF),
                this::parseExpression,
                parseToken(LexTokenCode.THEN),
                this::parseBody));

        children.addAll(parseOptionalSeries(ifNode,
                parseToken(LexTokenCode.ELSE),
                this::parseBody,
                parseToken(LexTokenCode.END)));

        ifNode.addChildren(children);
        return ifNode;
    }




    /** Loop : while Expression LoopBody | for Identifier in TypeIndicator LoopBody */
    private AstNode parseLoop(AstNode parent) {
        AstNode loop = parseAnyOf(parent,
                this::parseLoopFor,
                this::parseLoopWhile);

        if (loop == null) {
            if(iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.LOOP, span.getLineNum(), span.getPos());
        }

        return loop;
    }

    /** Loop : while Expression LoopBody */
    private AstNode parseLoopWhile(AstNode parent) {
        AstNode loopWhile = new AstGrammarNode(AstGrammarNodeType.LOOP, parent);

        List<AstNode> children = parseSeries(loopWhile,
                parseToken(LexTokenCode.WHILE),
                this::parseExpression,
                this::parseLoopBody);

        loopWhile.addChildren(children);
        return loopWhile;
    }

    /** Loop : for Identifier in TypeIndicator LoopBody */
    private AstNode parseLoopFor(AstNode parent) {
        AstNode loopFor = new AstGrammarNode(AstGrammarNodeType.LOOP, parent);

        List<AstNode> children = parseSeries(loopFor,
                parseToken(LexTokenCode.FOR),
                parseToken(LexTokenCode.IDENTIFIER),
                parseToken(LexTokenCode.IN),
                this::parseTypeIndicator,
                this::parseLoopBody);

        loopFor.addChildren(children);
        return loopFor;
    }



    /** LoopBody : loop Body end */
    private AstNode parseLoopBody(AstNode parent) {
        AstNode loopBody = new AstGrammarNode(AstGrammarNodeType.LOOP_BODY, parent);

        List<AstNode> children = parseSeries(loopBody,
                parseToken(LexTokenCode.LOOP),
                this::parseBody,
                parseToken(LexTokenCode.END));

        loopBody.addChildren(children);
        return loopBody;
    }




    /** Body : { Declaration | Statement | Expression } */
    private AstNode parseBody(AstNode parent) {
        AstGrammarNode body = new AstGrammarNode(AstGrammarNodeType.BODY, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseRepeated(body,
                this::parseBodyHelper));

        body.addChildren(children);
        return body;
    }

    /** Body : { Declaration | Statement | Expression } */
    private AstNode parseBodyHelper(AstNode parent) {
        AstNode body = parseAnyOf(parent,
                this::parseDeclaration,
                this::parseStatement,
                this::parseExpression);

        if (body == null) {
            if(iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.BODY, span.getLineNum(), span.getPos());
        }

        return body;
    }




    /** Primary: Identifier { Tail } | readInt | readReal | readString */
    private AstNode parsePrimary(AstNode parent) {
        AstNode primary = parseAnyOf(parent,
                this::parsePrimaryIdentifier,
                this::parsePrimaryTokens);

        if (primary == null) {
            if(!iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.PRIMARY, span.getLineNum(), span.getPos());
        }

        return primary;
    }

    /** Primary: Identifier { Tail } */
    private AstNode parsePrimaryIdentifier(AstNode parent) {
        AstGrammarNode primary = new AstGrammarNode(AstGrammarNodeType.PRIMARY, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(primary,
                parseToken(LexTokenCode.IDENTIFIER)));
        children.addAll(parseRepeated(primary,
                this::parseTail));

        primary.addChildren(children);
        return primary;
    }

    /** Primary: readInt | readReal | readString */
    private AstNode parsePrimaryTokens(AstNode parent) {
        AstGrammarNode primary = new AstGrammarNode(AstGrammarNodeType.PRIMARY, parent);
        Set<LexTokenCode> expectedCodes = Set.of(LexTokenCode.READ_INT, LexTokenCode.READ_REAL, LexTokenCode.READ_STRING);

        List<AstNode> children = parseSeries(primary,
                parseToken(expectedCodes));

        primary.addChildren(children);
        return primary;
    }




    /** Tail : .IntegerLiteral | .Identifier | [Expression] | (Expression {, Expression}) */
    private AstNode parseTail(AstNode parent) {
        AstNode tail = parseAnyOf(parent,
                this::parseTailUnnamedTupleElement,
                this::parseTailNamedTupleElement,
                this::parseTailArrayElement,
                this::parseTailFunctionCall);

        if (tail == null) {
            if(!iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.TAIL, span.getLineNum(), span.getPos());
        }

        return tail;
    }

    /** Tail: .IntegerLiteral */
    private AstNode parseTailUnnamedTupleElement(AstNode parent) {
        AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);
        List<AstNode> children = parseSeries(tail, parseToken(LexTokenCode.DOT), parseLiteralToken(LexLiteralTokenType.INTEGER));

        tail.addChildren(children);
        return tail;
    }

    /** Tail: .Identifier */
    private AstNode parseTailNamedTupleElement(AstNode parent) {
        AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);
        List<AstNode> children = parseSeries(tail, parseToken(LexTokenCode.DOT), parseToken(LexTokenCode.IDENTIFIER));

        tail.addChildren(children);
        return tail;
    }

    /** Tail: [Expression] */
    private AstNode parseTailArrayElement(AstNode parent) {
        AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);
        List<AstNode> children = parseSeries(tail,
                parseToken(LexTokenCode.OPEN_SQUARE_BRACKET),
                this::parseExpression,
                parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET));

        tail.addChildren(children);
        return tail;
    }

    /** Tail: (Expression {, Expression}) */
    private AstNode parseTailFunctionCall(AstNode parent) {
        AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(tail,
                parseToken(LexTokenCode.OPEN_CURLY_BRACKET),
                this::parseExpression));
        children.addAll(parseRepeated(tail,
                parseToken(LexTokenCode.COMMA),
                this::parseExpression));
        children.addAll(parseSeries(tail,
                parseToken(LexTokenCode.CLOSED_CURLY_BRACKET)));

        tail.addChildren(children);
        return tail;
    }




    /** Expression: Relation { ( and | or | xor ) Relation } */
    private AstNode parseExpression(AstNode parent) {
        AstNode expression = new AstGrammarNode(AstGrammarNodeType.EXPRESSION, parent);
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
        AstNode relation = new AstGrammarNode(AstGrammarNodeType.RELATION, parent);

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
        AstNode factor = new AstGrammarNode(AstGrammarNodeType.FACTOR, parent);

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
        AstNode term = new AstGrammarNode(AstGrammarNodeType.TERM, parent);

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
        AstNode unary = parseAnyOf(parent,
                this::parseUnaryTypePrimary,
                this::parseUnaryLiteral,
                this::parseUnaryTypeExpression);

        if (unary == null) {
            if(!iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.UNARY, span.getLineNum(), span.getPos());
        }

        return unary;
    }

    /** Unary : [ + | - | not ] Primary [ is TypeIndicator ] */
    private AstNode parseUnaryTypePrimary(AstNode parent) {
        AstNode unary = new AstGrammarNode(AstGrammarNodeType.UNARY, parent);

        List<AstNode> children = new ArrayList<>();
        Set<LexTokenCode> codes = Set.of(LexTokenCode.ADDITION, LexTokenCode.SUBTRACTION, LexTokenCode.NOT);
        children.addAll(parseOptionalSeries(unary,
                parseToken(codes)));

        children.addAll(parseSeries(unary,
                this::parsePrimary));

        children.addAll(parseOptionalSeries(unary,
                parseToken(LexTokenCode.IS),
                this::parseTypeIndicator));

        unary.addChildren(children);
        return unary;
    }

    /** Unary : ( Expression ) */
    private AstNode parseUnaryTypeExpression(AstNode parent) {
        AstNode unary = new AstGrammarNode(AstGrammarNodeType.UNARY, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(unary,
                parseToken(LexTokenCode.OPEN_ROUND_BRACKET),
                this::parseExpression,
                parseToken(LexTokenCode.CLOSED_ROUND_BRACKET)));

        unary.addChildren(children);
        return unary;
    }


    /** Literal : IntegerLiteral | RealLiteral | BooleanLiteral | StringLiteral | ArrayLiteral | TupleLiteral | FunctionLiteral */
    private AstNode parseUnaryLiteral(AstNode parent) {
        AstNode literal = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);

        AstNode child = parseAnyOf(parent,
                this::parseUnaryIntegerLiteral,
                this::parseUnaryRealLiteral,
                this::parseUnaryBooleanLiteral,
                this::parseUnaryStringLiteral,
                this::parseUnaryArrayLiteral,
                this::parseUnaryTupleLiteral,
                this::parseUnaryFunctionLiteral);

        if (child == null) {
            if(!iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.TAIL, span.getLineNum(), span.getPos());
        }

        literal.addChild(child);
        return literal;
    }

    /** Literal : IntegerLiteral */
    private AstNode parseUnaryIntegerLiteral(AstNode parent) {
        AstNode unaryInteger = new AstGrammarNode(AstGrammarNodeType.INTEGER_LITERAL, parent);

        List<AstNode> children = parseSeries(unaryInteger,
                parseLiteralToken(LexLiteralTokenType.INTEGER));

        unaryInteger.addChildren(children);
        return unaryInteger;
    }

    /** Literal : RealLiteral */
    private AstNode parseUnaryRealLiteral(AstNode parent) {
        AstNode unaryReal = new AstGrammarNode(AstGrammarNodeType.REAL_LITERAL, parent);

        List<AstNode> children = parseSeries(unaryReal,
                parseLiteralToken(LexLiteralTokenType.REAL));

        unaryReal.addChildren(children);
        return unaryReal;
    }

    /** Literal : BooleanLiteral */
    private AstNode parseUnaryBooleanLiteral(AstNode parent) {
        AstNode unaryBoolean = new AstGrammarNode(AstGrammarNodeType.BOOLEAN_LITERAL, parent);

        List<AstNode> children = parseSeries(unaryBoolean,
                parseLiteralToken(LexLiteralTokenType.BOOLEAN));

        unaryBoolean.addChildren(children);
        return unaryBoolean;
    }

    /** Literal : StringLiteral */
    private AstNode parseUnaryStringLiteral(AstNode parent) {
        AstNode unaryString = new AstGrammarNode(AstGrammarNodeType.STRING_LITERAL, parent);

        List<AstNode> children = parseSeries(unaryString,
                parseLiteralToken(LexLiteralTokenType.STRING));

        unaryString.addChildren(children);
        return unaryString;
    }

    /** ArrayLiteral : [ [ Expression { , Expression } ] ] */
    private AstNode parseUnaryArrayLiteral(AstNode parent) {
        AstNode unaryArray = new AstGrammarNode(AstGrammarNodeType.ARRAY_LITERAL, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(unaryArray,
                parseToken(LexTokenCode.OPEN_SQUARE_BRACKET)));

        children.addAll(parseOptionalSeries(unaryArray,
                this::parseExpression));
        children.addAll(parseRepeated(unaryArray,
                parseToken(LexTokenCode.COMMA),
                this::parseExpression));

        children.addAll(parseSeries(unaryArray,
                parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET)));

        unaryArray.addChildren(children);
        return unaryArray;
    }

    /** TupleLiteral : '{' [ TupleElement { ',' TupleElement } ] '}' */
    private AstNode parseUnaryTupleLiteral(AstNode parent) {
        AstNode unaryTuple = new AstGrammarNode(AstGrammarNodeType.TUPLE_LITERAL, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(unaryTuple,
                parseToken(LexTokenCode.OPEN_ROUND_BRACKET)));

        children.addAll(parseOptionalSeries(unaryTuple,
                this::parseUnaryUnaryTupleElement));
        children.addAll(parseRepeated(unaryTuple,
                this::parseUnaryUnaryTupleElement));

        children.addAll(parseSeries(unaryTuple,
                parseToken(LexTokenCode.CLOSED_CURLY_BRACKET)));

        unaryTuple.addChildren(children);
        return unaryTuple;
    }

    /** TupleElement : [ Identifier := ] Expression */
    private AstNode parseUnaryUnaryTupleElement(AstNode parent) {
        AstNode tupleElement = new AstGrammarNode(AstGrammarNodeType.TUPLE_ELEMENT, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseOptionalSeries(tupleElement,
                parseToken(LexTokenCode.IDENTIFIER),
                parseToken(LexTokenCode.ASSIGNMENT)));
        children.addAll(parseSeries(tupleElement,
                this::parseExpression));

        tupleElement.addChildren(children);
        return tupleElement;
    }

    /** FunctionLiteral : func [ Parameters ] FunBody */
    private AstNode parseUnaryFunctionLiteral(AstNode parent) {
        AstNode unaryFunction = new AstGrammarNode(AstGrammarNodeType.FUNCTION_LITERAL, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(unaryFunction,
                parseToken(LexTokenCode.FUNC)));

        children.addAll(parseOptionalSeries(unaryFunction,
                this::parseFunctionParameters));

        children.addAll(parseSeries(unaryFunction,
                this::parseFunctionBody));

        unaryFunction.addChildren(children);
        return unaryFunction;
    }





    /** Parameters : ( Identifier { , Identifier } ) */
    private AstNode parseFunctionParameters(AstNode parent) {
        AstNode parameters = new AstGrammarNode(AstGrammarNodeType.PARAMETERS, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(parameters,
                parseToken(LexTokenCode.OPEN_ROUND_BRACKET),
                parseToken(LexTokenCode.IDENTIFIER)));

        children.addAll(parseRepeated(parameters,
                parseToken(LexTokenCode.COMMA),
                parseToken(LexTokenCode.IDENTIFIER)));

        children.addAll(parseSeries(parameters,
                parseToken(LexTokenCode.CLOSED_ROUND_BRACKET)));


        parameters.addChildren(children);
        return parameters;
    }





    /** FunBody : is Body end | => Expression */
    private AstNode parseFunctionBody(AstNode parent) {
        AstNode funBody = parseAnyOf(parent,
                this::parseFunctionBodyWithContent,
                this::parseFunctionBodyExpression);

        if (funBody == null) {
            if(!iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.FUN_BODY, span.getLineNum(), span.getPos());
        }

        return funBody;
    }

    /** FunBody : is Body end */
    private AstNode parseFunctionBodyWithContent(AstNode parent) {
        AstNode funBody = new AstGrammarNode(AstGrammarNodeType.FUN_BODY, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(funBody,
                parseToken(LexTokenCode.IS),
                this::parseBody,
                parseToken(LexTokenCode.END)));

        funBody.addChildren(children);
        return funBody;
    }

    /** FunBody : => Expression */
    private AstNode parseFunctionBodyExpression(AstNode parent) {
        AstNode funBody = new AstGrammarNode(AstGrammarNodeType.FUN_BODY, parent);

        List<AstNode> children = parseSeries(funBody,
                parseToken(LexTokenCode.MORE_OR_EQUAL),
                this::parseExpression);

        funBody.addChildren(children);
        return funBody;
    }




    /** TypeIndicator: int | real | bool | string | empty | [ ] | { } | func | Expression .. Expression */
    private AstNode parseTypeIndicator(AstNode parent) {
        AstNode typeIndicator = parseAnyOf(parent,
                this::parseTypeIndicatorBasicKeywords,
                this::parseTypeIndicatorArray,
                this::parseTypeIndicatorTuple,
                this::parseTypeIndicatorRange);

        if (typeIndicator == null) {
            if(!iterator.hasNext()) {
                throw ExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw ExceptionFactory.ambiguousGrammar(AstGrammarNodeType.TYPE_INDICATOR, span.getLineNum(), span.getPos());
        }

        return typeIndicator;
    }

    /** TypeIndicator: int | real | bool | string | empty | func */
    private AstNode parseTypeIndicatorBasicKeywords(AstNode parent) {
        AstNode typeIndicator = new AstGrammarNode(AstGrammarNodeType.TYPE_INDICATOR, parent);

        List<AstNode> children = new ArrayList<>();
        Set<LexTokenCode> codes = Set.of(
                LexTokenCode.INTEGER,
                LexTokenCode.REAL,
                LexTokenCode.BOOLEAN,
                LexTokenCode.STRING,
                LexTokenCode.EMPTY,
                LexTokenCode.FUNC);
        children.addAll(parseSeries(typeIndicator,
                parseToken(codes)));

        typeIndicator.addChildren(children);
        return typeIndicator;
    }

    /** TypeIndicator: [ ] */
    private AstNode parseTypeIndicatorArray(AstNode parent) {
        AstNode typeIndicator = new AstGrammarNode(AstGrammarNodeType.TYPE_INDICATOR, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(typeIndicator,
                parseToken(LexTokenCode.OPEN_SQUARE_BRACKET),
                parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET)));

        typeIndicator.addChildren(children);
        return typeIndicator;
    }

    /** TypeIndicator: { } */
    private AstNode parseTypeIndicatorTuple(AstNode parent) {
        AstNode typeIndicator = new AstGrammarNode(AstGrammarNodeType.TYPE_INDICATOR, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(typeIndicator,
                parseToken(LexTokenCode.OPEN_CURLY_BRACKET),
                parseToken(LexTokenCode.CLOSED_CURLY_BRACKET)));

        typeIndicator.addChildren(children);
        return typeIndicator;
    }

    /** TypeIndicator: Expression .. Expression */
    private AstNode parseTypeIndicatorRange(AstNode parent) {
        AstNode typeIndicator = new AstGrammarNode(AstGrammarNodeType.TYPE_INDICATOR, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(typeIndicator,
                this::parseExpression,
                parseToken(LexTokenCode.DOT),
                parseToken(LexTokenCode.DOT),
                this::parseExpression));

        typeIndicator.addChildren(children);
        return typeIndicator;
    }


}
