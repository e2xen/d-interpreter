package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.syntax.exception.UnbalancedBracketsException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static com.projectd.interpreter.lex.token.LexTokenCode.*;

class BracketsBalancer {
    private static final Set<LexTokenCode> openBrackets = Set.of(OPEN_CURLY_BRACKET, OPEN_SQUARE_BRACKET);
    private static final Set<LexTokenCode> closeBrackets = Set.of(CLOSED_CURLY_BRACKET, CLOSE_SQUARE_BRACKET);
    private static final Map<LexTokenCode, LexTokenCode> closedToAppropriateOpenBracket = Map.ofEntries(
            Map.entry(CLOSED_CURLY_BRACKET, OPEN_CURLY_BRACKET),
            Map.entry(CLOSE_SQUARE_BRACKET, OPEN_SQUARE_BRACKET)
    );

    public static void verifyBalanceTokenSequence(ArrayList<LexToken> tokens) throws UnbalancedBracketsException {
        Stack<LexTokenCode> stack = new Stack<LexTokenCode>();

        for(LexToken token : tokens) {
            LexTokenCode currentCode = token.getCode();

            if (!(openBrackets.contains(currentCode) || closeBrackets.contains(currentCode))) continue;

            if(openBrackets.contains(currentCode) && (stack.isEmpty() || openBrackets.contains(stack.lastElement()))) {
                stack.push(currentCode);
            } else if(closeBrackets.contains(currentCode) && (!stack.isEmpty() &&
                    closedToAppropriateOpenBracket.get(currentCode).equals(stack.lastElement()))) {
                stack.pop();
            } else {
                throw new UnbalancedBracketsException(String.format("Unbalanced brackets exception: got %s, but expected %s on %s",
                        currentCode, stack.lastElement(), token.getSpan()));
            }
        }
    }
}
