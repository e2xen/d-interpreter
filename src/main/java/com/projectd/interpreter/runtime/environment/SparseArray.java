package com.projectd.interpreter.runtime.environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SparseArray {

    private final Map<Integer, RuntimeValue> map;

    public SparseArray() {
        map = new HashMap<>();
    }

    public SparseArray(Map<Integer, RuntimeValue> map) {
        this.map = map;
    }

    public RuntimeValue get(RuntimeValue index) {
        if (index.getType() != RuntimeValue.RuntimeValueType.INTEGER) {
            throw new IllegalStateException();
        }

        Integer i = (Integer) index.getValue();
        assertIndexRange(i);
        return map.getOrDefault(i, RuntimeValue.empty());
    }

    public void set(RuntimeValue index, RuntimeValue value) {
        Objects.requireNonNull(value);
        if (index.getType() != RuntimeValue.RuntimeValueType.INTEGER) {
            throw new IllegalStateException();
        }

        Integer i = (Integer) index.getValue();
        assertIndexRange(i);
        map.put(i, value);
    }

    public static SparseArray concatenate(SparseArray first, SparseArray second) {
        int maxIndex = first.map.keySet().stream()
                .max(Integer::compareTo).orElse(0);
        Map<Integer, RuntimeValue> newMap = new HashMap<>(first.map);
        for (Map.Entry<Integer, RuntimeValue> entry : second.map.entrySet()) {
            newMap.put(maxIndex + entry.getKey(), entry.getValue());
        }
        return new SparseArray(newMap);
    }

    public static SparseArray fromList(List<RuntimeValue> list) {
        SparseArray arr = new SparseArray();
        for (int i = 0; i < list.size(); i++) {
            arr.map.put(i+1, list.get(i));
        }
        return arr;
    }

    private static void assertIndexRange(Integer i) {
        if (i < 1) {
            throw new IndexOutOfBoundsException("Array index must be greater than 0");
        }
    }
}
