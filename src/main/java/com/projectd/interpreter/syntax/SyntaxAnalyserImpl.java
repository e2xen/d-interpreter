package com.projectd.interpreter.syntax;

import com.projectd.interpreter.syntax.contract.SyntaxAnalyserParseGrammar;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.shared.exception.SyntaxExceptionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SyntaxAnalyserImpl extends SyntaxAnalyser {

    private final SyntaxAnalyserParseGrammar parseLoop = new ParseLoop();
    private final SyntaxAnalyserParseGrammar parsePrimary = new ParsePrimary();
    private final SyntaxAnalyserParseGrammar parseTail = new ParseTail();
    private final SyntaxAnalyserParseGrammar parseUnary = new ParseUnary();
    private final SyntaxAnalyserParseGrammar parseFunBody = new ParseFunBody();
    private final SyntaxAnalyserParseGrammar parseTypeIndicator = new ParseTypeIndicator();

    public SyntaxAnalyserImpl(List<LexToken> tokens) {
        super(tokens.stream().filter(t -> !t.getCode().equals(LexTokenCode.SEMICOLON)).collect(Collectors.toList()));
    }

    @Override
    public AstNode buildAstTree() {
        AstNode program = new AstGrammarNode(AstGrammarNodeType.PROGRAM, null);

        while (iterator.hasNext()) {
            program.addChild(parseStatement(program));
        }

        return program;
    }

    /** Statement : Assignment | Declaration | Print | Return | If | Loop | Expression */
    private AstNode parseStatement(AstNode parent) {
        AstGrammarNode statement = new AstGrammarNode(AstGrammarNodeType.STATEMENT, parent);

        AstNode child = parseAnyOf(statement,
                this::parseAssignment,
                this::parseDeclaration,
                this::parsePrint,
                this::parseReturn,
                this::parseIf,
                this.parseLoop::parse,
                this::parseExpression);

        if (child == null) {
            if (!iterator.hasNext()) {
                throw SyntaxExceptionFactory.noToken();
            }
            LexTokenSpan span = iterator.next().getSpan();
            throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.STATEMENT, span.getLineNum(), span.getPos());
        }

        statement.addChild(child);
        return statement;
    }

    /** Assignment : Primary := Expression */
    private AstNode parseAssignment(AstNode parent) {
        AstGrammarNode assignment = new AstGrammarNode(AstGrammarNodeType.ASSIGNMENT, parent);

        List<AstNode> children = parseSeries(assignment,
                this.parsePrimary::parse,
                parseToken(LexTokenCode.ASSIGNMENT),
                this::parseExpression);

        assignment.addChildren(children);
        return assignment;
    }

    /** Declaration : var VariableDefinition { ‘,’ VariableDefinition } */
    private AstNode parseDeclaration(AstNode parent) {
        AstGrammarNode declaration = new AstGrammarNode(AstGrammarNodeType.DECLARATION, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(declaration,
                parseToken(LexTokenCode.VAR),
                this::parseVariableDefinition));

        children.addAll(parseRepeated(declaration,
                parseToken(LexTokenCode.COMMA),
                this::parseVariableDefinition));

        declaration.addChildren(children);
        return declaration;
    }

    /** VariableDefinition : IDENTIFIER [ ':=' Expression ] */
    private AstNode parseVariableDefinition(AstNode parent) {
        AstGrammarNode variableDefinition = new AstGrammarNode(AstGrammarNodeType.VARIABLE_DEFINITION, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(variableDefinition,
                parseToken(LexTokenCode.IDENTIFIER)));

        children.addAll(parseOptionalSeries(variableDefinition,
                parseToken(LexTokenCode.ASSIGNMENT),
                this::parseExpression));

        variableDefinition.addChildren(children);
        return variableDefinition;
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
                parseToken(LexTokenCode.RETURN)));

        children.addAll(parseOptionalSeries(returnNode,
                this::parseExpression));

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
                this::parseBody));

        children.addAll(parseSeries(ifNode,
                parseToken(LexTokenCode.END)));

        ifNode.addChildren(children);
        return ifNode;
    }




    private class ParseLoop implements SyntaxAnalyserParseGrammar {

        /** Loop : while Expression LoopBody | for Identifier in Expression .. Expression LoopBody */
        public AstNode parse(AstNode parent) {
            AstNode loop = parseAnyOf(parent,
                    this::parseLoopFor,
                    this::parseLoopWhile);

            if (loop == null) {
                if (!iterator.hasNext()) {
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.LOOP, span.getLineNum(), span.getPos());
            }

            return loop;
        }

        /** Loop : while Expression LoopBody */
        private AstNode parseLoopWhile(AstNode parent) {
            AstNode loopWhile = new AstGrammarNode(AstGrammarNodeType.LOOP, parent);

            List<AstNode> children = parseSeries(loopWhile,
                    parseToken(LexTokenCode.WHILE),
                    SyntaxAnalyserImpl.this::parseExpression,
                    SyntaxAnalyserImpl.this::parseLoopBody);

            loopWhile.addChildren(children);
            return loopWhile;
        }

        /** Loop : for Identifier in Expression .. Expression LoopBody */
        private AstNode parseLoopFor(AstNode parent) {
            AstNode loopFor = new AstGrammarNode(AstGrammarNodeType.LOOP, parent);

            List<AstNode> children = parseSeries(loopFor,
                    parseToken(LexTokenCode.FOR),
                    parseToken(LexTokenCode.IDENTIFIER),
                    parseToken(LexTokenCode.IN),
                    SyntaxAnalyserImpl.this::parseExpression,
                    parseToken(LexTokenCode.DOT),
                    parseToken(LexTokenCode.DOT),
                    SyntaxAnalyserImpl.this::parseExpression,
                    SyntaxAnalyserImpl.this::parseLoopBody);

            loopFor.addChildren(children);
            return loopFor;
        }
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

    /** Body : { Statement } */
    public AstNode parseBody(AstNode parent) {
        AstGrammarNode body = new AstGrammarNode(AstGrammarNodeType.BODY, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseRepeated(body,
                this::parseStatement));

        body.addChildren(children);
        return body;
    }


    private class ParseBody implements SyntaxAnalyserParseGrammar {

        /** Body : { Declaration | Statement | Expression } */
        public AstNode parse(AstNode parent) {
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
                    SyntaxAnalyserImpl.this::parseDeclaration,
                    SyntaxAnalyserImpl.this::parseStatement,
                    SyntaxAnalyserImpl.this::parseExpression);

            if (body == null) {
                if(iterator.hasNext()) {
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.BODY, span.getLineNum(), span.getPos());
            }

            return body;
        }
    }




    private class ParsePrimary implements SyntaxAnalyserParseGrammar {

        /** Primary : Identifier { Tail } | readInt | readReal | readString */
        public AstNode parse(AstNode parent) {
            AstNode primary = parseAnyOf(parent,
                    this::parsePrimaryIdentifier,
                    this::parsePrimaryTokens);

            if (primary == null) {
                if(!iterator.hasNext()) {
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.PRIMARY, span.getLineNum(), span.getPos());
            }

            return primary;
        }

        /** Primary : Identifier { Tail } */
        private AstNode parsePrimaryIdentifier(AstNode parent) {
            AstGrammarNode primary = new AstGrammarNode(AstGrammarNodeType.PRIMARY, parent);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseSeries(primary,
                    parseToken(LexTokenCode.IDENTIFIER)));
            children.addAll(parseRepeated(primary,
                    SyntaxAnalyserImpl.this.parseTail::parse));

            primary.addChildren(children);
            return primary;
        }

        /** Primary : readInt | readReal | readString */
        private AstNode parsePrimaryTokens(AstNode parent) {
            AstGrammarNode primary = new AstGrammarNode(AstGrammarNodeType.PRIMARY, parent);
            Set<LexTokenCode> expectedCodes = Set.of(LexTokenCode.READ_INT, LexTokenCode.READ_REAL, LexTokenCode.READ_STRING);

            List<AstNode> children = parseSeries(primary,
                    parseToken(expectedCodes));

            primary.addChildren(children);
            return primary;
        }
    }




    private class ParseTail implements SyntaxAnalyserParseGrammar {

        /** Tail : .IntegerLiteral | .Identifier | [Expression] | (Expression {, Expression}) */
        public AstNode parse(AstNode parent) {
            AstNode tail = parseAnyOf(parent,
                    this::parseTailUnnamedTupleElement,
                    this::parseTailNamedTupleElement,
                    this::parseTailArrayElement,
                    this::parseTailFunctionCall);

            if (tail == null) {
                if(!iterator.hasNext()) {
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.TAIL, span.getLineNum(), span.getPos());
            }

            return tail;
        }

        /** Tail : .IntegerLiteral */
        private AstNode parseTailUnnamedTupleElement(AstNode parent) {
            AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);
            List<AstNode> children = parseSeries(tail, parseToken(LexTokenCode.DOT), parseLiteralToken(LexLiteralTokenType.INT));

            tail.addChildren(children);
            return tail;
        }

        /** Tail : .Identifier */
        private AstNode parseTailNamedTupleElement(AstNode parent) {
            AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);
            List<AstNode> children = parseSeries(tail, parseToken(LexTokenCode.DOT), parseToken(LexTokenCode.IDENTIFIER));

            tail.addChildren(children);
            return tail;
        }

        /** Tail : [Expression] */
        private AstNode parseTailArrayElement(AstNode parent) {
            AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);
            List<AstNode> children = parseSeries(tail,
                    parseToken(LexTokenCode.OPEN_SQUARE_BRACKET),
                    SyntaxAnalyserImpl.this::parseExpression,
                    parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET));

            tail.addChildren(children);
            return tail;
        }

        /** Tail : (Expression {, Expression}) */
        private AstNode parseTailFunctionCall(AstNode parent) {
            AstGrammarNode tail = new AstGrammarNode(AstGrammarNodeType.TAIL, parent);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseSeries(tail,
                    parseToken(LexTokenCode.OPEN_ROUND_BRACKET),
                    SyntaxAnalyserImpl.this::parseExpression));

            children.addAll(parseRepeated(tail,
                    parseToken(LexTokenCode.COMMA),
                    SyntaxAnalyserImpl.this::parseExpression));

            children.addAll(parseSeries(tail,
                    parseToken(LexTokenCode.CLOSED_ROUND_BRACKET)));

            tail.addChildren(children);
            return tail;
        }
    }




    /** Expression : Conjunction { ( or | xor ) Conjunction } */
    private AstNode parseExpression(AstNode parent) {
        AstNode expression = new AstGrammarNode(AstGrammarNodeType.EXPRESSION, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(expression,
                this::parseConjunction));
        children.addAll(parseRepeated(expression,
                parseToken(Set.of(LexTokenCode.OR, LexTokenCode.XOR)),
                this::parseConjunction));

        expression.addChildren(children);
        return expression;
    }

    /** Conjunction : Relation { and Relation } */
    private AstNode parseConjunction(AstNode parent) {
        AstNode conjunction = new AstGrammarNode(AstGrammarNodeType.CONJUNCTION, parent);

        List<AstNode> children = new ArrayList<>();
        children.addAll(parseSeries(conjunction,
                this::parseRelation));

        children.addAll(parseRepeated(conjunction,
                parseToken(LexTokenCode.AND),
                this::parseRelation));

        conjunction.addChildren(children);
        return conjunction;
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
                this.parseUnary::parse));

        Set<LexTokenCode> codes = Set.of(LexTokenCode.MULTIPLICATION, LexTokenCode.DIVISION);
        children.addAll(parseRepeated(term,
                parseToken(codes),
                this.parseUnary::parse));

        term.addChildren(children);
        return term;
    }




    private class ParseUnary implements SyntaxAnalyserParseGrammar {

        /** Unary : [ + | - | not ] Primary [ is TypeIndicator ] | Literal | ( Expression ) */
        public AstNode parse(AstNode parent) {
            AstNode unary = parseAnyOf(parent,
                    this::parseUnaryTypePrimary,
                    this::parseUnaryLiteral,
                    this::parseUnaryTypeExpression);

            if (unary == null) {
                if(!iterator.hasNext()) {
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.UNARY, span.getLineNum(), span.getPos());
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
                    SyntaxAnalyserImpl.this.parsePrimary::parse));

            children.addAll(parseOptionalSeries(unary,
                    parseToken(LexTokenCode.IS),
                    SyntaxAnalyserImpl.this.parseTypeIndicator::parse));

            unary.addChildren(children);
            return unary;
        }

        /** Unary : ( Expression ) */
        private AstNode parseUnaryTypeExpression(AstNode parent) {
            AstNode unary = new AstGrammarNode(AstGrammarNodeType.UNARY, parent);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseSeries(unary,
                    parseToken(LexTokenCode.OPEN_ROUND_BRACKET),
                    SyntaxAnalyserImpl.this::parseExpression,
                    parseToken(LexTokenCode.CLOSED_ROUND_BRACKET)));

            unary.addChildren(children);
            return unary;
        }


        /** Literal : IntegerLiteral | RealLiteral | BooleanLiteral | StringLiteral | ArrayLiteral | TupleLiteral
         | FunctionLiteral */
        private AstNode parseUnaryLiteral(AstNode parent) {
            AstNode unary = new AstGrammarNode(AstGrammarNodeType.UNARY, parent);

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
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.LITERAL, span.getLineNum(), span.getPos());
            }

            unary.addChild(child);
            return unary;
        }

        /** Literal : IntegerLiteral */
        private AstNode parseUnaryIntegerLiteral(AstNode parent) {
            AstNode unaryInteger = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);

            List<AstNode> children = parseSeries(unaryInteger,
                    parseLiteralToken(LexLiteralTokenType.INT));

            unaryInteger.addChildren(children);
            return unaryInteger;
        }

        /** Literal : RealLiteral */
        private AstNode parseUnaryRealLiteral(AstNode parent) {
            AstNode unaryReal = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);

            List<AstNode> children = parseSeries(unaryReal,
                    parseLiteralToken(LexLiteralTokenType.REAL));

            unaryReal.addChildren(children);
            return unaryReal;
        }

        /** Literal : BooleanLiteral */
        private AstNode parseUnaryBooleanLiteral(AstNode parent) {
            AstNode unaryBoolean = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);

            List<AstNode> children = parseSeries(unaryBoolean,
                    parseLiteralToken(LexLiteralTokenType.BOOLEAN));

            unaryBoolean.addChildren(children);
            return unaryBoolean;
        }

        /** Literal : StringLiteral */
        private AstNode parseUnaryStringLiteral(AstNode parent) {
            AstNode unaryString = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);

            List<AstNode> children = parseSeries(unaryString,
                    parseLiteralToken(LexLiteralTokenType.STRING));

            unaryString.addChildren(children);
            return unaryString;
        }

        /** ArrayLiteral : [ [ Expression { , Expression } ] ] */
        private AstNode parseUnaryArrayLiteral(AstNode parent) {
            AstNode literal = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);
            AstNode unaryArray = new AstGrammarNode(AstGrammarNodeType.ARRAY_LITERAL, literal);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseSeries(unaryArray,
                    parseToken(LexTokenCode.OPEN_SQUARE_BRACKET)));

            children.addAll(parseOptionalSeries(unaryArray,
                    SyntaxAnalyserImpl.this::parseExpression));
            children.addAll(parseRepeated(unaryArray,
                    parseToken(LexTokenCode.COMMA),
                    SyntaxAnalyserImpl.this::parseExpression));

            children.addAll(parseSeries(unaryArray,
                    parseToken(LexTokenCode.CLOSE_SQUARE_BRACKET)));

            unaryArray.addChildren(children);
            literal.addChild(unaryArray);
            return literal;
        }

        /** TupleLiteral : '{' [ TupleElement { ',' TupleElement } ] '}' */
        private AstNode parseUnaryTupleLiteral(AstNode parent) {
            AstNode literal = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);
            AstNode unaryTuple = new AstGrammarNode(AstGrammarNodeType.TUPLE_LITERAL, literal);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseSeries(unaryTuple,
                    parseToken(LexTokenCode.OPEN_CURLY_BRACKET)));

            children.addAll(parseOptionalSeries(unaryTuple,
                    this::parseUnaryUnaryTupleElement));
            children.addAll(parseRepeated(unaryTuple,
                    parseToken(LexTokenCode.COMMA),
                    this::parseUnaryUnaryTupleElement));

            children.addAll(parseSeries(unaryTuple,
                    parseToken(LexTokenCode.CLOSED_CURLY_BRACKET)));

            unaryTuple.addChildren(children);
            literal.addChild(unaryTuple);
            return literal;
        }

        /** TupleElement : [ Identifier := ] Expression */
        private AstNode parseUnaryUnaryTupleElement(AstNode parent) {
            AstNode tupleElement = new AstGrammarNode(AstGrammarNodeType.TUPLE_ELEMENT, parent);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseOptionalSeries(tupleElement,
                    parseToken(LexTokenCode.IDENTIFIER),
                    parseToken(LexTokenCode.ASSIGNMENT)));
            children.addAll(parseSeries(tupleElement,
                    SyntaxAnalyserImpl.this::parseExpression));

            tupleElement.addChildren(children);
            return tupleElement;
        }

        /** FunctionLiteral : func [ Parameters ] FunBody */
        private AstNode parseUnaryFunctionLiteral(AstNode parent) {
            AstNode literal = new AstGrammarNode(AstGrammarNodeType.LITERAL, parent);
            AstNode unaryFunction = new AstGrammarNode(AstGrammarNodeType.FUNCTION_LITERAL, literal);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseSeries(unaryFunction,
                    parseToken(LexTokenCode.FUNC)));

            children.addAll(parseOptionalSeries(unaryFunction,
                    SyntaxAnalyserImpl.this::parseFunctionParameters));

            children.addAll(parseSeries(unaryFunction,
                    SyntaxAnalyserImpl.this.parseFunBody::parse));

            unaryFunction.addChildren(children);
            literal.addChild(unaryFunction);
            return literal;
        }
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




    private class ParseFunBody implements SyntaxAnalyserParseGrammar {

        /** FunBody : is Body end | => Expression */
        public AstNode parse(AstNode parent) {
            AstNode funBody = parseAnyOf(parent,
                    this::parseFunctionBodyWithContent,
                    this::parseFunctionBodyExpression);

            if (funBody == null) {
                if(!iterator.hasNext()) {
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.FUN_BODY, span.getLineNum(), span.getPos());
            }

            return funBody;
        }

        /** FunBody : is Body end */
        private AstNode parseFunctionBodyWithContent(AstNode parent) {
            AstNode funBody = new AstGrammarNode(AstGrammarNodeType.FUN_BODY, parent);

            List<AstNode> children = new ArrayList<>();
            children.addAll(parseSeries(funBody,
                    parseToken(LexTokenCode.IS),
                    SyntaxAnalyserImpl.this::parseBody,
                    parseToken(LexTokenCode.END)));

            funBody.addChildren(children);
            return funBody;
        }

        /** FunBody : => Expression */
        private AstNode parseFunctionBodyExpression(AstNode parent) {
            AstNode funBody = new AstGrammarNode(AstGrammarNodeType.FUN_BODY, parent);

            List<AstNode> children = parseSeries(funBody,
                    parseToken(LexTokenCode.LAMBDA),
                    SyntaxAnalyserImpl.this::parseExpression);

            funBody.addChildren(children);
            return funBody;
        }
    }




    private class ParseTypeIndicator implements SyntaxAnalyserParseGrammar {

        /** TypeIndicator: int | real | bool | string | empty | [ ] | { } | func */
        public AstNode parse(AstNode parent) {
            AstNode typeIndicator = parseAnyOf(parent,
                    this::parseTypeIndicatorBasicKeywords,
                    this::parseTypeIndicatorArray,
                    this::parseTypeIndicatorTuple);

            if (typeIndicator == null) {
                if(!iterator.hasNext()) {
                    throw SyntaxExceptionFactory.noToken();
                }
                LexTokenSpan span = iterator.next().getSpan();
                throw SyntaxExceptionFactory.ambiguousGrammar(AstGrammarNodeType.TYPE_INDICATOR, span.getLineNum(), span.getPos());
            }

            return typeIndicator;
        }

        /** TypeIndicator: int | real | bool | string | empty | func */
        private AstNode parseTypeIndicatorBasicKeywords(AstNode parent) {
            AstNode typeIndicator = new AstGrammarNode(AstGrammarNodeType.TYPE_INDICATOR, parent);

            List<AstNode> children = new ArrayList<>();
            Set<LexTokenCode> codes = Set.of(
                    LexTokenCode.INT,
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

    }

}