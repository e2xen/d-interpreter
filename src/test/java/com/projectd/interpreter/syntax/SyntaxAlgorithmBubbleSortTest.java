package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.syntax.tree.AstNode;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SyntaxAlgorithmBubbleSortTest {

    @Test
    public void testAlgorithm() {
        // Given
        LexTokenSpan sampleSpan = getSampleSpan();
        List<LexToken> expectedTokens = List.of(
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.VAR),
                new LexIdentifierToken("bubble", LexTokenSpan.of(0, 4)),
                new LexToken(LexTokenSpan.of(0,11), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(0, 14), LexTokenCode.FUNC),
                new LexToken(LexTokenSpan.of(0, 19), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("arr", LexTokenSpan.of(0, 20)),
                new LexToken(LexTokenSpan.of(0, 23), LexTokenCode.COMMA),
                new LexIdentifierToken("n", LexTokenSpan.of(0, 25)),
                new LexToken(LexTokenSpan.of(0, 26), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(0, 28), LexTokenCode.IS),
                new LexToken(LexTokenSpan.of(1, 2), LexTokenCode.FOR),
                new LexIdentifierToken("i", LexTokenSpan.of(1, 6)),
                new LexToken(LexTokenSpan.of(1, 8), LexTokenCode.IN),
                LexLiteralToken.ofValue(0, LexTokenSpan.of(1, 11)),
                new LexToken(LexTokenSpan.of(1, 13), LexTokenCode.DOT),
                new LexToken(LexTokenSpan.of(1, 14), LexTokenCode.DOT),
                new LexIdentifierToken("n", LexTokenSpan.of(1, 16)),
                new LexToken(LexTokenSpan.of(1, 17), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(1, 20), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(2, 4), LexTokenCode.VAR),
                new LexIdentifierToken("temp", LexTokenSpan.of(2, 8)),
                new LexToken(LexTokenSpan.of(2,13), LexTokenCode.ASSIGNMENT),
                LexLiteralToken.ofValue(0, sampleSpan),
                new LexToken(LexTokenSpan.of(3, 4), LexTokenCode.FOR),
                new LexIdentifierToken("j", LexTokenSpan.of(3, 8)),
                new LexToken(LexTokenSpan.of(3, 10), LexTokenCode.IN),
                LexLiteralToken.ofValue(0, LexTokenSpan.of(3, 13)),
                new LexToken(LexTokenSpan.of(3, 15), LexTokenCode.DOT),
                new LexToken(LexTokenSpan.of(3, 16), LexTokenCode.DOT),
                new LexIdentifierToken("n", LexTokenSpan.of(3, 18)),
                new LexToken(LexTokenSpan.of(3, 19), LexTokenCode.SUBTRACTION),
                new LexIdentifierToken("i", LexTokenSpan.of(3, 20)),
                new LexToken(LexTokenSpan.of(3, 21), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(3, 24), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(4, 6), LexTokenCode.IF),
                new LexIdentifierToken("arr", LexTokenSpan.of(4, 9)),
                new LexToken(LexTokenSpan.of(4, 12), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("j", LexTokenSpan.of(4, 13)),
                new LexToken(LexTokenSpan.of(4, 14), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(4, 16), LexTokenCode.MORE),
                new LexIdentifierToken("arr", LexTokenSpan.of(4, 18)),
                new LexToken(LexTokenSpan.of(4, 21), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("j", LexTokenSpan.of(4, 22)),
                new LexToken(LexTokenSpan.of(4, 23), LexTokenCode.ADDITION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(4, 25), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(4, 27), LexTokenCode.THEN),
                new LexIdentifierToken("temp", LexTokenSpan.of(5, 8)),
                new LexToken(LexTokenSpan.of(5,13), LexTokenCode.ASSIGNMENT),
                new LexIdentifierToken("arr", LexTokenSpan.of(5, 16)),
                new LexToken(LexTokenSpan.of(5, 19), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("j", LexTokenSpan.of(5, 20)),
                new LexToken(LexTokenSpan.of(5, 21), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexIdentifierToken("arr", LexTokenSpan.of(6, 8)),
                new LexToken(LexTokenSpan.of(6, 11), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("j", LexTokenSpan.of(6, 12)),
                new LexToken(LexTokenSpan.of(6, 13), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(6,15), LexTokenCode.ASSIGNMENT),
                new LexIdentifierToken("arr", LexTokenSpan.of(6, 18)),
                new LexToken(LexTokenSpan.of(6, 21), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("j", LexTokenSpan.of(6, 22)),
                new LexToken(LexTokenSpan.of(6, 23), LexTokenCode.ADDITION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(6, 25), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexIdentifierToken("arr", LexTokenSpan.of(7, 8)),
                new LexToken(LexTokenSpan.of(7, 11), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("j", LexTokenSpan.of(7, 12)),
                new LexToken(LexTokenSpan.of(7, 13), LexTokenCode.ADDITION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(7, 15), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(7,17), LexTokenCode.ASSIGNMENT),
                new LexIdentifierToken("temp", LexTokenSpan.of(7, 20)),
                new LexToken(LexTokenSpan.of(8, 6), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(9, 6), LexTokenCode.FOR),
                new LexIdentifierToken("i", LexTokenSpan.of(9, 10)),
                new LexToken(LexTokenSpan.of(9, 12), LexTokenCode.IN),
                LexLiteralToken.ofValue(0, LexTokenSpan.of(9, 15)),
                new LexToken(LexTokenSpan.of(9, 17), LexTokenCode.DOT),
                new LexToken(LexTokenSpan.of(9, 18), LexTokenCode.DOT),
                new LexIdentifierToken("n", LexTokenSpan.of(9, 20)),
                new LexToken(LexTokenSpan.of(9, 21), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(9, 24), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(10, 8), LexTokenCode.PRINT),
                new LexIdentifierToken("arr", LexTokenSpan.of(10, 14)),
                new LexToken(LexTokenSpan.of(10, 17), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("i", LexTokenSpan.of(10, 18)),
                new LexToken(LexTokenSpan.of(10, 19), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(11, 6), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(12, 4), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(13, 2), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(14, 0), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(15, 0), LexTokenCode.VAR),
                new LexIdentifierToken("arr", LexTokenSpan.of(15, 4)),
                new LexToken(LexTokenSpan.of(15, 8), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(15, 11), LexTokenCode.OPEN_SQUARE_BRACKET),
                LexLiteralToken.ofValue(11, LexTokenSpan.of(15, 12)),
                new LexToken(LexTokenSpan.of(15, 14), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(37, LexTokenSpan.of(15, 16)),
                new LexToken(LexTokenSpan.of(15, 18), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(15, LexTokenSpan.of(15, 20)),
                new LexToken(LexTokenSpan.of(15, 22), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(221, LexTokenSpan.of(15, 24)),
                new LexToken(LexTokenSpan.of(15, 27), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(113, LexTokenSpan.of(15, 29)),
                new LexToken(LexTokenSpan.of(15, 32), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(16, 0), LexTokenCode.VAR),
                new LexIdentifierToken("n", LexTokenSpan.of(16, 4)),
                new LexToken(LexTokenSpan.of(16,6), LexTokenCode.ASSIGNMENT),
                LexLiteralToken.ofValue(7, sampleSpan),
                new LexIdentifierToken("bubble", LexTokenSpan.of(17, 0)),
                new LexToken(LexTokenSpan.of(17, 7), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("arr", LexTokenSpan.of(17, 8)),
                new LexToken(LexTokenSpan.of(17, 11), LexTokenCode.COMMA),
                new LexIdentifierToken("n", LexTokenSpan.of(17, 13)),
                new LexToken(LexTokenSpan.of(17, 14), LexTokenCode.CLOSED_ROUND_BRACKET)
        );
        // When
        SyntaxAnalyser syntaxAnalyser = new SyntaxAnalyserImpl(expectedTokens);

        AstNode root = syntaxAnalyser.buildAstTree();
        System.out.println(root.toString());

        // Then
        // Resulting tree must contain 4 statements
        assert(root.getChildren().size() == 4);

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
                │                               └── LITERAL
                │                                   └── FUNCTION_LITERAL
                │                                       ├── func
                │                                       ├── PARAMETERS
                │                                       │   ├── (
                │                                       │   ├── IDENTIFIER
                │                                       │   ├── ,
                │                                       │   ├── IDENTIFIER
                │                                       │   └── )
                │                                       └── FUN_BODY
                │                                           ├── is
                │                                           ├── BODY
                │                                           │   └── STATEMENT
                │                                           │       └── LOOP
                │                                           │           ├── for
                │                                           │           ├── IDENTIFIER
                │                                           │           ├── in
                │                                           │           ├── EXPRESSION
                │                                           │           │   └── CONJUNCTION
                │                                           │           │       └── RELATION
                │                                           │           │           └── FACTOR
                │                                           │           │               └── TERM
                │                                           │           │                   └── LITERAL
                │                                           │           │                       └── INTEGER_LITERAL
                │                                           │           │                           └── LITERAL
                │                                           │           ├── .
                │                                           │           ├── .
                │                                           │           ├── EXPRESSION
                │                                           │           │   └── CONJUNCTION
                │                                           │           │       └── RELATION
                │                                           │           │           └── FACTOR
                │                                           │           │               ├── TERM
                │                                           │           │               │   └── UNARY
                │                                           │           │               │       └── PRIMARY
                │                                           │           │               │           └── IDENTIFIER
                │                                           │           │               ├── -
                │                                           │           │               └── TERM
                │                                           │           │                   └── LITERAL
                │                                           │           │                       └── INTEGER_LITERAL
                │                                           │           │                           └── LITERAL
                │                                           │           └── LOOP_BODY
                │                                           │               ├── loop
                │                                           │               ├── BODY
                │                                           │               │   ├── STATEMENT
                │                                           │               │   │   └── DECLARATION
                │                                           │               │   │       ├── var
                │                                           │               │   │       └── VARIABLE_DEFINITION
                │                                           │               │   │           ├── IDENTIFIER
                │                                           │               │   │           ├── :=
                │                                           │               │   │           └── EXPRESSION
                │                                           │               │   │               └── CONJUNCTION
                │                                           │               │   │                   └── RELATION
                │                                           │               │   │                       └── FACTOR
                │                                           │               │   │                           └── TERM
                │                                           │               │   │                               └── LITERAL
                │                                           │               │   │                                   └── INTEGER_LITERAL
                │                                           │               │   │                                       └── LITERAL
                │                                           │               │   └── STATEMENT
                │                                           │               │       └── LOOP
                │                                           │               │           ├── for
                │                                           │               │           ├── IDENTIFIER
                │                                           │               │           ├── in
                │                                           │               │           ├── EXPRESSION
                │                                           │               │           │   └── CONJUNCTION
                │                                           │               │           │       └── RELATION
                │                                           │               │           │           └── FACTOR
                │                                           │               │           │               └── TERM
                │                                           │               │           │                   └── LITERAL
                │                                           │               │           │                       └── INTEGER_LITERAL
                │                                           │               │           │                           └── LITERAL
                │                                           │               │           ├── .
                │                                           │               │           ├── .
                │                                           │               │           ├── EXPRESSION
                │                                           │               │           │   └── CONJUNCTION
                │                                           │               │           │       └── RELATION
                │                                           │               │           │           └── FACTOR
                │                                           │               │           │               ├── TERM
                │                                           │               │           │               │   └── UNARY
                │                                           │               │           │               │       └── PRIMARY
                │                                           │               │           │               │           └── IDENTIFIER
                │                                           │               │           │               ├── -
                │                                           │               │           │               ├── TERM
                │                                           │               │           │               │   └── UNARY
                │                                           │               │           │               │       └── PRIMARY
                │                                           │               │           │               │           └── IDENTIFIER
                │                                           │               │           │               ├── -
                │                                           │               │           │               └── TERM
                │                                           │               │           │                   └── LITERAL
                │                                           │               │           │                       └── INTEGER_LITERAL
                │                                           │               │           │                           └── LITERAL
                │                                           │               │           └── LOOP_BODY
                │                                           │               │               ├── loop
                │                                           │               │               ├── BODY
                │                                           │               │               │   ├── STATEMENT
                │                                           │               │               │   │   └── IF
                │                                           │               │               │   │       ├── if
                │                                           │               │               │   │       ├── EXPRESSION
                │                                           │               │               │   │       │   └── CONJUNCTION
                │                                           │               │               │   │       │       └── RELATION
                │                                           │               │               │   │       │           ├── FACTOR
                │                                           │               │               │   │       │           │   └── TERM
                │                                           │               │               │   │       │           │       └── UNARY
                │                                           │               │               │   │       │           │           └── PRIMARY
                │                                           │               │               │   │       │           │               ├── IDENTIFIER
                │                                           │               │               │   │       │           │               └── TAIL
                │                                           │               │               │   │       │           │                   ├── [
                │                                           │               │               │   │       │           │                   ├── EXPRESSION
                │                                           │               │               │   │       │           │                   │   └── CONJUNCTION
                │                                           │               │               │   │       │           │                   │       └── RELATION
                │                                           │               │               │   │       │           │                   │           └── FACTOR
                │                                           │               │               │   │       │           │                   │               └── TERM
                │                                           │               │               │   │       │           │                   │                   └── UNARY
                │                                           │               │               │   │       │           │                   │                       └── PRIMARY
                │                                           │               │               │   │       │           │                   │                           └── IDENTIFIER
                │                                           │               │               │   │       │           │                   └── ]
                │                                           │               │               │   │       │           ├── >
                │                                           │               │               │   │       │           └── FACTOR
                │                                           │               │               │   │       │               └── TERM
                │                                           │               │               │   │       │                   └── UNARY
                │                                           │               │               │   │       │                       └── PRIMARY
                │                                           │               │               │   │       │                           ├── IDENTIFIER
                │                                           │               │               │   │       │                           └── TAIL
                │                                           │               │               │   │       │                               ├── [
                │                                           │               │               │   │       │                               ├── EXPRESSION
                │                                           │               │               │   │       │                               │   └── CONJUNCTION
                │                                           │               │               │   │       │                               │       └── RELATION
                │                                           │               │               │   │       │                               │           └── FACTOR
                │                                           │               │               │   │       │                               │               ├── TERM
                │                                           │               │               │   │       │                               │               │   └── UNARY
                │                                           │               │               │   │       │                               │               │       └── PRIMARY
                │                                           │               │               │   │       │                               │               │           └── IDENTIFIER
                │                                           │               │               │   │       │                               │               ├── +
                │                                           │               │               │   │       │                               │               └── TERM
                │                                           │               │               │   │       │                               │                   └── LITERAL
                │                                           │               │               │   │       │                               │                       └── INTEGER_LITERAL
                │                                           │               │               │   │       │                               │                           └── LITERAL
                │                                           │               │               │   │       │                               └── ]
                │                                           │               │               │   │       ├── then
                │                                           │               │               │   │       ├── BODY
                │                                           │               │               │   │       │   ├── STATEMENT
                │                                           │               │               │   │       │   │   └── ASSIGNMENT
                │                                           │               │               │   │       │   │       ├── PRIMARY
                │                                           │               │               │   │       │   │       │   └── IDENTIFIER
                │                                           │               │               │   │       │   │       ├── :=
                │                                           │               │               │   │       │   │       └── EXPRESSION
                │                                           │               │               │   │       │   │           └── CONJUNCTION
                │                                           │               │               │   │       │   │               └── RELATION
                │                                           │               │               │   │       │   │                   └── FACTOR
                │                                           │               │               │   │       │   │                       └── TERM
                │                                           │               │               │   │       │   │                           └── UNARY
                │                                           │               │               │   │       │   │                               └── PRIMARY
                │                                           │               │               │   │       │   │                                   ├── IDENTIFIER
                │                                           │               │               │   │       │   │                                   └── TAIL
                │                                           │               │               │   │       │   │                                       ├── [
                │                                           │               │               │   │       │   │                                       ├── EXPRESSION
                │                                           │               │               │   │       │   │                                       │   └── CONJUNCTION
                │                                           │               │               │   │       │   │                                       │       └── RELATION
                │                                           │               │               │   │       │   │                                       │           └── FACTOR
                │                                           │               │               │   │       │   │                                       │               └── TERM
                │                                           │               │               │   │       │   │                                       │                   └── UNARY
                │                                           │               │               │   │       │   │                                       │                       └── PRIMARY
                │                                           │               │               │   │       │   │                                       │                           └── IDENTIFIER
                │                                           │               │               │   │       │   │                                       └── ]
                │                                           │               │               │   │       │   ├── STATEMENT
                │                                           │               │               │   │       │   │   └── ASSIGNMENT
                │                                           │               │               │   │       │   │       ├── PRIMARY
                │                                           │               │               │   │       │   │       │   ├── IDENTIFIER
                │                                           │               │               │   │       │   │       │   └── TAIL
                │                                           │               │               │   │       │   │       │       ├── [
                │                                           │               │               │   │       │   │       │       ├── EXPRESSION
                │                                           │               │               │   │       │   │       │       │   └── CONJUNCTION
                │                                           │               │               │   │       │   │       │       │       └── RELATION
                │                                           │               │               │   │       │   │       │       │           └── FACTOR
                │                                           │               │               │   │       │   │       │       │               └── TERM
                │                                           │               │               │   │       │   │       │       │                   └── UNARY
                │                                           │               │               │   │       │   │       │       │                       └── PRIMARY
                │                                           │               │               │   │       │   │       │       │                           └── IDENTIFIER
                │                                           │               │               │   │       │   │       │       └── ]
                │                                           │               │               │   │       │   │       ├── :=
                │                                           │               │               │   │       │   │       └── EXPRESSION
                │                                           │               │               │   │       │   │           └── CONJUNCTION
                │                                           │               │               │   │       │   │               └── RELATION
                │                                           │               │               │   │       │   │                   └── FACTOR
                │                                           │               │               │   │       │   │                       └── TERM
                │                                           │               │               │   │       │   │                           └── UNARY
                │                                           │               │               │   │       │   │                               └── PRIMARY
                │                                           │               │               │   │       │   │                                   ├── IDENTIFIER
                │                                           │               │               │   │       │   │                                   └── TAIL
                │                                           │               │               │   │       │   │                                       ├── [
                │                                           │               │               │   │       │   │                                       ├── EXPRESSION
                │                                           │               │               │   │       │   │                                       │   └── CONJUNCTION
                │                                           │               │               │   │       │   │                                       │       └── RELATION
                │                                           │               │               │   │       │   │                                       │           └── FACTOR
                │                                           │               │               │   │       │   │                                       │               ├── TERM
                │                                           │               │               │   │       │   │                                       │               │   └── UNARY
                │                                           │               │               │   │       │   │                                       │               │       └── PRIMARY
                │                                           │               │               │   │       │   │                                       │               │           └── IDENTIFIER
                │                                           │               │               │   │       │   │                                       │               ├── +
                │                                           │               │               │   │       │   │                                       │               └── TERM
                │                                           │               │               │   │       │   │                                       │                   └── LITERAL
                │                                           │               │               │   │       │   │                                       │                       └── INTEGER_LITERAL
                │                                           │               │               │   │       │   │                                       │                           └── LITERAL
                │                                           │               │               │   │       │   │                                       └── ]
                │                                           │               │               │   │       │   └── STATEMENT
                │                                           │               │               │   │       │       └── ASSIGNMENT
                │                                           │               │               │   │       │           ├── PRIMARY
                │                                           │               │               │   │       │           │   ├── IDENTIFIER
                │                                           │               │               │   │       │           │   └── TAIL
                │                                           │               │               │   │       │           │       ├── [
                │                                           │               │               │   │       │           │       ├── EXPRESSION
                │                                           │               │               │   │       │           │       │   └── CONJUNCTION
                │                                           │               │               │   │       │           │       │       └── RELATION
                │                                           │               │               │   │       │           │       │           └── FACTOR
                │                                           │               │               │   │       │           │       │               ├── TERM
                │                                           │               │               │   │       │           │       │               │   └── UNARY
                │                                           │               │               │   │       │           │       │               │       └── PRIMARY
                │                                           │               │               │   │       │           │       │               │           └── IDENTIFIER
                │                                           │               │               │   │       │           │       │               ├── +
                │                                           │               │               │   │       │           │       │               └── TERM
                │                                           │               │               │   │       │           │       │                   └── LITERAL
                │                                           │               │               │   │       │           │       │                       └── INTEGER_LITERAL
                │                                           │               │               │   │       │           │       │                           └── LITERAL
                │                                           │               │               │   │       │           │       └── ]
                │                                           │               │               │   │       │           ├── :=
                │                                           │               │               │   │       │           └── EXPRESSION
                │                                           │               │               │   │       │               └── CONJUNCTION
                │                                           │               │               │   │       │                   └── RELATION
                │                                           │               │               │   │       │                       └── FACTOR
                │                                           │               │               │   │       │                           └── TERM
                │                                           │               │               │   │       │                               └── UNARY
                │                                           │               │               │   │       │                                   └── PRIMARY
                │                                           │               │               │   │       │                                       └── IDENTIFIER
                │                                           │               │               │   │       └── end
                │                                           │               │               │   └── STATEMENT
                │                                           │               │               │       └── LOOP
                │                                           │               │               │           ├── for
                │                                           │               │               │           ├── IDENTIFIER
                │                                           │               │               │           ├── in
                │                                           │               │               │           ├── EXPRESSION
                │                                           │               │               │           │   └── CONJUNCTION
                │                                           │               │               │           │       └── RELATION
                │                                           │               │               │           │           └── FACTOR
                │                                           │               │               │           │               └── TERM
                │                                           │               │               │           │                   └── LITERAL
                │                                           │               │               │           │                       └── INTEGER_LITERAL
                │                                           │               │               │           │                           └── LITERAL
                │                                           │               │               │           ├── .
                │                                           │               │               │           ├── .
                │                                           │               │               │           ├── EXPRESSION
                │                                           │               │               │           │   └── CONJUNCTION
                │                                           │               │               │           │       └── RELATION
                │                                           │               │               │           │           └── FACTOR
                │                                           │               │               │           │               ├── TERM
                │                                           │               │               │           │               │   └── UNARY
                │                                           │               │               │           │               │       └── PRIMARY
                │                                           │               │               │           │               │           └── IDENTIFIER
                │                                           │               │               │           │               ├── -
                │                                           │               │               │           │               └── TERM
                │                                           │               │               │           │                   └── LITERAL
                │                                           │               │               │           │                       └── INTEGER_LITERAL
                │                                           │               │               │           │                           └── LITERAL
                │                                           │               │               │           └── LOOP_BODY
                │                                           │               │               │               ├── loop
                │                                           │               │               │               ├── BODY
                │                                           │               │               │               │   └── STATEMENT
                │                                           │               │               │               │       └── PRINT
                │                                           │               │               │               │           ├── print
                │                                           │               │               │               │           └── EXPRESSION
                │                                           │               │               │               │               └── CONJUNCTION
                │                                           │               │               │               │                   └── RELATION
                │                                           │               │               │               │                       └── FACTOR
                │                                           │               │               │               │                           └── TERM
                │                                           │               │               │               │                               └── UNARY
                │                                           │               │               │               │                                   └── PRIMARY
                │                                           │               │               │               │                                       ├── IDENTIFIER
                │                                           │               │               │               │                                       └── TAIL
                │                                           │               │               │               │                                           ├── [
                │                                           │               │               │               │                                           ├── EXPRESSION
                │                                           │               │               │               │                                           │   └── CONJUNCTION
                │                                           │               │               │               │                                           │       └── RELATION
                │                                           │               │               │               │                                           │           └── FACTOR
                │                                           │               │               │               │                                           │               └── TERM
                │                                           │               │               │               │                                           │                   └── UNARY
                │                                           │               │               │               │                                           │                       └── PRIMARY
                │                                           │               │               │               │                                           │                           └── IDENTIFIER
                │                                           │               │               │               │                                           └── ]
                │                                           │               │               │               └── end
                │                                           │               │               └── end
                │                                           │               └── end
                │                                           └── end
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
                │                               └── LITERAL
                │                                   └── ARRAY_LITERAL
                │                                       ├── [
                │                                       ├── EXPRESSION
                │                                       │   └── CONJUNCTION
                │                                       │       └── RELATION
                │                                       │           └── FACTOR
                │                                       │               └── TERM
                │                                       │                   └── LITERAL
                │                                       │                       └── INTEGER_LITERAL
                │                                       │                           └── LITERAL
                │                                       ├── ,
                │                                       ├── EXPRESSION
                │                                       │   └── CONJUNCTION
                │                                       │       └── RELATION
                │                                       │           └── FACTOR
                │                                       │               └── TERM
                │                                       │                   └── LITERAL
                │                                       │                       └── INTEGER_LITERAL
                │                                       │                           └── LITERAL
                │                                       ├── ,
                │                                       ├── EXPRESSION
                │                                       │   └── CONJUNCTION
                │                                       │       └── RELATION
                │                                       │           └── FACTOR
                │                                       │               └── TERM
                │                                       │                   └── LITERAL
                │                                       │                       └── INTEGER_LITERAL
                │                                       │                           └── LITERAL
                │                                       ├── ,
                │                                       ├── EXPRESSION
                │                                       │   └── CONJUNCTION
                │                                       │       └── RELATION
                │                                       │           └── FACTOR
                │                                       │               └── TERM
                │                                       │                   └── LITERAL
                │                                       │                       └── INTEGER_LITERAL
                │                                       │                           └── LITERAL
                │                                       ├── ,
                │                                       ├── EXPRESSION
                │                                       │   └── CONJUNCTION
                │                                       │       └── RELATION
                │                                       │           └── FACTOR
                │                                       │               └── TERM
                │                                       │                   └── LITERAL
                │                                       │                       └── INTEGER_LITERAL
                │                                       │                           └── LITERAL
                │                                       └── ]
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
                │                               └── LITERAL
                │                                   └── INTEGER_LITERAL
                │                                       └── LITERAL
                └── STATEMENT
                    └── EXPRESSION
                        └── CONJUNCTION
                            └── RELATION
                                └── FACTOR
                                    └── TERM
                                        └── UNARY
                                            └── PRIMARY
                                                ├── IDENTIFIER
                                                └── TAIL
                                                    ├── (
                                                    ├── EXPRESSION
                                                    │   └── CONJUNCTION
                                                    │       └── RELATION
                                                    │           └── FACTOR
                                                    │               └── TERM
                                                    │                   └── UNARY
                                                    │                       └── PRIMARY
                                                    │                           └── IDENTIFIER
                                                    ├── ,
                                                    ├── EXPRESSION
                                                    │   └── CONJUNCTION
                                                    │       └── RELATION
                                                    │           └── FACTOR
                                                    │               └── TERM
                                                    │                   └── UNARY
                                                    │                       └── PRIMARY
                                                    │                           └── IDENTIFIER
                                                    └── )
                """;
        assert(expectedStructureOfTree.equals(root.toString()));
    }

    private LexTokenSpan getSampleSpan() {
        return LexTokenSpan.of(0, 0);
    }
}
