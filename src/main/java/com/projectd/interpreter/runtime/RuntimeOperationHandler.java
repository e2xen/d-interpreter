package com.projectd.interpreter.runtime;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.lex.token.LexTokenSpan;
import com.projectd.interpreter.runtime.environment.ImmutableTuple;
import com.projectd.interpreter.runtime.environment.RuntimeValue;
import com.projectd.interpreter.runtime.environment.SparseArray;
import com.projectd.interpreter.shared.exception.RuntimeExceptionFactory;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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
            case OPEN_SQUARE_BRACKET -> {
                return handleArrayIndex(left, right);
            }
            case DOT -> {
                return handleTupleIndex(left, right);
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

    public static RuntimeValue handleTypeCheck(RuntimeValue value, RuntimeValue.RuntimeValueType type) {
        return RuntimeValue.ofValue(type.equals(value.getType()));
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
        assertOperandsSameType("concatenation", Set.of(RuntimeValue.RuntimeValueType.ARRAY,
                RuntimeValue.RuntimeValueType.TUPLE, RuntimeValue.RuntimeValueType.STRING), left, right);

        RuntimeValue result;
        if (left.getType() == RuntimeValue.RuntimeValueType.ARRAY) {
            SparseArray first = (SparseArray) left.getValue();
            SparseArray second = (SparseArray) right.getValue();
            result = RuntimeValue.ofValue(SparseArray.concatenate(first, second));
        } else if (left.getType() == RuntimeValue.RuntimeValueType.TUPLE) {
            ImmutableTuple first = (ImmutableTuple) left.getValue();
            ImmutableTuple second = (ImmutableTuple) right.getValue();
            result = RuntimeValue.ofValue(ImmutableTuple.concatenate(first, second));
        } else {
            String first = (String) left.getValue();
            String second = (String) right.getValue();
            result = RuntimeValue.ofValue(first + second);
        }
        return result;
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

    private static RuntimeValue handleArrayIndex(RuntimeValue array, RuntimeValue index) {
        assertOperandsType("array indexing", Set.of(RuntimeValue.RuntimeValueType.ARRAY), array);
        assertOperandsType("array indexing", Set.of(RuntimeValue.RuntimeValueType.INTEGER), index);
        return ((SparseArray) array.getValue()).get(index);
    }

    private static RuntimeValue handleTupleIndex(RuntimeValue tuple, RuntimeValue index) {
        assertOperandsType("tuple indexing", Set.of(RuntimeValue.RuntimeValueType.TUPLE), tuple);
        assertOperandsType("tuple indexing", Set.of(RuntimeValue.RuntimeValueType.INTEGER, RuntimeValue.RuntimeValueType.STRING), index);
        if (index.getType() == RuntimeValue.RuntimeValueType.INTEGER) {
            return ((ImmutableTuple) tuple.getValue()).getUnnamedElement(index);
        } else {
            return ((ImmutableTuple) tuple.getValue()).getNamedElement(index);
        }
    }

    public static Consumer<RuntimeValue> setterOf(LexToken operation, RuntimeValue object, RuntimeValue index) {
        opSpan = operation.getSpan();
        assertOperandsType("assignment", Set.of(RuntimeValue.RuntimeValueType.ARRAY), object);
        assertOperandsType("assignment", Set.of(RuntimeValue.RuntimeValueType.INTEGER), index);

        SparseArray arr = (SparseArray) object.getValue();
        return v -> arr.set(index, v);
    }

    public static boolean conditionCheck(LexToken operation, RuntimeValue value) {
        opSpan = operation.getSpan();
        assertOperandsType("condition check", Set.of(RuntimeValue.RuntimeValueType.BOOLEAN), value);

        return (Boolean) value.getValue();
    }

    public static Iterator<RuntimeValue> integerRange(LexToken operation, RuntimeValue from, RuntimeValue to) {
        opSpan = operation.getSpan();
        assertOperandsType("range", Set.of(RuntimeValue.RuntimeValueType.INTEGER), from, to);

        int left = (Integer) from.getValue();
        int right = (Integer) to.getValue();
        if (left > right) {
            throw RuntimeExceptionFactory.generic("Left range border cannot be greater than the right one", opSpan);
        }
        List<RuntimeValue> range = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            range.add(RuntimeValue.ofValue(i));
        }
        return range.iterator();
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

    private static void assertOperandsSameType(String op, Set<RuntimeValue.RuntimeValueType> types, RuntimeValue... values) {
        List<RuntimeValue.RuntimeValueType> providedTypes = Arrays.stream(values).map(RuntimeValue::getType).collect(Collectors.toList());
        RuntimeValue.RuntimeValueType sameType = providedTypes.get(0);
        providedTypes.forEach(t -> {
            if (!types.contains(t) || t != sameType)
                throw RuntimeExceptionFactory.invalidOperandTypes(op, providedTypes, opSpan);
        });
    }
}
