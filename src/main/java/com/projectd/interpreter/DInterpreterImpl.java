package com.projectd.interpreter;

import com.projectd.interpreter.lex.LexicalAnalyser;
import com.projectd.interpreter.lex.LexicalAnalyserImpl;
import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.runtime.RuntimeExecutorImpl;
import com.projectd.interpreter.runtime.contract.RuntimeExecutor;
import com.projectd.interpreter.syntax.SyntaxAnalyser;
import com.projectd.interpreter.syntax.SyntaxAnalyserImpl;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.List;

public class DInterpreterImpl implements DInterpreter {

    private static final LexicalAnalyser lexicalAnalyser = new LexicalAnalyserImpl();
    private static final RuntimeExecutor executor = new RuntimeExecutorImpl();

    @Override
    public void interpretFromSource(List<String> sourceLines) {
        List<LexToken> lexTokens = lexicalAnalyser.analyse(sourceLines);

        SyntaxAnalyser syntaxAnalyser = new SyntaxAnalyserImpl(lexTokens);
        AstNode syntaxTree = syntaxAnalyser.buildAstTree();

        executor.execute(syntaxTree);
    }
}