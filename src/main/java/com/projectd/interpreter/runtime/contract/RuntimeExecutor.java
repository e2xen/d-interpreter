package com.projectd.interpreter.runtime.contract;

import com.projectd.interpreter.syntax.tree.AstNode;

public interface RuntimeExecutor {
    void execute(AstNode program);
}
