package com.projectd.interpreter.syntax.iterator;

import java.util.Iterator;

public interface RollbackableIterator<E> extends Iterator<E> {
    void checkpoint();

    void rollback();
}
