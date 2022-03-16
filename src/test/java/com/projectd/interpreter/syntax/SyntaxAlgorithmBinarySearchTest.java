package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.syntax.tree.AstNode;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SyntaxAlgorithmBinarySearchTest {

    @Test
    public void testAlgorithm() {
        // Given
        LexTokenSpan sampleSpan = getSampleSpan();
        List<LexToken> expectedTokens = List.of(
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.VAR),
                new LexIdentifierToken("arr", LexTokenSpan.of(0, 4)),
                new LexToken(LexTokenSpan.of(0, 8), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(0, 11), LexTokenCode.OPEN_SQUARE_BRACKET),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(0, 12)),
                new LexToken(LexTokenSpan.of(0, 13), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(4, LexTokenSpan.of(0, 15)),
                new LexToken(LexTokenSpan.of(0, 16), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(7, LexTokenSpan.of(0, 18)),
                new LexToken(LexTokenSpan.of(0, 19), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(15, LexTokenSpan.of(0, 21)),
                new LexToken(LexTokenSpan.of(0, 23), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(30, LexTokenSpan.of(0, 25)),
                new LexToken(LexTokenSpan.of(0, 27), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(102, LexTokenSpan.of(0, 29)),
                new LexToken(LexTokenSpan.of(0, 32), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(113, LexTokenSpan.of(0, 34)),
                new LexToken(LexTokenSpan.of(0, 37), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(1, 0), LexTokenCode.VAR),
                new LexIdentifierToken("first", LexTokenSpan.of(1, 4)),
                new LexToken(LexTokenSpan.of(1, 10), LexTokenCode.ASSIGNMENT),
                LexLiteralToken.ofValue(0, sampleSpan),
                new LexToken(LexTokenSpan.of(2, 0), LexTokenCode.VAR),
                new LexIdentifierToken("last", LexTokenSpan.of(2, 4)),
                new LexToken(LexTokenSpan.of(2, 9), LexTokenCode.ASSIGNMENT),
                LexLiteralToken.ofValue(6, sampleSpan),
                new LexToken(LexTokenSpan.of(3, 0), LexTokenCode.VAR),
                new LexIdentifierToken("key", LexTokenSpan.of(3, 4)),
                new LexToken(LexTokenSpan.of(3, 8), LexTokenCode.ASSIGNMENT),
                LexLiteralToken.ofValue(15, sampleSpan),
                new LexToken(LexTokenSpan.of(4, 0), LexTokenCode.VAR),
                new LexIdentifierToken("mid", LexTokenSpan.of(4, 4)),
                new LexToken(LexTokenSpan.of(4, 8), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(4, 11), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("first", LexTokenSpan.of(4, 12)),
                new LexToken(LexTokenSpan.of(4, 18), LexTokenCode.ADDITION),
                new LexIdentifierToken("last", LexTokenSpan.of(4, 20)),
                new LexToken(LexTokenSpan.of(4, 24), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(4, 25), LexTokenCode.DIVISION),
                LexLiteralToken.ofValue(2, sampleSpan),
                new LexToken(LexTokenSpan.of(5, 0), LexTokenCode.VAR),
                new LexIdentifierToken("binary", LexTokenSpan.of(5, 4)),
                new LexToken(LexTokenSpan.of(5, 11), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(5, 14), LexTokenCode.FUNC),
                new LexToken(LexTokenSpan.of(5, 19), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("arr", LexTokenSpan.of(5, 20)),
                new LexToken(LexTokenSpan.of(5, 23), LexTokenCode.COMMA),
                new LexIdentifierToken("first", LexTokenSpan.of(5, 25)),
                new LexToken(LexTokenSpan.of(5, 30), LexTokenCode.COMMA),
                new LexIdentifierToken("last", LexTokenSpan.of(5, 32)),
                new LexToken(LexTokenSpan.of(5, 36), LexTokenCode.COMMA),
                new LexIdentifierToken("mid", LexTokenSpan.of(5, 38)),
                new LexToken(LexTokenSpan.of(5, 41), LexTokenCode.COMMA),
                new LexIdentifierToken("key", LexTokenSpan.of(5, 43)),
                new LexToken(LexTokenSpan.of(5, 46), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(5, 48), LexTokenCode.IS),
                new LexToken(LexTokenSpan.of(6, 2), LexTokenCode.FOR),
                new LexIdentifierToken("i", LexTokenSpan.of(6, 6)),
                new LexToken(LexTokenSpan.of(6, 8), LexTokenCode.IN),
                new LexIdentifierToken("first", LexTokenSpan.of(6, 11)),
                new LexToken(LexTokenSpan.of(6, 17), LexTokenCode.DOT),
                new LexToken(LexTokenSpan.of(6, 18), LexTokenCode.DOT),
                new LexIdentifierToken("last", LexTokenSpan.of(6, 20)),
                new LexToken(LexTokenSpan.of(6, 25), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(7, 4), LexTokenCode.IF),
                new LexIdentifierToken("arr", LexTokenSpan.of(7, 7)),
                new LexToken(LexTokenSpan.of(7, 10), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("mid", LexTokenSpan.of(7, 11)),
                new LexToken(LexTokenSpan.of(7, 14), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(7, 16), LexTokenCode.LESS),
                new LexIdentifierToken("key", LexTokenSpan.of(7, 18)),
                new LexToken(LexTokenSpan.of(7, 22), LexTokenCode.THEN),
                new LexIdentifierToken("first", LexTokenSpan.of(8, 6)),
                new LexToken(LexTokenSpan.of(8, 12), LexTokenCode.ASSIGNMENT),
                new LexIdentifierToken("mid", LexTokenSpan.of(8, 15)),
                new LexToken(LexTokenSpan.of(8, 19), LexTokenCode.ADDITION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(9, 4), LexTokenCode.ELSE),
                new LexToken(LexTokenSpan.of(10, 6), LexTokenCode.IF),
                new LexIdentifierToken("arr", LexTokenSpan.of(10, 9)),
                new LexToken(LexTokenSpan.of(10, 12), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("mid", LexTokenSpan.of(10, 13)),
                new LexToken(LexTokenSpan.of(10, 16), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(10, 18), LexTokenCode.EQUAL),
                new LexIdentifierToken("key", LexTokenSpan.of(10, 21)),
                new LexToken(LexTokenSpan.of(10, 24), LexTokenCode.THEN),
                new LexToken(LexTokenSpan.of(11, 8), LexTokenCode.RETURN),
                new LexIdentifierToken("mid", LexTokenSpan.of(11, 15)),
                new LexToken(LexTokenSpan.of(12, 6), LexTokenCode.ELSE),
                new LexIdentifierToken("last", LexTokenSpan.of(13, 8)),
                new LexToken(LexTokenSpan.of(13, 13), LexTokenCode.ASSIGNMENT),
                new LexIdentifierToken("mid", LexTokenSpan.of(13, 16)),
                new LexToken(LexTokenSpan.of(13, 20), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(1, sampleSpan),
                new LexToken(LexTokenSpan.of(14, 6), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(15, 4), LexTokenCode.END),
                new LexIdentifierToken("mid", LexTokenSpan.of(16, 4)),
                new LexToken(LexTokenSpan.of(16, 8), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(16, 11), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("first", LexTokenSpan.of(16, 12)),
                new LexToken(LexTokenSpan.of(16, 18), LexTokenCode.ADDITION),
                new LexIdentifierToken("last", LexTokenSpan.of(16, 20)),
                new LexToken(LexTokenSpan.of(16, 24), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(16, 25), LexTokenCode.DIVISION),
                LexLiteralToken.ofValue(2, sampleSpan),
                new LexToken(LexTokenSpan.of(17, 4), LexTokenCode.IF),
                new LexIdentifierToken("first", LexTokenSpan.of(17, 7)),
                new LexToken(LexTokenSpan.of(17, 13), LexTokenCode.MORE),
                new LexIdentifierToken("last", LexTokenSpan.of(17, 15)),
                new LexToken(LexTokenSpan.of(17, 20), LexTokenCode.THEN),
                new LexToken(LexTokenSpan.of(18, 6), LexTokenCode.RETURN),
                LexLiteralToken.ofValue("Element is not found!", LexTokenSpan.of(18, 14)),
                new LexToken(LexTokenSpan.of(19, 4), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(20, 2), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(21, 0), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(22, 0), LexTokenCode.PRINT),
                new LexIdentifierToken("binary", LexTokenSpan.of(22, 6)),
                new LexToken(LexTokenSpan.of(22, 12), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("arr", LexTokenSpan.of(22, 13)),
                new LexToken(LexTokenSpan.of(22, 16), LexTokenCode.COMMA),
                new LexIdentifierToken("first", LexTokenSpan.of(22, 18)),
                new LexToken(LexTokenSpan.of(22, 23), LexTokenCode.COMMA),
                new LexIdentifierToken("last", LexTokenSpan.of(22, 25)),
                new LexToken(LexTokenSpan.of(22, 29), LexTokenCode.COMMA),
                new LexIdentifierToken("mid", LexTokenSpan.of(22, 31)),
                new LexToken(LexTokenSpan.of(22, 34), LexTokenCode.COMMA),
                new LexIdentifierToken("key", LexTokenSpan.of(22, 36)),
                new LexToken(LexTokenSpan.of(22, 39), LexTokenCode.CLOSED_ROUND_BRACKET)
        );

        // When
        SyntaxAnalyser syntaxAnalyser = new SyntaxAnalyserImpl(expectedTokens);

        AstNode root = syntaxAnalyser.buildAstTree();
        System.out.println(root.toString());

        // Then
        // Resulting tree must contain 7 statements
        assert(root.getChildren().size() == 7);

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
                │                                       └── LITERAL
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
                │                                       └── LITERAL
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
                │                                       └── LITERAL
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
                │                               ├── UNARY
                │                               │   ├── (
                │                               │   ├── EXPRESSION
                │                               │   │   └── CONJUNCTION
                │                               │   │       └── RELATION
                │                               │   │           └── FACTOR
                │                               │   │               ├── TERM
                │                               │   │               │   └── UNARY
                │                               │   │               │       └── PRIMARY
                │                               │   │               │           └── IDENTIFIER
                │                               │   │               ├── +
                │                               │   │               └── TERM
                │                               │   │                   └── UNARY
                │                               │   │                       └── PRIMARY
                │                               │   │                           └── IDENTIFIER
                │                               │   └── )
                │                               ├── /
                │                               └── UNARY
                │                                   └── LITERAL
                │                                       └── LITERAL
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
                │                                       └── FUNCTION_LITERAL
                │                                           ├── func
                │                                           ├── PARAMETERS
                │                                           │   ├── (
                │                                           │   ├── IDENTIFIER
                │                                           │   ├── ,
                │                                           │   ├── IDENTIFIER
                │                                           │   ├── ,
                │                                           │   ├── IDENTIFIER
                │                                           │   ├── ,
                │                                           │   ├── IDENTIFIER
                │                                           │   ├── ,
                │                                           │   ├── IDENTIFIER
                │                                           │   └── )
                │                                           └── FUN_BODY
                │                                               ├── is
                │                                               ├── BODY
                │                                               │   └── STATEMENT
                │                                               │       └── LOOP
                │                                               │           ├── for
                │                                               │           ├── IDENTIFIER
                │                                               │           ├── in
                │                                               │           ├── EXPRESSION
                │                                               │           │   └── CONJUNCTION
                │                                               │           │       └── RELATION
                │                                               │           │           └── FACTOR
                │                                               │           │               └── TERM
                │                                               │           │                   └── UNARY
                │                                               │           │                       └── PRIMARY
                │                                               │           │                           └── IDENTIFIER
                │                                               │           ├── .
                │                                               │           ├── .
                │                                               │           ├── EXPRESSION
                │                                               │           │   └── CONJUNCTION
                │                                               │           │       └── RELATION
                │                                               │           │           └── FACTOR
                │                                               │           │               └── TERM
                │                                               │           │                   └── UNARY
                │                                               │           │                       └── PRIMARY
                │                                               │           │                           └── IDENTIFIER
                │                                               │           └── LOOP_BODY
                │                                               │               ├── loop
                │                                               │               ├── BODY
                │                                               │               │   ├── STATEMENT
                │                                               │               │   │   └── IF
                │                                               │               │   │       ├── if
                │                                               │               │   │       ├── EXPRESSION
                │                                               │               │   │       │   └── CONJUNCTION
                │                                               │               │   │       │       └── RELATION
                │                                               │               │   │       │           ├── FACTOR
                │                                               │               │   │       │           │   └── TERM
                │                                               │               │   │       │           │       └── UNARY
                │                                               │               │   │       │           │           └── PRIMARY
                │                                               │               │   │       │           │               ├── IDENTIFIER
                │                                               │               │   │       │           │               └── TAIL
                │                                               │               │   │       │           │                   ├── [
                │                                               │               │   │       │           │                   ├── EXPRESSION
                │                                               │               │   │       │           │                   │   └── CONJUNCTION
                │                                               │               │   │       │           │                   │       └── RELATION
                │                                               │               │   │       │           │                   │           └── FACTOR
                │                                               │               │   │       │           │                   │               └── TERM
                │                                               │               │   │       │           │                   │                   └── UNARY
                │                                               │               │   │       │           │                   │                       └── PRIMARY
                │                                               │               │   │       │           │                   │                           └── IDENTIFIER
                │                                               │               │   │       │           │                   └── ]
                │                                               │               │   │       │           ├── <
                │                                               │               │   │       │           └── FACTOR
                │                                               │               │   │       │               └── TERM
                │                                               │               │   │       │                   └── UNARY
                │                                               │               │   │       │                       └── PRIMARY
                │                                               │               │   │       │                           └── IDENTIFIER
                │                                               │               │   │       ├── then
                │                                               │               │   │       ├── BODY
                │                                               │               │   │       │   └── STATEMENT
                │                                               │               │   │       │       └── ASSIGNMENT
                │                                               │               │   │       │           ├── PRIMARY
                │                                               │               │   │       │           │   └── IDENTIFIER
                │                                               │               │   │       │           ├── :=
                │                                               │               │   │       │           └── EXPRESSION
                │                                               │               │   │       │               └── CONJUNCTION
                │                                               │               │   │       │                   └── RELATION
                │                                               │               │   │       │                       └── FACTOR
                │                                               │               │   │       │                           ├── TERM
                │                                               │               │   │       │                           │   └── UNARY
                │                                               │               │   │       │                           │       └── PRIMARY
                │                                               │               │   │       │                           │           └── IDENTIFIER
                │                                               │               │   │       │                           ├── +
                │                                               │               │   │       │                           └── TERM
                │                                               │               │   │       │                               └── UNARY
                │                                               │               │   │       │                                   └── LITERAL
                │                                               │               │   │       │                                       └── LITERAL
                │                                               │               │   │       ├── else
                │                                               │               │   │       ├── BODY
                │                                               │               │   │       │   └── STATEMENT
                │                                               │               │   │       │       └── IF
                │                                               │               │   │       │           ├── if
                │                                               │               │   │       │           ├── EXPRESSION
                │                                               │               │   │       │           │   └── CONJUNCTION
                │                                               │               │   │       │           │       └── RELATION
                │                                               │               │   │       │           │           ├── FACTOR
                │                                               │               │   │       │           │           │   └── TERM
                │                                               │               │   │       │           │           │       └── UNARY
                │                                               │               │   │       │           │           │           └── PRIMARY
                │                                               │               │   │       │           │           │               ├── IDENTIFIER
                │                                               │               │   │       │           │           │               └── TAIL
                │                                               │               │   │       │           │           │                   ├── [
                │                                               │               │   │       │           │           │                   ├── EXPRESSION
                │                                               │               │   │       │           │           │                   │   └── CONJUNCTION
                │                                               │               │   │       │           │           │                   │       └── RELATION
                │                                               │               │   │       │           │           │                   │           └── FACTOR
                │                                               │               │   │       │           │           │                   │               └── TERM
                │                                               │               │   │       │           │           │                   │                   └── UNARY
                │                                               │               │   │       │           │           │                   │                       └── PRIMARY
                │                                               │               │   │       │           │           │                   │                           └── IDENTIFIER
                │                                               │               │   │       │           │           │                   └── ]
                │                                               │               │   │       │           │           ├── =
                │                                               │               │   │       │           │           └── FACTOR
                │                                               │               │   │       │           │               └── TERM
                │                                               │               │   │       │           │                   └── UNARY
                │                                               │               │   │       │           │                       └── PRIMARY
                │                                               │               │   │       │           │                           └── IDENTIFIER
                │                                               │               │   │       │           ├── then
                │                                               │               │   │       │           ├── BODY
                │                                               │               │   │       │           │   └── STATEMENT
                │                                               │               │   │       │           │       └── RETURN
                │                                               │               │   │       │           │           ├── return
                │                                               │               │   │       │           │           └── EXPRESSION
                │                                               │               │   │       │           │               └── CONJUNCTION
                │                                               │               │   │       │           │                   └── RELATION
                │                                               │               │   │       │           │                       └── FACTOR
                │                                               │               │   │       │           │                           └── TERM
                │                                               │               │   │       │           │                               └── UNARY
                │                                               │               │   │       │           │                                   └── PRIMARY
                │                                               │               │   │       │           │                                       └── IDENTIFIER
                │                                               │               │   │       │           ├── else
                │                                               │               │   │       │           ├── BODY
                │                                               │               │   │       │           │   └── STATEMENT
                │                                               │               │   │       │           │       └── ASSIGNMENT
                │                                               │               │   │       │           │           ├── PRIMARY
                │                                               │               │   │       │           │           │   └── IDENTIFIER
                │                                               │               │   │       │           │           ├── :=
                │                                               │               │   │       │           │           └── EXPRESSION
                │                                               │               │   │       │           │               └── CONJUNCTION
                │                                               │               │   │       │           │                   └── RELATION
                │                                               │               │   │       │           │                       └── FACTOR
                │                                               │               │   │       │           │                           ├── TERM
                │                                               │               │   │       │           │                           │   └── UNARY
                │                                               │               │   │       │           │                           │       └── PRIMARY
                │                                               │               │   │       │           │                           │           └── IDENTIFIER
                │                                               │               │   │       │           │                           ├── -
                │                                               │               │   │       │           │                           └── TERM
                │                                               │               │   │       │           │                               └── UNARY
                │                                               │               │   │       │           │                                   └── LITERAL
                │                                               │               │   │       │           │                                       └── LITERAL
                │                                               │               │   │       │           └── end
                │                                               │               │   │       └── end
                │                                               │               │   ├── STATEMENT
                │                                               │               │   │   └── ASSIGNMENT
                │                                               │               │   │       ├── PRIMARY
                │                                               │               │   │       │   └── IDENTIFIER
                │                                               │               │   │       ├── :=
                │                                               │               │   │       └── EXPRESSION
                │                                               │               │   │           └── CONJUNCTION
                │                                               │               │   │               └── RELATION
                │                                               │               │   │                   └── FACTOR
                │                                               │               │   │                       └── TERM
                │                                               │               │   │                           ├── UNARY
                │                                               │               │   │                           │   ├── (
                │                                               │               │   │                           │   ├── EXPRESSION
                │                                               │               │   │                           │   │   └── CONJUNCTION
                │                                               │               │   │                           │   │       └── RELATION
                │                                               │               │   │                           │   │           └── FACTOR
                │                                               │               │   │                           │   │               ├── TERM
                │                                               │               │   │                           │   │               │   └── UNARY
                │                                               │               │   │                           │   │               │       └── PRIMARY
                │                                               │               │   │                           │   │               │           └── IDENTIFIER
                │                                               │               │   │                           │   │               ├── +
                │                                               │               │   │                           │   │               └── TERM
                │                                               │               │   │                           │   │                   └── UNARY
                │                                               │               │   │                           │   │                       └── PRIMARY
                │                                               │               │   │                           │   │                           └── IDENTIFIER
                │                                               │               │   │                           │   └── )
                │                                               │               │   │                           ├── /
                │                                               │               │   │                           └── UNARY
                │                                               │               │   │                               └── LITERAL
                │                                               │               │   │                                   └── LITERAL
                │                                               │               │   └── STATEMENT
                │                                               │               │       └── IF
                │                                               │               │           ├── if
                │                                               │               │           ├── EXPRESSION
                │                                               │               │           │   └── CONJUNCTION
                │                                               │               │           │       └── RELATION
                │                                               │               │           │           ├── FACTOR
                │                                               │               │           │           │   └── TERM
                │                                               │               │           │           │       └── UNARY
                │                                               │               │           │           │           └── PRIMARY
                │                                               │               │           │           │               └── IDENTIFIER
                │                                               │               │           │           ├── >
                │                                               │               │           │           └── FACTOR
                │                                               │               │           │               └── TERM
                │                                               │               │           │                   └── UNARY
                │                                               │               │           │                       └── PRIMARY
                │                                               │               │           │                           └── IDENTIFIER
                │                                               │               │           ├── then
                │                                               │               │           ├── BODY
                │                                               │               │           │   └── STATEMENT
                │                                               │               │           │       └── RETURN
                │                                               │               │           │           ├── return
                │                                               │               │           │           └── EXPRESSION
                │                                               │               │           │               └── CONJUNCTION
                │                                               │               │           │                   └── RELATION
                │                                               │               │           │                       └── FACTOR
                │                                               │               │           │                           └── TERM
                │                                               │               │           │                               └── UNARY
                │                                               │               │           │                                   └── LITERAL
                │                                               │               │           │                                       └── LITERAL
                │                                               │               │           └── end
                │                                               │               └── end
                │                                               └── end
                └── STATEMENT
                    └── PRINT
                        ├── print
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
                                                        ├── ,
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
