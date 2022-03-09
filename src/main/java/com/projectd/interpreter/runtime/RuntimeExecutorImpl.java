package com.projectd.interpreter.runtime;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.runtime.contract.RuntimeExecutor;
import com.projectd.interpreter.runtime.environment.RuntimeEnvironment;
import com.projectd.interpreter.runtime.environment.RuntimeValue;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.util.List;
import java.util.Set;

public class RuntimeExecutorImpl implements RuntimeExecutor {

    private RuntimeEnvironment runtime = new RuntimeEnvironment();

    @Override
    public void execute(AstNode program) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
