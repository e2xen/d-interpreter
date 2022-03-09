package com.projectd.interpreter.runtime;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.lex.token.LexTokenSpan;
import com.projectd.interpreter.runtime.environment.RuntimeValue;
import com.projectd.interpreter.shared.exception.RuntimeExceptionFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RuntimeOperationHandler {

    private static LexTokenSpan opSpan;

    public static RuntimeValue handleBinaryOperation(LexToken operation, RuntimeValue left, RuntimeValue right) {
        opSpan = operation.getSpan();

        switch (operation.getCode()) {
            case OR -> {
                return handleOr(left, right);
            }
            case XOR -> {
                return handleXor(left, right);
            }
            case AND -> {
                return handleAnd(left, right);
            }
            case LESS, LESS_OR_EQUAL, MORE, MORE_OR_EQUAL, EQUAL, NOT_EQUAL -> {
                return handleComparison(operation.getCode(), left, right);
            }
            case ADDITION -> {
                return handleAddition(left, right);
            }
            case SUBTRACTION -> {
                return handleSubtraction(left, right);
            }
            case MULTIPLICATION -> {
                return handleMultiplication(left, right);
            }
            case DIVISION -> {
                return handleDivision(left, right);
            }
            default -> throw new IllegalArgumentException(String.format("Operation %s is not supported", operation.getCode().toString()));
        }
    }

    public static RuntimeValue handleUnaryOperation(LexToken operation, RuntimeValue value) {
        opSpan = operation.getSpan();

        switch (operation.getCode()) {
            case ADDITION -> {
                return handleIdentity(value);
            }
            case SUBTRACTION -> {
                return handleNegation(value);
            }
            case NOT -> {
                return handleLogicNegation(value);
            }
            default -> throw new IllegalArgumentException(String.format("Operation %s is not supported", operation.getCode().toString()));
        }
    }

    private static RuntimeValue handleOr(RuntimeValue left, RuntimeValue right) {
        assertOperandsType("or", Set.of(RuntimeValue.RuntimeValueType.BOOLEAN), left, right);

        boolean first = (Boolean) left.getValue();
        boolean second = (Boolean) left.getValue();
        return RuntimeValue.ofValue(first || second);
    }

    private static RuntimeValue handleAnd(RuntimeValue left, RuntimeValue right) {
        assertOperandsType("and", Set.of(RuntimeValue.RuntimeValueType.BOOLEAN), left, right);

        boolean first = (Boolean) left.getValue();
        boolean second = (Boolean) left.getValue();
        return RuntimeValue.ofValue(first && second);
    }

    private static RuntimeValue handleXor(RuntimeValue left, RuntimeValue right) {
        assertOperandsType("xor", Set.of(RuntimeValue.RuntimeValueType.BOOLEAN), left, right);

        boolean first = (Boolean) left.getValue();
        boolean second = (Boolean) left.getValue();
        return RuntimeValue.ofValue(first && !second || !first && second);
    }

    private static RuntimeValue handleComparison(LexTokenCode compare, RuntimeValue left, RuntimeValue right) {
        assertOperandsType("comparison", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.REAL), left, right);

        double first = doubleValue(left);
        double second = doubleValue(right);

        boolean result;
        switch (compare) {
            case LESS -> result = first < second;
            case LESS_OR_EQUAL -> result = first <= second;
            case MORE -> result = first > second;
            case MORE_OR_EQUAL -> result = first >= second;
            case EQUAL -> result = first == second;
            case NOT_EQUAL -> result = first != second;
            default -> throw new IllegalArgumentException();
        }
        return RuntimeValue.ofValue(result);
    }

    private static RuntimeValue handleAddition(RuntimeValue left, RuntimeValue right) {
        if (Set.of(RuntimeValue.RuntimeValueType.STRING,
                RuntimeValue.RuntimeValueType.ARRAY,
                RuntimeValue.RuntimeValueType.TUPLE)
                .contains(left.getType())) {
            return handleConcatenation(left, right);
        }

        assertOperandsType("addition", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.REAL), left, right);

        if (left.getType() == RuntimeValue.RuntimeValueType.INTEGER
                && right.getType() == RuntimeValue.RuntimeValueType.INTEGER) {
            return RuntimeValue.ofValue((Integer) left.getValue() + (Integer) right.getValue());
        }

        double first = doubleValue(left);
        double second = doubleValue(right);

        return RuntimeValue.ofValue(first + second);
    }

    private static RuntimeValue handleConcatenation(RuntimeValue left, RuntimeValue right) {
        throw new UnsupportedOperationException();
    }

    private static RuntimeValue handleSubtraction(RuntimeValue left, RuntimeValue right) {
        assertOperandsType("subtraction", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.REAL), left, right);

        if (left.getType() == RuntimeValue.RuntimeValueType.INTEGER
                && right.getType() == RuntimeValue.RuntimeValueType.INTEGER) {
            return RuntimeValue.ofValue((Integer) left.getValue() - (Integer) right.getValue());
        }

        double first = doubleValue(left);
        double second = doubleValue(right);

        return RuntimeValue.ofValue(first - second);
    }

    private static RuntimeValue handleMultiplication(RuntimeValue left, RuntimeValue right) {
        assertOperandsType("multiplication", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.REAL), left, right);

        if (left.getType() == RuntimeValue.RuntimeValueType.INTEGER
                && right.getType() == RuntimeValue.RuntimeValueType.INTEGER) {
            return RuntimeValue.ofValue((Integer) left.getValue() * (Integer) right.getValue());
        }

        double first = doubleValue(left);
        double second = doubleValue(right);

        return RuntimeValue.ofValue(first * second);
    }

    private static RuntimeValue handleDivision(RuntimeValue left, RuntimeValue right) {
        assertOperandsType("division", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.REAL), left, right);

        if (left.getType() == RuntimeValue.RuntimeValueType.INTEGER
                && right.getType() == RuntimeValue.RuntimeValueType.INTEGER) {
            return RuntimeValue.ofValue((Integer) left.getValue() / (Integer) right.getValue());
        }

        double first = doubleValue(left);
        double second = doubleValue(right);

        return RuntimeValue.ofValue(first / second);
    }

    private static RuntimeValue handleIdentity(RuntimeValue value) {
        assertOperandsType("identity", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.REAL), value);
        return value;
    }

    private static RuntimeValue handleNegation(RuntimeValue value) {
        assertOperandsType("negation", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.REAL), value);

        if (value.getType() == RuntimeValue.RuntimeValueType.INTEGER) {
            return RuntimeValue.ofValue(-1 * (Integer) value.getValue());
        } else {
            return RuntimeValue.ofValue(-1 * (Double) value.getValue());
        }
    }

    private static RuntimeValue handleLogicNegation(RuntimeValue value) {
        assertOperandsType("logical negation", Set.of(RuntimeValue.RuntimeValueType.BOOLEAN), value);
        return RuntimeValue.ofValue( !(Boolean) value.getValue() );
    }


    private static double doubleValue(RuntimeValue value) {
        double result;
        if (value.getType() == RuntimeValue.RuntimeValueType.INTEGER) {
            result = Double.valueOf((Integer) value.getValue());
        } else {
            result = (Double) value.getValue();
        }
        return result;
    }

    private static void assertOperandsType(String op, Set<RuntimeValue.RuntimeValueType> types, RuntimeValue... values) {
        List<RuntimeValue.RuntimeValueType> providedTypes = Arrays.stream(values).map(RuntimeValue::getType).collect(Collectors.toList());
        providedTypes.forEach(t -> {
            if (!types.contains(t))
                throw RuntimeExceptionFactory.invalidOperandTypes(op, providedTypes, opSpan);
        });
    }
}
