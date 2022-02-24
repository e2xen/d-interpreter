package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.LexToken;

import java.util.List;

public interface LexicalAnalyser {

    List<LexToken> analyse(List<String> sourceLines);
}
