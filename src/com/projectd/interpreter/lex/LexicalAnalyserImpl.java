package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.lex.token.LexTokenSpan;

import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyserImpl implements LexicalAnalyser {

    @Override
    public List<LexToken> analyse(List<String> sourceLines) {
        List<LexToken> lexTokenList = new ArrayList<>();
        for (int i = 0; i < sourceLines.size(); i++) {
            for (int j = 0; j < sourceLines.get(i).length(); j++) {
                char ch = sourceLines.get(i).charAt(j);
                switch (ch) {
                    // TODO добавить проверку на нулл
                    case ' ':
                        continue;
                    case ';':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.SEMICOLON));
                        break;
                    case '>':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.MORE_OR_EQUAL));
                            j += 2;
                        } else {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.MORE));
                        }
                        break;
                    case '<':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.LESS_OR_EQUAL));
                            j += 2;
                        } else {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.LESS));
                        }
                        break;
                    case '=':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.EQUAL));
                        break;
                    case '/':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.NOT_EQUAL));
                            j += 2;
                        } else {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.DIVISION));
                        }
                        break;
                    case '*':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.MULTIPLICATION));
                        break;
                    case '+':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.PLUS_EQUAL));
                            j += 2;
                        } else {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.ADDITION));
                        }
                        break;
                    case '-':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.MINUS_EQUAL));
                            j += 2;
                        } else {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.SUBSTRACTION));
                        }
                        break;
                    case ':':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.ASSIGNMENT));
                        j += 2;
                        break;
                    case '{':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.OPEN_CURLY_BRACKET));
                        break;
                    case '}':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.CLOSED_CURLY_BRACKET));
                        break;
                    case '[':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.OPEN_SQUARE_BRACKET));
                        break;
                    case ']':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.CLOSE_SQUARE_BRACKET));
                        break;
                    case ',':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.COMMA));
                        break;
                    case '\"':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.QUOTES));
                        break;
                    case '.':
                        lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.DOT));
                        break;
                    case 'v':
                        if (sourceLines.get(i).charAt(j + 1) == 'a' && sourceLines.get(i).charAt(j + 2) == 'r') {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.VAR));
                            j += 3;
                        } else {
                            lexTokenList.add(new LexToken(new LexTokenSpan(i, j), LexTokenCode.IDENTIFIER));
                            // j += до конца слова
                        }
                        break;

                }
            }
        }
        return lexTokenList;
    }

//    public Integer lengthOf(String s, int i) {
//        while (s.charAt(i) != ' ') {
//
//        }
//    }
}
