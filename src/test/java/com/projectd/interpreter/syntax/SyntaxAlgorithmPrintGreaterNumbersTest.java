package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.syntax.tree.AstNode;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SyntaxAlgorithmPrintGreaterNumbersTest {

    @Test
    public void testAlgorithm() {
        // Given
        List<LexToken> expectedTokens = List.of(
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.VAR),
                new LexIdentifierToken("arr", LexTokenSpan.of(0, 4)),
                new LexToken(LexTokenSpan.of(0, 8), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(0, 11), LexTokenCode.OPEN_SQUARE_BRACKET),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(0, 12)),
                new LexToken(LexTokenSpan.of(0, 13), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(2, LexTokenSpan.of(0, 15)),
                new LexToken(LexTokenSpan.of(0, 16), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(3, LexTokenSpan.of(0, 18)),
                new LexToken(LexTokenSpan.of(0, 19), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(2, 1), LexTokenCode.FOR),
                new LexIdentifierToken("i", LexTokenSpan.of(2, 5)),
                new LexToken(LexTokenSpan.of(2, 7), LexTokenCode.IN),
                LexLiteralToken.ofValue(0, LexTokenSpan.of(2, 10)),
                new LexToken(LexTokenSpan.of(2, 12), LexTokenCode.DOT),
                new LexToken(LexTokenSpan.of(2, 13), LexTokenCode.DOT),
                LexLiteralToken.ofValue(3, LexTokenSpan.of(2, 15)),
                new LexToken(LexTokenSpan.of(2, 17), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(3, 2), LexTokenCode.IF),
                new LexIdentifierToken("arr", LexTokenSpan.of(3, 5)),
                new LexToken(LexTokenSpan.of(3, 8), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("i", LexTokenSpan.of(3, 9)),
                new LexToken(LexTokenSpan.of(3, 10), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(3, 12), LexTokenCode.MORE_OR_EQUAL),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(3, 15)),
                new LexToken(LexTokenSpan.of(3, 17), LexTokenCode.THEN),
                new LexToken(LexTokenSpan.of(4, 3), LexTokenCode.PRINT),
                new LexIdentifierToken("arr", LexTokenSpan.of(4, 9)),
                new LexToken(LexTokenSpan.of(4, 12), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("i", LexTokenSpan.of(4, 13)),
                new LexToken(LexTokenSpan.of(4, 14), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(5, 2), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(6, 1), LexTokenCode.END)
        );

        // When
        SyntaxAnalyser syntaxAnalyser = new SyntaxAnalyserImpl(expectedTokens);

        AstNode root = syntaxAnalyser.buildAstTree();
        System.out.println(root.toString());

        // Then
        // Resulting tree must contain 2 statements
        assert(root.getChildren().size() == 2);

        String expectedStructureOfTree = """
                PROGRAM
                ├── STATEMENT
                │   └── DECLARATION
                │       ├── var
                │       └── VARIABLE_DEFINITION
                │           ├── IDENTIFIER
                │           ├── :=
                │           └── EXPRESSION
                │               └── CONJUNCTION
                │                   └── RELATION
                │                       └── FACTOR
                │                           └── TERM
                │                               └── UNARY
                │                                   └── LITERAL
                │                                       └── ARRAY_LITERAL
                │                                           ├── [
                │                                           ├── EXPRESSION
                │                                           │   └── CONJUNCTION
                │                                           │       └── RELATION
                │                                           │           └── FACTOR
                │                                           │               └── TERM
                │                                           │                   └── UNARY
                │                                           │                       └── LITERAL
                │                                           │                           └── LITERAL
                │                                           ├── ,
                │                                           ├── EXPRESSION
                │                                           │   └── CONJUNCTION
                │                                           │       └── RELATION
                │                                           │           └── FACTOR
                │                                           │               └── TERM
                │                                           │                   └── UNARY
                │                                           │                       └── LITERAL
                │                                           │                           └── LITERAL
                │                                           ├── ,
                │                                           ├── EXPRESSION
                │                                           │   └── CONJUNCTION
                │                                           │       └── RELATION
                │                                           │           └── FACTOR
                │                                           │               └── TERM
                │                                           │                   └── UNARY
                │                                           │                       └── LITERAL
                │                                           │                           └── LITERAL
                │                                           └── ]
                └── STATEMENT
                    └── LOOP
                        ├── for
                        ├── IDENTIFIER
                        ├── in
                        ├── EXPRESSION
                        │   └── CONJUNCTION
                        │       └── RELATION
                        │           └── FACTOR
                        │               └── TERM
                        │                   └── UNARY
                        │                       └── LITERAL
                        │                           └── LITERAL
                        ├── .
                        ├── .
                        ├── EXPRESSION
                        │   └── CONJUNCTION
                        │       └── RELATION
                        │           └── FACTOR
                        │               └── TERM
                        │                   └── UNARY
                        │                       └── LITERAL
                        │                           └── LITERAL
                        └── LOOP_BODY
                            ├── loop
                            ├── BODY
                            │   └── STATEMENT
                            │       └── IF
                            │           ├── if
                            │           ├── EXPRESSION
                            │           │   └── CONJUNCTION
                            │           │       └── RELATION
                            │           │           ├── FACTOR
                            │           │           │   └── TERM
                            │           │           │       └── UNARY
                            │           │           │           └── PRIMARY
                            │           │           │               ├── IDENTIFIER
                            │           │           │               └── TAIL
                            │           │           │                   ├── [
                            │           │           │                   ├── EXPRESSION
                            │           │           │                   │   └── CONJUNCTION
                            │           │           │                   │       └── RELATION
                            │           │           │                   │           └── FACTOR
                            │           │           │                   │               └── TERM
                            │           │           │                   │                   └── UNARY
                            │           │           │                   │                       └── PRIMARY
                            │           │           │                   │                           └── IDENTIFIER
                            │           │           │                   └── ]
                            │           │           ├── >=
                            │           │           └── FACTOR
                            │           │               └── TERM
                            │           │                   └── UNARY
                            │           │                       └── LITERAL
                            │           │                           └── LITERAL
                            │           ├── then
                            │           ├── BODY
                            │           │   └── STATEMENT
                            │           │       └── PRINT
                            │           │           ├── print
                            │           │           └── EXPRESSION
                            │           │               └── CONJUNCTION
                            │           │                   └── RELATION
                            │           │                       └── FACTOR
                            │           │                           └── TERM
                            │           │                               └── UNARY
                            │           │                                   └── PRIMARY
                            │           │                                       ├── IDENTIFIER
                            │           │                                       └── TAIL
                            │           │                                           ├── [
                            │           │                                           ├── EXPRESSION
                            │           │                                           │   └── CONJUNCTION
                            │           │                                           │       └── RELATION
                            │           │                                           │           └── FACTOR
                            │           │                                           │               └── TERM
                            │           │                                           │                   └── UNARY
                            │           │                                           │                       └── PRIMARY
                            │           │                                           │                           └── IDENTIFIER
                            │           │                                           └── ]
                            │           └── end
                            └── end
                """;

        assert(expectedStructureOfTree.equals(root.toString()));
    }
}
