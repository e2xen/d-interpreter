package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.shared.exception.ExceptionFactory;
import com.projectd.interpreter.shared.exception.SyntaxAnalyzerParseException;
import com.projectd.interpreter.syntax.iterator.LexTokenIterator;
import com.projectd.interpreter.syntax.tree.AstNode;
import com.projectd.interpreter.syntax.tree.AstTokenNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class SyntaxAnalyser {

    protected final LexTokenIterator iterator;

    public SyntaxAnalyser(List<LexToken> tokens) {
        this.iterator = new LexTokenIterator(tokens);
    }

    abstract public AstNode buildAstTree();

    @SafeVarargs
    protected final AstNode parseAnyOf(AstNode parent, Function<AstNode, AstNode>... parseTries) {
        AstNode parseResult = null;
        for (Function<AstNode, AstNode> parseTry : parseTries) {
            iterator.checkpoint();
            try {
                parseResult = parseTry.apply(parent);
                iterator.discardCheckpoint();
            } catch (SyntaxAnalyzerParseException e) {
                iterator.rollback();
                continue;
            }

            if (parseResult != null) {
                break;
            }
        }
        return parseResult;
    }

    @SafeVarargs
    protected final List<AstNode> parseSeries(AstNode parent, Function<AstNode, AstNode>... parseSeries) {
        List<AstNode> parseResult = new ArrayList<>();
        for (Function<AstNode, AstNode> parse : parseSeries) {
            parseResult.add(parse.apply(parent));
        }
        return parseResult;
    }

    @SafeVarargs
    protected final List<AstNode> parseOptionalSeries(AstNode parent, Function<AstNode, AstNode>... parseSeries) {
        List<AstNode> parseResult = new ArrayList<>();
        iterator.checkpoint();
        for (Function<AstNode, AstNode> parse : parseSeries) {
            try {
                parseResult.add(parse.apply(parent));
            } catch (SyntaxAnalyzerParseException e) {
                iterator.rollback();
                parseResult = new ArrayList<>();
                break;
            }
        }

        if (parseResult.size() > 0) {
            iterator.discardCheckpoint();
        }

        return parseResult;
    }

    @SafeVarargs
    protected final List<AstNode> parseRepeated(AstNode parent, Function<AstNode, AstNode>... parsePattern) {
        List<AstNode> parseResult = new ArrayList<>();
        while (iterator.hasNext()) {
            iterator.checkpoint();
            try {
                parseResult.addAll(parseSeries(parent, parsePattern));
                iterator.discardCheckpoint();
            } catch (SyntaxAnalyzerParseException e) {
                iterator.rollback();
                break;
            }
        }
        return parseResult;
    }

    protected AstTokenNode parseToken(Set<LexTokenCode> expectedTokens, AstNode parent) {
        if (!iterator.hasNext()) {
            throw ExceptionFactory.noToken(expectedTokens);
        }
        LexToken token = iterator.next();
        if (!expectedTokens.contains(token.getCode())) {
            LexTokenSpan nextSpan = token.getSpan();
            throw ExceptionFactory.unexpectedToken(expectedTokens, token.getCode(), nextSpan.getLineNum(), nextSpan.getPos());
        }

        return new AstTokenNode(token, parent);
    }

    protected AstNode parseLiteralToken(Set<LexLiteralTokenType> expectedLiteralTypes, AstNode parent) {
        Set<LexTokenCode> expectedTokens = Set.of(LexTokenCode.LITERAL);
        AstTokenNode token = parseToken(expectedTokens, parent);

        if(!(token.getToken() instanceof LexLiteralToken literalToken)) {
            LexTokenSpan nextSpan = token.getToken().getSpan();
            throw ExceptionFactory.unexpectedToken(expectedTokens, token.getToken().getCode(), nextSpan.getLineNum(), nextSpan.getPos());
        }

        if(!expectedLiteralTypes.contains(literalToken.getType())) {
            LexTokenSpan nextSpan = token.getToken().getSpan();
            throw ExceptionFactory.unexpectedLiteralToken(expectedLiteralTypes, token.getToken().getCode(), nextSpan.getLineNum(), nextSpan.getPos());
        }

        return token;
    }

    protected Function<AstNode, AstNode> parseToken(Set<LexTokenCode> expectedTokens) {
        return parent -> parseToken(expectedTokens, parent);
    }

    protected Function<AstNode, AstNode> parseToken(LexTokenCode expectedToken) {
        return parent -> parseToken(Set.of(expectedToken), parent);
    }

    protected Function<AstNode, AstNode> parseLiteralToken(LexLiteralTokenType expectedLiteralTokenType) {
        return parent -> parseLiteralToken(Set.of(expectedLiteralTokenType), parent);
    }
}
