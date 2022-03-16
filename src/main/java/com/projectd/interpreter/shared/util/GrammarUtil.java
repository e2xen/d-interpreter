package com.projectd.interpreter.shared.util;

import com.projectd.interpreter.lex.token.LexIdentifierToken;
import com.projectd.interpreter.lex.token.LexLiteralToken;
import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

public class GrammarUtil {

    public static AstNode verifyGramInstanceOrException(AstNode node, AstGrammarNodeType expectedType) {
        if((node instanceof AstGrammarNode grammarNode) && (grammarNode.getType().equals(expectedType))) {
            return node;
        } else {
            String message = String.format("Expected instance of %s; got %s", expectedType, node);
            throw new IllegalStateException(message);
        }
    }

    public static LexToken verifyLexInstanceOrException(AstNode node) {
        if(node instanceof AstTokenNode token) {
            return token.getToken();
        } else {
            String message = String.format("Expected instance of %s; got %s", LexLiteralToken.class, node);
            throw new IllegalStateException(message);
        }
    }

}
