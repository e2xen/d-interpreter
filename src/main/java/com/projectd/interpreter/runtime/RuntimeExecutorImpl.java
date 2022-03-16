package com.projectd.interpreter.runtime;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.runtime.contract.RuntimeExecutor;
import com.projectd.interpreter.runtime.environment.*;
import com.projectd.interpreter.shared.exception.RuntimeExceptionFactory;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RuntimeExecutorImpl implements RuntimeExecutor {

    private final RuntimeEnvironment runtime = new RuntimeEnvironment();

    @Override
    public void execute(AstNode program) {
        assertGrammar(program, AstGrammarNodeType.PROGRAM);

        program.getChildren().forEach(this::executeStatement);
    }

    private Optional<RuntimeValue> executeStatement(AstNode statement) {
        assertGrammar(statement, AstGrammarNodeType.STATEMENT);

        if (statement.getChildren().get(0) instanceof AstGrammarNode grammarNode) {
            switch (grammarNode.getGrammarType()) {
                case ASSIGNMENT -> executeAssignment(grammarNode);
                case DECLARATION -> executeDeclaration(grammarNode);
                case PRINT -> executePrint(grammarNode);
                case EXPRESSION -> calcExpression(grammarNode);
                case RETURN -> {
                    return Optional.of(executeReturn(grammarNode));
                }
                case IF -> {
                    return executeIf(grammarNode);
                }
                case LOOP -> {
                    return executeLoop(grammarNode);
                }
            }
        } else fail();
        return Optional.empty();
    }

    private RuntimeValue executeReturn(AstNode ret) {
        assertGrammar(ret, AstGrammarNodeType.RETURN);

        if (ret.getChildren().size() > 1) {
            return calcExpression(ret.getChildren().get(1));
        } else {
            return RuntimeValue.empty();
        }
    }

    private Optional<RuntimeValue> executeIf(AstNode iff) {
        assertGrammar(iff, AstGrammarNodeType.IF);

        List<AstNode> children = iff.getChildren();
        boolean condition = RuntimeOperationHandler.conditionCheck(((AstTokenNode) children.get(0)).getToken(), calcExpression(children.get(1)));
        Optional<RuntimeValue> result;
        runtime.nestScope();
        if (condition) {
            result = executeBody(children.get(3));
        } else {
            if (children.size() > 5) {
                result = executeBody(children.get(5));
            } else {
                result = Optional.empty();
            }
        }
        runtime.flushScope();
        return result;
    }

    private Optional<RuntimeValue> executeBody(AstNode body) {
        assertGrammar(body, AstGrammarNodeType.BODY);

        for (AstNode statement : body.getChildren()) {
            Optional<RuntimeValue> statementRes = executeStatement(statement);
            if (statementRes.isPresent()) {
                return statementRes;
            }
        }
        return Optional.empty();
    }

    private Optional<RuntimeValue> executeLoop(AstNode loop) {
        assertGrammar(loop, AstGrammarNodeType.LOOP);

        if (loop.getChildren().get(0) instanceof AstTokenNode tokenNode) {
            if (tokenNode.getToken().getCode() == LexTokenCode.WHILE) {
                return executeWhile(loop.getChildren());
            } else if (tokenNode.getToken().getCode() == LexTokenCode.FOR) {
                return executeFor(loop.getChildren());
            }
        }
        throw new IllegalStateException();
    }

    private Optional<RuntimeValue> executeWhile(List<AstNode> children) {
        AstNode body = children.get(2).getChildren().get(1);
        boolean condition = RuntimeOperationHandler.conditionCheck(((AstTokenNode) children.get(0)).getToken(), calcExpression(children.get(1)));
        Optional<RuntimeValue> result = Optional.empty();
        runtime.nestScope();
        while (condition) {
            result = executeBody(body);
            if (result.isPresent()) {
                break;
            }
            condition = RuntimeOperationHandler.conditionCheck(((AstTokenNode) children.get(0)).getToken(), calcExpression(children.get(1)));
        }
        runtime.flushScope();
        return result;
    }

    private Optional<RuntimeValue> executeFor(List<AstNode> children) {
        AstNode body = children.get(7).getChildren().get(1);

        Optional<RuntimeValue> result = Optional.empty();
        runtime.nestScope();
        LexIdentifierToken identifier = (LexIdentifierToken) ((AstTokenNode) children.get(1)).getToken();
        runtime.declareVariable(identifier);
        Iterator<RuntimeValue> it = RuntimeOperationHandler.integerRange(((AstTokenNode) children.get(4)).getToken(),
                calcExpression(children.get(3)), calcExpression(children.get(6)));
        for (; it.hasNext(); ) {
            RuntimeValue i = it.next();
            runtime.assignVariable(identifier, i);
            result = executeBody(body);
            if (result.isPresent()) {
                break;
            }
        }
        runtime.flushScope();
        return result;
    }

    private void executeDeclaration(AstNode declaration) {
        assertGrammar(declaration, AstGrammarNodeType.DECLARATION);
        declaration.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .forEach(this::executeVariableDefinition);
    }

    private void executeVariableDefinition(AstNode definition) {
        assertGrammar(definition, AstGrammarNodeType.VARIABLE_DEFINITION);

        List<AstNode> children = definition.getChildren();
        if (children.get(0) instanceof AstTokenNode tokenNode) {
            if (tokenNode.getToken() instanceof LexIdentifierToken identifierToken) {
                if (children.size() > 1) {
                    RuntimeValue value = calcExpression(children.get(children.size()-1));
                    runtime.declareAndAssignVariable(identifierToken, value);
                } else {
                    runtime.declareVariable(identifierToken);
                }
                return;
            }
        }
        throw new IllegalStateException();
    }

    private void executeAssignment(AstNode assignment) {
        assertGrammar(assignment, AstGrammarNodeType.ASSIGNMENT);
        List<AstNode> children = assignment.getChildren();

        RuntimeValue value = calcExpression(children.get(2));
        primaryAssignmentFunction(children.get(0)).accept(value);
    }

    private Consumer<RuntimeValue> primaryAssignmentFunction(AstNode primary) {
        assertGrammar(primary, AstGrammarNodeType.PRIMARY);
        List<AstNode> children = primary.getChildren();

        if (children.get(0) instanceof AstTokenNode tokenNode) {
            if (Set.of(LexTokenCode.READ_INT,
                    LexTokenCode.READ_REAL,
                    LexTokenCode.READ_STRING)
                    .contains(tokenNode.getToken().getCode())) {
                throw RuntimeExceptionFactory.generic("Cannot assign value to a read operation", tokenNode.getToken().getSpan());
            } else if (tokenNode.getToken() instanceof LexIdentifierToken identifierToken) {
                Consumer<RuntimeValue> result = v -> runtime.assignVariable(identifierToken, v);
                RuntimeValue value = runtime.getVariableValue(identifierToken);
                for (int i = 1; i < children.size(); i++) {
                    if (i == children.size()-1) {
                        result = tailAssignmentFunction(children.get(i), value);
                    } else {
                        value = calcTail(children.get(i), value);
                    }
                }
                return result;
            }
        }
        throw new IllegalStateException();
    }

    private Consumer<RuntimeValue> tailAssignmentFunction(AstNode tail, RuntimeValue ofValue) {
        assertGrammar(tail, AstGrammarNodeType.TAIL);

        List<AstNode> children = tail.getChildren();
        if (children.get(0) instanceof AstTokenNode tailOpNode) {
            switch (tailOpNode.getToken().getCode()) {
                case DOT -> throw RuntimeExceptionFactory.immutableObject(RuntimeValue.RuntimeValueType.TUPLE, tailOpNode.getToken().getSpan());
                case OPEN_SQUARE_BRACKET -> {
                    return arrayTailAssignmentFunction(tailOpNode.getToken(), calcExpression(children.get(1)), ofValue);
                }
                case OPEN_ROUND_BRACKET -> throw RuntimeExceptionFactory.immutableObject(RuntimeValue.RuntimeValueType.FUNCTION, tailOpNode.getToken().getSpan());
            }
        }
        throw new IllegalStateException();
    }

    private Consumer<RuntimeValue> arrayTailAssignmentFunction(LexToken op, RuntimeValue index, RuntimeValue ofValue) {
        return RuntimeOperationHandler.setterOf(op, ofValue, index);
    }

    private void executePrint(AstNode print) {
        assertGrammar(print, AstGrammarNodeType.PRINT);

        List<RuntimeValue> toPrint = print.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .map(this::calcExpression).collect(Collectors.toList());
        RuntimeIOHandler.handlePrint(toPrint);
    }

    public RuntimeValue callFunction(LexToken op, RuntimeFunction func, List<RuntimeValue> args) {
        List<LexIdentifierToken> parameters = func.getParameters();
        List<AstNode> body = func.getBody();
        if (parameters.size() != args.size()) {
            throw RuntimeExceptionFactory.argumentsMismatch(parameters.size(), args.size(), op.getSpan());
        }
        if (body.size() < 1) {
            throw RuntimeExceptionFactory.emptyFunction(op.getSpan());
        }

        runtime.nestScope();
        RuntimeValue result = RuntimeValue.empty();
        for (int i = 0; i < args.size(); i++) {
            runtime.declareAndAssignVariable(parameters.get(i), args.get(i));
        }
        if (func.isLambda()) {
            result = calcExpression(body.get(0));
        } else for (AstNode statement : body) {
            Optional<RuntimeValue> statementRes = executeStatement(statement);
            if (statementRes.isPresent()) {
                result = statementRes.get();
                break;
            }
        }
        runtime.flushScope();
        return result;
    }



    private RuntimeValue calcExpression(AstNode expression) {
        assertGrammar(expression, AstGrammarNodeType.EXPRESSION);

        List<AstNode> children = expression.getChildren();
        RuntimeValue result = calcConjunction(children.get(0));

        if (children.size() > 1) {
            LexToken op = null;
            for (int i = 1; i < children.size(); i++) {
                if (children.get(i) instanceof AstTokenNode token) {
                    op = token.getToken();
                } else if (children.get(i) instanceof AstGrammarNode grammar) {
                    switch (op.getCode()) {
                        case OR, XOR -> result = RuntimeOperationHandler.handleBinaryOperation(op, result, calcConjunction(grammar));
                        default -> fail();
                    }
                } else fail();
            }
        }

        return result;
    }

    private RuntimeValue calcConjunction(AstNode conjunction) {
        assertGrammar(conjunction, AstGrammarNodeType.CONJUNCTION);

        List<AstNode> children = conjunction.getChildren();
        RuntimeValue result = calcRelation(children.get(0));

        if (children.size() > 1) {
            LexToken op = null;
            for (int i = 1; i < children.size(); i++) {
                if (children.get(i) instanceof AstTokenNode token) {
                    op = token.getToken();
                } else if (children.get(i) instanceof AstGrammarNode grammar) {
                    if (op.getCode() != LexTokenCode.AND) {
                        fail();
                    }
                    result = RuntimeOperationHandler.handleBinaryOperation(op, result, calcRelation(grammar));
                } else fail();
            }
        }

        return result;
    }

    private RuntimeValue calcRelation(AstNode relation) {
        assertGrammar(relation, AstGrammarNodeType.RELATION);

        List<AstNode> children = relation.getChildren();
        RuntimeValue result = calcFactor(children.get(0));

        if (children.size() > 1) {
            LexToken op = ((AstTokenNode) children.get(1)).getToken();
            if (!Set.of(LexTokenCode.LESS,
                    LexTokenCode.LESS_OR_EQUAL,
                    LexTokenCode.MORE,
                    LexTokenCode.MORE_OR_EQUAL,
                    LexTokenCode.EQUAL,
                    LexTokenCode.NOT_EQUAL)
                    .contains(op.getCode())) {
                fail();
            }
            RuntimeValue other = calcFactor(children.get(2));
            result = RuntimeOperationHandler.handleBinaryOperation(op, result, other);
        }

        return result;
    }

    private RuntimeValue calcFactor(AstNode factor) {
        assertGrammar(factor, AstGrammarNodeType.FACTOR);

        List<AstNode> children = factor.getChildren();
        RuntimeValue result = calcTerm(children.get(0));

        if (children.size() > 1) {
            LexToken op = null;
            for (int i = 1; i < children.size(); i++) {
                if (children.get(i) instanceof AstTokenNode token) {
                    op = token.getToken();
                } else if (children.get(i) instanceof AstGrammarNode grammar) {
                    switch (op.getCode()) {
                        case ADDITION, SUBTRACTION -> result = RuntimeOperationHandler.handleBinaryOperation(op, result, calcTerm(grammar));
                        default -> fail();
                    }
                } else fail();
            }
        }

        return result;
    }

    private RuntimeValue calcTerm(AstNode term) {
        assertGrammar(term, AstGrammarNodeType.TERM);

        List<AstNode> children = term.getChildren();
        RuntimeValue result = calcUnary(children.get(0));

        if (children.size() > 1) {
            LexToken op = null;
            for (int i = 1; i < children.size(); i++) {
                if (children.get(i) instanceof AstTokenNode token) {
                    op = token.getToken();
                } else if (children.get(i) instanceof AstGrammarNode grammar) {
                    switch (op.getCode()) {
                        case MULTIPLICATION, DIVISION -> result = RuntimeOperationHandler.handleBinaryOperation(op, result, calcUnary(grammar));
                        default -> fail();
                    }
                } else fail();
            }
        }

        return result;
    }

    private RuntimeValue calcUnary(AstNode unary) {
        assertGrammar(unary, AstGrammarNodeType.UNARY);

        List<AstNode> children = unary.getChildren();
        if (children.get(0) instanceof AstGrammarNode grammar) {
            if (grammar.getGrammarType() == AstGrammarNodeType.LITERAL) {
                return calcLiteral(children.get(0));
            }
        }
        if (children.get(0) instanceof AstTokenNode token) {
            if (token.getToken().getCode() == LexTokenCode.OPEN_ROUND_BRACKET) {
                return calcExpression(children.get(1));
            }
        }

        RuntimeValue result = null;
        if (children.get(0) instanceof AstTokenNode token) {
            if (Set.of(LexTokenCode.ADDITION,
                    LexTokenCode.SUBTRACTION,
                    LexTokenCode.NOT).contains(token.getToken().getCode())) {
                result = RuntimeOperationHandler.handleUnaryOperation(token.getToken(), calcPrimary(children.get(1)));
            } else fail();
        } else {
            result = calcPrimary(children.get(0));
        }

        if (children.size() > 2) {
            result = RuntimeOperationHandler.handleTypeCheck(result, calcTypeIndicator(children.get(children.size()-1)));
        }
        return result;
    }

    private RuntimeValue calcPrimary(AstNode primary) {
        assertGrammar(primary, AstGrammarNodeType.PRIMARY);

        List<AstNode> children = primary.getChildren();
        if (children.get(0) instanceof AstTokenNode token) {
            if (Set.of(LexTokenCode.READ_INT,
                    LexTokenCode.READ_REAL,
                    LexTokenCode.READ_STRING).contains(token.getToken().getCode())) {
                return RuntimeIOHandler.handleRead(token.getToken());
            } else if (token.getToken() instanceof LexIdentifierToken identifier) {
                RuntimeValue result = runtime.getVariableValue(identifier);
                for (int i = 1; i < children.size(); i++) {
                    result = calcTail(children.get(i), result);
                }
                return result;
            }
        }
        throw new IllegalStateException();
    }

    private RuntimeValue calcTail(AstNode tail, RuntimeValue value) {
        assertGrammar(tail, AstGrammarNodeType.TAIL);

        List<AstNode> children = tail.getChildren();
        if (children.get(0) instanceof AstTokenNode tailOpNode) {
            switch (tailOpNode.getToken().getCode()) {
                case DOT -> {
                    LexToken tupleTail = ((AstTokenNode) children.get(1)).getToken();
                    return calcTupleTail(tailOpNode.getToken(), tupleTail, value);
                }
                case OPEN_SQUARE_BRACKET -> {
                    return calcArrayTail(tailOpNode.getToken(), calcExpression(children.get(1)), value);
                }
                case OPEN_ROUND_BRACKET -> {
                    List<RuntimeValue> args = children.stream()
                            .filter(e -> e instanceof AstGrammarNode)
                            .map(this::calcExpression).collect(Collectors.toList());
                    return calcFunctionTail(tailOpNode.getToken(), args, value);
                }
            }
        }
        throw new IllegalStateException();
    }

    private RuntimeValue calcTupleTail(LexToken tailOp, LexToken tail, RuntimeValue value) {
        if (tail instanceof LexIdentifierToken identifier) {
            RuntimeValue namedIndex = RuntimeValue.ofValue(identifier.getIdentifier());
            return RuntimeOperationHandler.handleBinaryOperation(tailOp, value, namedIndex);
        } else if (tail instanceof LexLiteralToken literalToken) {
            if (literalToken.getType() == LexLiteralTokenType.INT) {
                RuntimeValue index = RuntimeValue.ofLiteral(literalToken);
                return RuntimeOperationHandler.handleBinaryOperation(tailOp, value, index);
            }
        }
        throw new IllegalStateException();
    }

    private RuntimeValue calcArrayTail(LexToken tailOp, RuntimeValue index, RuntimeValue value) {
        return RuntimeOperationHandler.handleBinaryOperation(tailOp, value, index);
    }

    private RuntimeValue calcFunctionTail(LexToken tailOp, List<RuntimeValue> args, RuntimeValue func) {
        if (func.getType() != RuntimeValue.RuntimeValueType.FUNCTION) {
            throw RuntimeExceptionFactory.invalidOperandTypes("function call", List.of(func.getType()), tailOp.getSpan());
        }
        return callFunction(tailOp, (RuntimeFunction) func.getValue(), args);
    }

    private RuntimeValue calcLiteral(AstNode literal) {
        assertGrammar(literal, AstGrammarNodeType.LITERAL);

        if (literal.getChildren().get(0) instanceof AstTokenNode token) {
            if (token.getToken() instanceof LexLiteralToken literalToken) {
                return RuntimeValue.ofLiteral(literalToken);
            } else fail();
        } else if (literal.getChildren().get(0) instanceof AstGrammarNode grammarNode) {
            switch (grammarNode.getGrammarType()) {
                case ARRAY_LITERAL -> {
                    return calcArrayLiteral(grammarNode);
                }
                case TUPLE_LITERAL -> {
                    return calcTupleLiteral(grammarNode);
                }
                case FUNCTION_LITERAL -> {
                    return calcFunctionLiteral(grammarNode);
                }
            }
        }
        throw new IllegalStateException();
    }

    private RuntimeValue calcArrayLiteral(AstNode arrayLiteral) {
        assertGrammar(arrayLiteral, AstGrammarNodeType.ARRAY_LITERAL);
        List<RuntimeValue> elements = arrayLiteral.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .map(this::calcExpression)
                .collect(Collectors.toList());
        return RuntimeValue.ofValue(SparseArray.fromList(elements));
    }

    private RuntimeValue calcTupleLiteral(AstNode tupleLiteral) {
        assertGrammar(tupleLiteral, AstGrammarNodeType.TUPLE_LITERAL);
        ImmutableTuple.Builder builder = ImmutableTuple.builder();
        tupleLiteral.getChildren().stream()
                .filter(e -> e instanceof AstGrammarNode)
                .forEach(e -> addTupleElement(e, builder));
        return RuntimeValue.ofValue(builder.build());
    }

    private void addTupleElement(AstNode tupleElement, ImmutableTuple.Builder builder) {
        assertGrammar(tupleElement, AstGrammarNodeType.TUPLE_ELEMENT);
        if (tupleElement.getChildren().get(0) instanceof AstTokenNode token) {
            if (token.getToken() instanceof LexIdentifierToken identifier) {
                builder.addNamedElement(identifier.getIdentifier(), calcExpression(tupleElement.getChildren().get(2)));
            } else fail();
        } else if (tupleElement.getChildren().get(0) instanceof AstGrammarNode grammar) {
            builder.addUnnamedElement(calcExpression(grammar));
        } else fail();
    }

    private RuntimeValue calcFunctionLiteral(AstNode functionLiteral) {
        assertGrammar(functionLiteral, AstGrammarNodeType.FUNCTION_LITERAL);
        List<AstNode> children = functionLiteral.getChildren();

        if (children.get(1) instanceof AstGrammarNode grammarNode) {
            List<LexIdentifierToken> params = new ArrayList<>();
            if (grammarNode.getGrammarType() == AstGrammarNodeType.PARAMETERS) {
                params = calcParams(grammarNode);
            }
            RuntimeFunction func = calcFunBody(children.get(children.size()-1), params);
            return RuntimeValue.ofValue(func);
        }
        throw new IllegalStateException();
    }

    private List<LexIdentifierToken> calcParams(AstNode params) {
        assertGrammar(params, AstGrammarNodeType.PARAMETERS);
        return params.getChildren().stream()
                .map(e -> (AstTokenNode) e)
                .map(AstTokenNode::getToken)
                .filter(e -> e instanceof LexIdentifierToken)
                .map(e -> (LexIdentifierToken) e).collect(Collectors.toList());
    }

    private RuntimeFunction calcFunBody(AstNode funBody, List<LexIdentifierToken> params) {
        assertGrammar(funBody, AstGrammarNodeType.FUN_BODY);
        if (funBody.getChildren().get(1) instanceof AstGrammarNode grammarNode) {
            if (grammarNode.getGrammarType() == AstGrammarNodeType.EXPRESSION) {
                return new RuntimeFunction(params, List.of(grammarNode), true);
            } else if (grammarNode.getGrammarType() == AstGrammarNodeType.BODY) {
                return new RuntimeFunction(params, grammarNode.getChildren(), false);
            }
        }
        throw new IllegalStateException();
    }

    private RuntimeValue.RuntimeValueType calcTypeIndicator(AstNode typeInd) {
        assertGrammar(typeInd, AstGrammarNodeType.TYPE_INDICATOR);

        RuntimeValue.RuntimeValueType result = null;
        if (typeInd.getChildren().get(0) instanceof AstTokenNode token) {
            switch (token.getToken().getCode()) {
                case INT -> result = RuntimeValue.RuntimeValueType.INTEGER;
                case REAL -> result = RuntimeValue.RuntimeValueType.REAL;
                case BOOLEAN -> result = RuntimeValue.RuntimeValueType.BOOLEAN;
                case STRING -> result = RuntimeValue.RuntimeValueType.STRING;
                case EMPTY -> result = RuntimeValue.RuntimeValueType.EMPTY;
                case FUNC -> result = RuntimeValue.RuntimeValueType.FUNCTION;
                case OPEN_CURLY_BRACKET -> result = RuntimeValue.RuntimeValueType.ARRAY;
                case OPEN_ROUND_BRACKET -> result = RuntimeValue.RuntimeValueType.TUPLE;
                default -> fail();
            }
        } else fail();

        return result;
    }

    private static void assertGrammar(AstNode node, AstGrammarNodeType expectedType) {
        if (node instanceof AstGrammarNode grammarNode) {
            if (expectedType.equals(grammarNode.getGrammarType()))
                return;
        }
        fail();
    }

    private static void fail() {
        throw new IllegalStateException();
    }
}
