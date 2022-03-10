package com.projectd.interpreter.syntax.tree;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode
public abstract class AstNode {
    private final AstNode parent;
    private final List<AstNode> children;

    public AstNode(AstNode parent) {
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public AstNode(AstNode parent, List<AstNode> children) {
        this.parent = parent;
        this.children = children;
    }

    public List<AstNode> getChildren() {
        return this.children;
    }

    public void addChild(AstNode child) {
        this.children.add(child);
    }

    public void addChildren(List<AstNode> children) {
        this.children.addAll(children);
    }


    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        printTree(buffer, "", "");
        return buffer.toString();
    }

    protected abstract String getContent();

    private void printTree(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(getContent());
        buffer.append('\n');
        for (Iterator<AstNode> it = children.iterator(); it.hasNext();) {
            AstNode next = it.next();
            if (it.hasNext()) {
                next.printTree(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.printTree(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
