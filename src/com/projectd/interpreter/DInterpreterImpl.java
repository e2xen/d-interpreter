package com.projectd.interpreter;

import com.projectd.interpreter.lex.LexicalAnalyser;
import com.projectd.interpreter.lex.LexicalAnalyserImpl;
import com.projectd.interpreter.lex.token.LexToken;

import java.util.List;

public class DInterpreterImpl implements DInterpreter {

    private static final LexicalAnalyser lexicalAnalyser = new LexicalAnalyserImpl();

    @Override
    public void interpretFromSource(List<String> sourceLines) {
        List<LexToken> lexTokens = lexicalAnalyser.analyse(sourceLines);

    }
}