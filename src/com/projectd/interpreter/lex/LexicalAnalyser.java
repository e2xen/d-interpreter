package com.projectd.interpreter.lex;

import java.util.List;

public interface LexicalAnalyser {

    List<LexToken> analyse(String source);
}
