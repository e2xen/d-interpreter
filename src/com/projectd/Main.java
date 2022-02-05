package com.projectd;

import com.projectd.interpreter.DInterpreter;
import com.projectd.interpreter.DInterpreterImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    private static final DInterpreter interpreter = new DInterpreterImpl();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Specify exactly one file of source code");
        }

        String sourcePath = args[0];
        List<String> sourceCodeLines;
        try {
            Path path = Paths.get(sourcePath);
            sourceCodeLines = Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println(e.toString());
            return;
        }

        interpreter.interpretFromSource(sourceCodeLines);
    }
}
