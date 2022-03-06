package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class SyntaxAssignmentTest {

    @Test
    void basicAssignment() {
        LexTokenSpan sampleSpan = getSampleSpan();

        List<LexToken> tokens = List.of(
                new LexIdentifierToken("TEST_IDENT", LexTokenSpan.of(0, 0)),
                new LexToken(sampleSpan, LexTokenCode.ASSIGNMENT),
                LexLiteralToken.ofValue(7, sampleSpan),
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(8, sampleSpan)
        );

        // When
        SyntaxAnalyser analyser = new SyntaxAnalyserImpl(tokens);

        // Then
        AstNode root = analyser.buildAstTree();

        assert(root.getChildren().size() == 1);
    }

    private LexTokenSpan getSampleSpan() {
        return LexTokenSpan.of(0, 0);
    }
}
