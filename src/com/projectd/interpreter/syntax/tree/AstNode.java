package com.projectd.interpreter.syntax.tree;

import com.projectd.interpreter.lex.token.LexToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AstNode {
    private final AstNode parent;
    private List<AstNode> children;

    public AstNode(AstNode parent) {
        this.parent = parent;
        this.children = new ArrayList<AstNode>();
    }

    public AstNode(AstNode parent, List<AstNode> children) {
        this.parent = parent;
        this.children = children;
    }

    public abstract String getContent();

    public List<AstNode> getChildren() {
        return this.children;
    }

    public void addChild(AstNode child) {
        this.children.add(child);
    }

    public void addChildren(List<AstNode> children) {
        this.children.addAll(children);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(getContent());
        buffer.append('\n');
        for (Iterator<AstNode> it = children.iterator(); it.hasNext();) {
            AstNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
