package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.LexIdentifierToken;
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
                    case ' ':
                        continue;
                    case ';':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.SEMICOLON));
                        break;
                    case '>':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.MORE_OR_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.MORE));
                        }
                        break;
                    case '<':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LESS_OR_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LESS));
                        }
                        break;
                    case '=':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.EQUAL));
                        break;
                    case '/':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.NOT_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.DIVISION));
                        }
                        break;
                    case '*':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.MULTIPLICATION));
                        break;
                    case '+':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.PLUS_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.ADDITION));
                        }
                        break;
                    case '-':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.MINUS_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.SUBTRACTION));
                        }
                        break;
                    case ':':
                        if (sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.ASSIGNMENT));
                            j += 1;
                        } else {
                            System.out.println("Not valid token");
                        }
                        break;
                    case '{':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.OPEN_CURLY_BRACKET));
                        break;
                    case '}':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.CLOSED_CURLY_BRACKET));
                        break;
                    case '[':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.OPEN_SQUARE_BRACKET));
                        break;
                    case ']':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.CLOSE_SQUARE_BRACKET));
                        break;
                    case ',':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.COMMA));
                        break;
                    case '\"':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.QUOTES));
                        break;
                    case '.':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.DOT));
                        break;
                    case '(':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.OPEN_ROUND_BRACKET));
                        break;
                    case ')':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.CLOSED_ROUND_BRACKET));
                        break;
                    case 'r':
                        if (sourceLines.get(i).charAt(j + 1) == 'e' && sourceLines.get(i).charAt(j + 2) == 'a'
                                && sourceLines.get(i).charAt(j + 3) == 'l' && sourceLines.get(i).charAt(j + 4) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.REAL));
                            j += 3;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'e' && sourceLines.get(i).charAt(j + 2) == 't'
                                && sourceLines.get(i).charAt(j + 3) == 'u' && sourceLines.get(i).charAt(j + 4) == 'r'
                                && sourceLines.get(i).charAt(j + 5) == 'n' && sourceLines.get(i).charAt(j + 6) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.RETURN));
                            j += 5;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'i':
                        if (sourceLines.get(i).charAt(j + 1) == 'n' && sourceLines.get(i).charAt(j + 2) == 't'
                                && sourceLines.get(i).charAt(j + 3) == 'e' && sourceLines.get(i).charAt(j + 4) == 'g'
                                && sourceLines.get(i).charAt(j + 5) == 'e' && sourceLines.get(i).charAt(j + 6) == 'r'
                                && sourceLines.get(i).charAt(j + 7) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.INTEGER));
                            j += 6;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'f' && sourceLines.get(i).charAt(j + 2) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.IF));
                            j += 1;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'n' && sourceLines.get(i).charAt(j + 2) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.IN));
                            j += 1;
                        } else if (sourceLines.get(i).charAt(j + 1) == 's' && sourceLines.get(i).charAt(j + 2) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.IS));
                            j += 1;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'n' && sourceLines.get(i).charAt(j + 2) == 'p'
                                && sourceLines.get(i).charAt(j + 3) == 'u' && sourceLines.get(i).charAt(j + 4) == 't'
                                && sourceLines.get(i).charAt(j + 5) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.INPUT));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'b':
                        if (sourceLines.get(i).charAt(j + 1) == 'o' && sourceLines.get(i).charAt(j + 2) == 'o'
                                && sourceLines.get(i).charAt(j + 3) == 'l' && sourceLines.get(i).charAt(j + 4) == 'e'
                                && sourceLines.get(i).charAt(j + 5) == 'a' && sourceLines.get(i).charAt(j + 6) == 'n'
                                && sourceLines.get(i).charAt(j + 7) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.BOOLEAN));
                            j += 6;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 's':
                        if (sourceLines.get(i).charAt(j + 1) == 't' && sourceLines.get(i).charAt(j + 2) == 'r'
                                && sourceLines.get(i).charAt(j + 3) == 'i' && sourceLines.get(i).charAt(j + 4) == 'n'
                                && sourceLines.get(i).charAt(j + 5) == 'g' && sourceLines.get(i).charAt(j + 6) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.STRING));
                            j += 5;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 't':
                        if (sourceLines.get(i).charAt(j + 1) == 'h' && sourceLines.get(i).charAt(j + 2) == 'e'
                                && sourceLines.get(i).charAt(j + 3) == 'n' && sourceLines.get(i).charAt(j + 4) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.THEN));
                            j += 3;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'r' && sourceLines.get(i).charAt(j + 2) == 'u'
                                && sourceLines.get(i).charAt(j + 3) == 'e' && sourceLines.get(i).charAt(j + 4) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.TRUE));
                            j += 3;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'e':
                        if (sourceLines.get(i).charAt(j + 1) == 'l' && sourceLines.get(i).charAt(j + 2) == 's'
                                && sourceLines.get(i).charAt(j + 3) == 'e' && sourceLines.get(i).charAt(j + 4) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.ELSE));
                            j += 3;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'n' && sourceLines.get(i).charAt(j + 2) == 'd'
                                && sourceLines.get(i).charAt(j + 3) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.END));
                            j += 2;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'm' && sourceLines.get(i).charAt(j + 2) == 'p'
                                && sourceLines.get(i).charAt(j + 3) == 't' && sourceLines.get(i).charAt(j + 4) == 'y'
                                && sourceLines.get(i).charAt(j + 5) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.EMPTY));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'w':
                        if (sourceLines.get(i).charAt(j + 1) == 'h' && sourceLines.get(i).charAt(j + 2) == 'i'
                                && sourceLines.get(i).charAt(j + 3) == 'l' && sourceLines.get(i).charAt(j + 4) == 'e'
                                && sourceLines.get(i).charAt(j + 5) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.WHILE));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'v':
                        if (sourceLines.get(i).charAt(j + 1) == 'a' && sourceLines.get(i).charAt(j + 2) == 'r'
                                && sourceLines.get(i).charAt(j + 3) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.VAR));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'l':
                        if (sourceLines.get(i).charAt(j + 1) == 'o' && sourceLines.get(i).charAt(j + 2) == 'o'
                                && sourceLines.get(i).charAt(j + 3) == 'p' && sourceLines.get(i).charAt(j + 4) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LOOP));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'f':
                        if (sourceLines.get(i).charAt(j + 1) == 'o' && sourceLines.get(i).charAt(j + 2) == 'r'
                                && sourceLines.get(i).charAt(j + 3) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.FOR));
                            j += 2;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'u' && sourceLines.get(i).charAt(j + 2) == 'n'
                                && sourceLines.get(i).charAt(j + 3) == 'c' && sourceLines.get(i).charAt(j + 4) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.FUNC));
                            j += 3;
                        } else if (sourceLines.get(i).charAt(j + 1) == 'a' && sourceLines.get(i).charAt(j + 2) == 'l'
                                && sourceLines.get(i).charAt(j + 3) == 's' && sourceLines.get(i).charAt(j + 4) == 'e'
                                && sourceLines.get(i).charAt(j + 5) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.FALSE));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'p':
                        if (sourceLines.get(i).charAt(j + 1) == 'r' && sourceLines.get(i).charAt(j + 2) == 'i'
                                && sourceLines.get(i).charAt(j + 3) == 'n' && sourceLines.get(i).charAt(j + 4) == 't'
                                && sourceLines.get(i).charAt(j + 5) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.PRINT));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'o':
                        if (sourceLines.get(i).charAt(j + 1) == 'r' && sourceLines.get(i).charAt(j + 2) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.OR));
                            j += 1;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'a':
                        if (sourceLines.get(i).charAt(j + 1) == 'n' && sourceLines.get(i).charAt(j + 2) == 'd'
                                && sourceLines.get(i).charAt(j + 3) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.AND));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'x':
                        if (sourceLines.get(i).charAt(j + 1) == 'o' && sourceLines.get(i).charAt(j + 2) == 'r'
                                && sourceLines.get(i).charAt(j + 3) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.XOR));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'n':
                        if (sourceLines.get(i).charAt(j + 1) == 'o' && sourceLines.get(i).charAt(j + 2) == 't'
                                && sourceLines.get(i).charAt(j + 3) == ' ') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.NOT));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
//                    case '1':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '2':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '3':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '4':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '5':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '6':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '7':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '8':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '9':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
//                    case '0':
//                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LITERAL));
//                        break;
                    default:
                        LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                        lexTokenList.add(lexIdentifierToken);
                        j += lexIdentifierToken.getIdentifier().length() - 1;
                        break;
                }
            }
        }
        return lexTokenList;
    }

    public LexIdentifierToken buildIdentifier(int i, int j, List<String> sourceLines) {
        StringBuilder identifier = new StringBuilder();
        int counter = 0;
        while (sourceLines.get(i).charAt(j) != ' ' && sourceLines.get(i).charAt(j) != '\"'
                && sourceLines.get(i).charAt(j) != ';') {
            identifier.append(sourceLines.get(i).charAt(j));
            counter++;
            j++;
        }
        return new LexIdentifierToken(identifier.toString(), LexTokenSpan.of(i, j - counter));
    }
}