package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class LexerFactorialTest {

    @Test
    public void testAlgorithm() {
        // Given
        String input = """   
                var factorial := func (x) is
                	if x == 1 then
                		return 1
                	end
                	else
                		return (x * factorial(x-1))
                	end   
                end             
                               
                var num := 3
                print ("The factorial of 3 is")
                print factorial(num)
                """;

        LexicalAnalyser lexer = new LexicalAnalyserImpl();

        // When
        List<LexToken> result = lexer.analyse(Arrays.stream(input.split("\\r?\\n")).toList());
        result.forEach(System.out::println);

        // Then
        List<LexToken> expectedTokens = List.of(
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.VAR),
                new LexIdentifierToken("factorial", LexTokenSpan.of(0, 4)),
                new LexToken(LexTokenSpan.of(0, 14), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(0, 17), LexTokenCode.FUNC),
                new LexToken(LexTokenSpan.of(0, 22), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("x", LexTokenSpan.of(0, 23)),
                new LexToken(LexTokenSpan.of(0, 24), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(0, 26), LexTokenCode.IS),
                new LexToken(LexTokenSpan.of(1, 1), LexTokenCode.IF),
                new LexIdentifierToken("x", LexTokenSpan.of(1, 4)),
                new LexToken(LexTokenSpan.of(1, 6), LexTokenCode.EQUAL),
                new LexToken(LexTokenSpan.of(1, 7), LexTokenCode.EQUAL),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(1, 11)),
                new LexToken(LexTokenSpan.of(1, 11), LexTokenCode.THEN),
                new LexToken(LexTokenSpan.of(2, 2), LexTokenCode.RETURN),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(2, 9)),
                new LexToken(LexTokenSpan.of(3, 1), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(4, 1), LexTokenCode.ELSE),
                new LexToken(LexTokenSpan.of(5, 2), LexTokenCode.RETURN),
                new LexToken(LexTokenSpan.of(5, 9), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("x", LexTokenSpan.of(5, 10)),
                new LexToken(LexTokenSpan.of(5, 12), LexTokenCode.MULTIPLICATION),
                new LexIdentifierToken("factorial", LexTokenSpan.of(5, 14)),
                new LexToken(LexTokenSpan.of(5, 23), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("x", LexTokenSpan.of(5, 24)),
                new LexToken(LexTokenSpan.of(5, 25), LexTokenCode.SUBTRACTION),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(5, 26)),
                new LexToken(LexTokenSpan.of(5, 27), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(5, 28), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(6, 1), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(7, 0), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(9, 0), LexTokenCode.VAR),
                new LexIdentifierToken("num", LexTokenSpan.of(9, 4)),
                new LexToken(LexTokenSpan.of(9, 8), LexTokenCode.ASSIGNMENT),
                LexLiteralToken.ofValue(3, LexTokenSpan.of(9, 11)),
                new LexToken(LexTokenSpan.of(10, 0), LexTokenCode.PRINT),
                new LexToken(LexTokenSpan.of(10, 6), LexTokenCode.OPEN_ROUND_BRACKET),
                LexLiteralToken.ofValue("The factorial of 3 is", LexTokenSpan.of(10, 8)),
                new LexToken(LexTokenSpan.of(10, 30), LexTokenCode.CLOSED_ROUND_BRACKET),
                new LexToken(LexTokenSpan.of(11, 0), LexTokenCode.PRINT),
                new LexIdentifierToken("factorial", LexTokenSpan.of(11, 6)),
                new LexToken(LexTokenSpan.of(11, 15), LexTokenCode.OPEN_ROUND_BRACKET),
                new LexIdentifierToken("num", LexTokenSpan.of(11, 16)),
                new LexToken(LexTokenSpan.of(11, 19), LexTokenCode.CLOSED_ROUND_BRACKET)
                );



        assert (result.equals(expectedTokens));
    }

}
