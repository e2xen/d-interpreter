package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LexerFibonacciTest {

    @Test
    public void testAlgorithm() {
        // Given
        String input = """   
                var fibo := func (n) is
                	if n <= 1 then
                		return n
                	end
                	else
                		return fibo(n-1) + fibo(n-2)
                	end
                end
                                
                print ("Fibonacci sequence:")
                for i in 0 .. 10 loop
                    print fibo(i)
                end
                """;

        LexicalAnalyser lexer = new LexicalAnalyserImpl();

        // When
        List<LexToken> result = lexer.analyse(Arrays.stream(input.split("\\r?\\n")).toList());
        result.forEach(System.out::println);

        // Then
        List<LexToken> expectedTokens = List.of(
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.VAR),
                new LexIdentifierToken("fibo", LexTokenSpan.of(0, 4)),
                new LexToken(LexTokenSpan.of(0, 9), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(0, 12), LexTokenCode.FUNC),
                new LexToken(LexTokenSpan.of(0, 17), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("n", LexTokenSpan.of(0, 18)),
                new LexToken(LexTokenSpan.of(0, 19), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(0, 21), LexTokenCode.IS),
                new LexToken(LexTokenSpan.of(1, 1), LexTokenCode.IF),
                new LexIdentifierToken("n", LexTokenSpan.of(1, 4)),
                new LexToken(LexTokenSpan.of(1, 6), LexTokenCode.LESS_OR_EQUAL),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(1, 9)),
                new LexToken(LexTokenSpan.of(1, 11), LexTokenCode.THEN),
                new LexToken(LexTokenSpan.of(2, 2), LexTokenCode.RETURN),
                new LexIdentifierToken("n", LexTokenSpan.of(2, 9)),
                new LexToken(LexTokenSpan.of(3, 1), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(4, 1), LexTokenCode.ELSE),
                new LexToken(LexTokenSpan.of(5, 2), LexTokenCode.RETURN),
                new LexIdentifierToken("fibo", LexTokenSpan.of(5, 9)),
                new LexToken(LexTokenSpan.of(5, 13), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("n", LexTokenSpan.of(5, 14)),
                new LexToken(LexTokenSpan.of(5, 15), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(5, 16)),
                new LexToken(LexTokenSpan.of(5, 17), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(5, 19), LexTokenCode.ADDITION),
                new LexIdentifierToken("fibo", LexTokenSpan.of(5, 21)),
                new LexToken(LexTokenSpan.of(5, 25), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("n", LexTokenSpan.of(5, 26)),
                new LexToken(LexTokenSpan.of(5, 27), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(2, LexTokenSpan.of(5, 28)),
                new LexToken(LexTokenSpan.of(5, 29), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(6, 1), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(7, 0), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(9, 0), LexTokenCode.PRINT),
                new LexToken(LexTokenSpan.of(9, 6), LexTokenCode.OPEN_ROUND_BRACKET),
                LexLiteralToken.ofValue("Fibonacci sequence:", LexTokenSpan.of(9, 8)),
                new LexToken(LexTokenSpan.of(9, 28), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(10, 0), LexTokenCode.FOR),
                new LexIdentifierToken("i", LexTokenSpan.of(10, 4)),
                new LexToken(LexTokenSpan.of(10, 6), LexTokenCode.IN),
                LexLiteralToken.ofValue(0, LexTokenSpan.of(10, 9)),
                new LexToken(LexTokenSpan.of(10, 11), LexTokenCode.DOT),
                new LexToken(LexTokenSpan.of(10, 12), LexTokenCode.DOT),
                LexLiteralToken.ofValue(10, LexTokenSpan.of(10, 14)),
                new LexToken(LexTokenSpan.of(10, 17), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(11, 4), LexTokenCode.PRINT),
                new LexIdentifierToken("fibo", LexTokenSpan.of(11, 10)),
                new LexToken(LexTokenSpan.of(11, 14), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("i", LexTokenSpan.of(11, 15)),
                new LexToken(LexTokenSpan.of(11, 16), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(12, 0), LexTokenCode.END)
                );
        assert (result.equals(expectedTokens));
    }

}
