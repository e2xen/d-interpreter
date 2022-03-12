package com.projectd.interpreter.shared.exception;

import com.projectd.interpreter.lex.token.LexLiteralTokenType;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExceptionFactory {

    public static UnexpectedTokenException unexpectedToken(Set<LexTokenCode> expectedTokens, LexTokenCode gotToken, int lineNum, int pos) {
        List<String> expectedTokenStrings = expectedTokens.stream()
                .map(LexTokenCode::toString).collect(Collectors.toList());
        String gotTokenString = gotToken.toString();
        String message = String.format("Expected one of the following tokens: %s; got %s instead", expectedTokenStrings.toString(), gotTokenString);
        return new UnexpectedTokenException(message, lineNum, pos);
    }

    public static UnexpectedTokenException unexpectedLiteralToken(Set<LexLiteralTokenType> expectedLiteralTokens, LexTokenCode gotToken, int lineNum, int pos) {
        List<String> expectedTokenStrings = expectedLiteralTokens.stream()
                .map(LexLiteralTokenType::toString).collect(Collectors.toList());
        String gotTokenString = gotToken.toString();
        String message = String.format("Expected one of the following literal tokens types: %s; got %s instead",
                expectedTokenStrings.toString(),
                gotTokenString);
        return new UnexpectedTokenException(message, lineNum, pos);
    }

    public static NoTokenException noToken(Set<LexTokenCode> expectedTokens) {
        List<String> expectedTokenStrings = expectedTokens.stream()
                .map(LexTokenCode::toString).collect(Collectors.toList());
        String message = String.format("Expected one of the following tokens: %s; got no tokens", expectedTokenStrings.toString());
        return new NoTokenException(message);
    }

    public static NoTokenException noToken() {
        String message = "Expected at least 1 token; got no tokens";
        return new NoTokenException(message);
    }

    public static AmbiguousGrammarException ambiguousGrammar(AstGrammarNodeType expectedGrammar, int lineNum, int pos) {
        String message = String.format("Could not parse %s grammar", expectedGrammar.toString());
        return new AmbiguousGrammarException(message, lineNum, pos);
    }

    public static LexBadIdentifierException badIdentifier(int lineNum, int pos) {
        return new LexBadIdentifierException("Could not parse token: incorrect identifier found", lineNum, pos);
    }
}
