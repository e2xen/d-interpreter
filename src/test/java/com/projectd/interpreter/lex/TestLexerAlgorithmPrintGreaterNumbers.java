package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestLexerAlgorithmPrintGreaterNumbers {

    @Test
    public void test() {
        String input = """
               var arr := [1, 2, 3]
                                
                for i in 0..3 loop\040
                	if arr[i] >= 1 then\040
                		print arr[i]
                	end\040
                end\040
                """;

        System.out.println(Arrays.stream(input.split("\\r?\\n")).toList());
        LexicalAnalyserImpl lexer = new LexicalAnalyserImpl();
        System.out.println(lexer.analyse(Arrays.stream(input.split("\\r?\\n")).toList()));

        List<LexToken> expectedTokens = List.of(
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.VAR),
                new LexIdentifierToken("arr", LexTokenSpan.of(0, 4)),
                new LexToken(LexTokenSpan.of(0, 8), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(0, 11), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(2, 1), LexTokenCode.FOR),
                new LexIdentifierToken("i", LexTokenSpan.of(2, 5)),
                new LexToken(LexTokenSpan.of(2, 7), LexTokenCode.IN),
                new LexToken(LexTokenSpan.of(2, 15), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(3, 12), LexTokenCode.MORE_OR_EQUAL),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(3, 15)),
                new LexToken(LexTokenSpan.of(3, 17), LexTokenCode.THEN)
        );
    }

}
