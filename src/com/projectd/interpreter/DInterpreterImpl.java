package com.projectd.interpreter;

import com.projectd.interpreter.lex.LexToken;
import com.projectd.interpreter.lex.LexicalAnalyser;
import com.projectd.interpreter.lex.LexicalAnalyserImpl;

import java.util.List;

public class DInterpreterImpl implements DInterpreter {

    private static final LexicalAnalyser lexicalAnalyser = new LexicalAnalyserImpl();

    @Override
    public void interpretFromSource(String source) {
        List<LexToken> lexTokens = lexicalAnalyser.analyse(source);

    }
}
