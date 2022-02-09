package com.projectd;

import com.projectd.interpreter.DInterpreter;
import com.projectd.interpreter.DInterpreterImpl;
import com.projectd.interpreter.lex.token.LexLiteralToken;
import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.lex.token.LexTokenSpan;
import com.projectd.interpreter.syntax.SyntaxAnalyserImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final DInterpreter interpreter = new DInterpreterImpl();

    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.out.println("Specify exactly one file of source code");
//        }
//
//        String sourcePath = args[0];
//        List<String> sourceCodeLines;
//        try {
//            Path path = Paths.get(sourcePath);
//            sourceCodeLines = Files.readAllLines(path);
//        } catch (IOException e) {
//            System.out.println(e.toString());
//            return;
//        }

//        List<String> sourceCodeLines;
//        Path path = Paths.get("C:\\Users\\79374\\IdeaProjects\\d-interpreter\\src\\com\\projectd\\test.txt");
//        sourceCodeLines = Files.readAllLines(path);
//
//        interpreter.interpretFromSource(sourceCodeLines);

        LexTokenSpan span = LexTokenSpan.of(0, 0);

        List<LexToken> lex = new ArrayList<LexToken>();
        lex.add(LexLiteralToken.ofValue(1, span));
        SyntaxAnalyserImpl s = new SyntaxAnalyserImpl(lex);
        s.buildAstTree();
    }
}
