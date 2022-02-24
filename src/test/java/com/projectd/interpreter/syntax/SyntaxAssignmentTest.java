package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.LexIdentifierToken;
import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.lex.token.LexTokenSpan;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyntaxAssignmentTest {

    @Test
    void basicAssignment() {
        // Given
        List<LexToken> tokens = List.of(
                new LexIdentifierToken("TEST_IDENT", getSampleSpan()),
                new LexToken(getSampleSpan(), LexTokenCode.ASSIGNMENT),
                new LexToken(getSampleSpan(), LexTokenCode.LITERAL)
        );

        // When
        SyntaxAnalyser analyser = new SyntaxAnalyserImpl(tokens);

        // Then
        System.out.println(analyser.buildAstTree().toString());
    }

    private LexTokenSpan getSampleSpan() {
        return LexTokenSpan.of(0, 0);
    }
}
