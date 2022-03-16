package com.projectd.interpreter.runtime.environment;

import java.util.*;

public class ImmutableTuple {

    private final List<RuntimeValue> values;
    private final Map<String, Integer> nameIndex;

    private ImmutableTuple(List<RuntimeValue> values, Map<String, Integer> nameIndex) {
        this.values = values;
        this.nameIndex = nameIndex;
    }

    protected ImmutableTuple() {
        values = new ArrayList<>();
        nameIndex = new HashMap<>();
    }

    public RuntimeValue getUnnamedElement(RuntimeValue index) {
        if (index.getType() != RuntimeValue.RuntimeValueType.INTEGER) {
            throw new IllegalStateException();
        }

        Integer i = (Integer) index.getValue();
        assertIndexRange(i);
        return values.get(i-1);
    }

    public RuntimeValue getNamedElement(RuntimeValue name) {
        if (name.getType() != RuntimeValue.RuntimeValueType.STRING) {
            throw new IllegalStateException();
        }

        String n = (String) name.getValue();
        assertKnownName(n);
        Integer i = nameIndex.get(n);
        assertIndexRange(i);
        return values.get(i-1);
    }

    public static ImmutableTuple concatenate(ImmutableTuple first, ImmutableTuple second) {
        List<RuntimeValue> newValues = new ArrayList<>(first.values);
        newValues.addAll(second.values);
        Map<String, Integer> newNameIndex = new HashMap<>(first.nameIndex);
        for (Map.Entry<String, Integer> entry : second.nameIndex.entrySet()) {
            newNameIndex.put(entry.getKey(), first.values.size() + entry.getValue());
        }
        return new ImmutableTuple(newValues, newNameIndex);
    }

    protected void addUnnamedElement(RuntimeValue value) {
        Objects.requireNonNull(value);

        values.add(value);
    }

    protected void addNamedElement(String name, RuntimeValue value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        values.add(value);
        nameIndex.put(name, values.size());
    }

    private void assertKnownName(String name) {
        if (!nameIndex.containsKey(name)) {
            throw new IndexOutOfBoundsException(String.format("Named element %s does not exist", name));
        }
    }

    private void assertIndexRange(Integer i) {
        if ((i < 1) || (i > values.size())) {
            throw new IndexOutOfBoundsException(String.format("Tuple index is out of bounds: [1, %d]", values.size()));
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append('{');
        values.forEach(s -> {
            out.append(s.toString());
            out.append(", ");
        });
        out.append('}');
        return out.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ImmutableTuple tuple = new ImmutableTuple();

        public void addUnnamedElement(RuntimeValue v) {
            tuple.addUnnamedElement(v);
        }

        public void addNamedElement(String name, RuntimeValue v) {
            tuple.addNamedElement(name, v);
        }

        public ImmutableTuple build() {
            return tuple;
        }
    }
}
