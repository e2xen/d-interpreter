package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.shared.exception.SyntaxExceptionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LexicalAnalyserImpl implements LexicalAnalyser {

    private static final Pattern LITERAL_TOKEN_PATTERN = Pattern.compile("^[A-Za-z0-9_]*$");
    private static final Pattern IDENTIFIER_OR_LITERAL_PATTERN = Pattern.compile("^[A-Za-z0-9_.]$");

    @Override
    public List<LexToken> analyse(List<String> sourceLines) {
        List<LexToken> lexTokenList = new ArrayList<>();
        for (int i = 0; i < sourceLines.size(); i++) {

            int sizeOfLine = sourceLines.get(i).length();
            for (int j = 0; j < sizeOfLine; j++) {
                char ch = sourceLines.get(i).charAt(j);
                switch (ch) {
                    case ' ', '\n', '\t':
                        continue;
                    case ';':
                        lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.SEMICOLON));
                        break;
                    case '>':
                        if ((j + 1) < sizeOfLine && sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.MORE_OR_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.MORE));
                        }
                        break;
                    case '<':
                        if ((j + 1) < sizeOfLine && sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LESS_OR_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LESS));
                        }
                        break;
                    case '=':
                        if ((j + 1) < sizeOfLine && sourceLines.get(i).charAt(j + 1) == '>') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LAMBDA));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.EQUAL));
                        }
                        break;
                    case '/':
                        if ((j + 1) < sizeOfLine && sourceLines.get(i).charAt(j + 1) == '=') {
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
                        if ((j + 1) < sizeOfLine && sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.PLUS_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.ADDITION));
                        }
                        break;
                    case '-':
                        if ((j + 1) < sizeOfLine && sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.MINUS_EQUAL));
                            j += 1;
                        } else {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.SUBTRACTION));
                        }
                        break;
                    case ':':
                        if ((j + 1) < sizeOfLine && sourceLines.get(i).charAt(j + 1) == '=') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.ASSIGNMENT));
                            j += 1;
                        } else {
                            throw SyntaxExceptionFactory.badIdentifier(i, j);
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
                        j++;
                        StringBuilder literal = new StringBuilder();
                        int counter = 0;
                        while (sourceLines.get(i).charAt(j) != '\"') {
                            literal.append(sourceLines.get(i).charAt(j));
                            counter++;
                            j++;
                        }
                        lexTokenList.add(LexLiteralToken.ofValue(literal.toString(), LexTokenSpan.of(i, j - counter)));
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
                        if ((j + 3) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'e'
                                && sourceLines.get(i).charAt(j + 2) == 'a'
                                && sourceLines.get(i).charAt(j + 3) == 'l'
                                && charIsSpaceOrEOF(i, j + 4, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.REAL));
                            j += 3;
                        } else if ((j + 5 < sizeOfLine)
                                && sourceLines.get(i).charAt(j + 1) == 'e'
                                && sourceLines.get(i).charAt(j + 2) == 't'
                                && sourceLines.get(i).charAt(j + 3) == 'u'
                                && sourceLines.get(i).charAt(j + 4) == 'r'
                                && sourceLines.get(i).charAt(j + 5) == 'n'
                                && charIsSpaceOrEOF(i, j + 6, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.RETURN));
                            j += 5;
                        } else if ((j + 7) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'e'
                                && sourceLines.get(i).charAt(j + 2) == 'a'
                                && sourceLines.get(i).charAt(j + 3) == 'd'
                                && sourceLines.get(i).charAt(j + 4) == 'I'
                                && sourceLines.get(i).charAt(j + 5) == 'n'
                                && sourceLines.get(i).charAt(j + 6) == 't'
                                && sourceLines.get(i).charAt(j + 7) == '(') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.READ_INT));
                            j += 6;
                        } else if ((j + 8) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'e'
                                && sourceLines.get(i).charAt(j + 2) == 'a'
                                && sourceLines.get(i).charAt(j + 3) == 'd'
                                && sourceLines.get(i).charAt(j + 4) == 'R'
                                && sourceLines.get(i).charAt(j + 5) == 'e'
                                && sourceLines.get(i).charAt(j + 6) == 'a'
                                && sourceLines.get(i).charAt(j + 7) == 'l'
                                && sourceLines.get(i).charAt(j + 8) == '(') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.READ_REAL));
                            j += 7;
                        } else if ((j + 10) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'e'
                                && sourceLines.get(i).charAt(j + 2) == 'a'
                                && sourceLines.get(i).charAt(j + 3) == 'd'
                                && sourceLines.get(i).charAt(j + 4) == 'S'
                                && sourceLines.get(i).charAt(j + 5) == 't'
                                && sourceLines.get(i).charAt(j + 6) == 'r'
                                && sourceLines.get(i).charAt(j + 7) == 'i'
                                && sourceLines.get(i).charAt(j + 8) == 'n'
                                && sourceLines.get(i).charAt(j + 9) == 'g'
                                && sourceLines.get(i).charAt(j + 10) == '(') {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.READ_STRING));
                            j += 9;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'i':
                        if ((j + 2) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'n'
                                && sourceLines.get(i).charAt(j + 2) == 't'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.INT));
                            j += 2;
                        } else if ((j + 1) < sizeOfLine
                            && sourceLines.get(i).charAt(j + 1) == 'f'
                            && charIsSpaceOrEOF(i, j + 2, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.IF));
                            j += 1;
                        } else if ((j + 1) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'n'
                                && charIsSpaceOrEOF(i, j + 2, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.IN));
                            j += 1;
                        } else if ((j + 1) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 's'
                                && charIsSpaceOrEOF(i, j + 2, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.IS));
                            j += 1;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'b':
                        if ((j + 6) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'o'
                                && sourceLines.get(i).charAt(j + 2) == 'o'
                                && sourceLines.get(i).charAt(j + 3) == 'l'
                                && sourceLines.get(i).charAt(j + 4) == 'e'
                                && sourceLines.get(i).charAt(j + 5) == 'a'
                                && sourceLines.get(i).charAt(j + 6) == 'n'
                                && charIsSpaceOrEOF(i, j + 7, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.BOOLEAN));
                            j += 6;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 's':
                        if ((j + 5) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 't'
                                && sourceLines.get(i).charAt(j + 2) == 'r'
                                && sourceLines.get(i).charAt(j + 3) == 'i'
                                && sourceLines.get(i).charAt(j + 4) == 'n'
                                && sourceLines.get(i).charAt(j + 5) == 'g'
                                && charIsSpaceOrEOF(i, j + 6, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.STRING));
                            j += 5;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 't':
                        if ((j + 3) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'h'
                                && sourceLines.get(i).charAt(j + 2) == 'e'
                                && sourceLines.get(i).charAt(j + 3) == 'n'
                                && charIsSpaceOrEOF(i, j + 4, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.THEN));
                            j += 3;
                        } else if ((j + 3) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'r'
                                && sourceLines.get(i).charAt(j + 2) == 'u'
                                && sourceLines.get(i).charAt(j + 3) == 'e'
                                && charIsSpaceOrEOF(i, j + 4, sourceLines)) {
                            lexTokenList.add(LexLiteralToken.ofValue(true, LexTokenSpan.of(i, j)));
                            j += 3;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'e':
                        if ((j + 3) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'l'
                                && sourceLines.get(i).charAt(j + 2) == 's'
                                && sourceLines.get(i).charAt(j + 3) == 'e'
                                && charIsSpaceOrEOF(i, j + 4, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.ELSE));
                            j += 3;
                        } else if ((j + 2) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'n'
                                && sourceLines.get(i).charAt(j + 2) == 'd'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.END));
                            j += 2;
                        } else if ((j + 4) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'm'
                                && sourceLines.get(i).charAt(j + 2) == 'p'
                                && sourceLines.get(i).charAt(j + 3) == 't'
                                && sourceLines.get(i).charAt(j + 4) == 'y'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.EMPTY));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'w':
                        if ((j + 4) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'h'
                                && sourceLines.get(i).charAt(j + 2) == 'i'
                                && sourceLines.get(i).charAt(j + 3) == 'l'
                                && sourceLines.get(i).charAt(j + 4) == 'e'
                                && charIsSpaceOrEOF(i, j + 5, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.WHILE));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'v':
                        if ((j + 2) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'a'
                                && sourceLines.get(i).charAt(j + 2) == 'r'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.VAR));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'l':
                        if ((j + 3) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'o'
                                && sourceLines.get(i).charAt(j + 2) == 'o'
                                && sourceLines.get(i).charAt(j + 3) == 'p'
                                && charIsSpaceOrEOF(i, j + 4, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.LOOP));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'f':
                        if ((j + 2) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'o'
                                && sourceLines.get(i).charAt(j + 2) == 'r'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.FOR));
                            j += 2;
                        } else if ((j + 3) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'u'
                                && sourceLines.get(i).charAt(j + 2) == 'n'
                                && sourceLines.get(i).charAt(j + 3) == 'c'
                                && charIsSpaceOrEOF(i, j + 4, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.FUNC));
                            j += 3;
                        } else if ((j + 4) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'a'
                                && sourceLines.get(i).charAt(j + 2) == 'l'
                                && sourceLines.get(i).charAt(j + 3) == 's'
                                && sourceLines.get(i).charAt(j + 4) == 'e'
                                && charIsSpaceOrEOF(i, j + 5, sourceLines)) {
                            lexTokenList.add(LexLiteralToken.ofValue(false, LexTokenSpan.of(i, j)));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'p':
                        if ((j + 4) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'r'
                                && sourceLines.get(i).charAt(j + 2) == 'i'
                                && sourceLines.get(i).charAt(j + 3) == 'n'
                                && sourceLines.get(i).charAt(j + 4) == 't'
                                && charIsSpaceOrEOF(i, j + 5, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.PRINT));
                            j += 4;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'o':
                        if ((j + 1) < sizeOfLine
                            && sourceLines.get(i).charAt(j + 1) == 'r'
                            && charIsSpaceOrEOF(i, j + 2, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.OR));
                            j += 1;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'a':
                        if ((j + 2) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'n'
                                && sourceLines.get(i).charAt(j + 2) == 'd'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.AND));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'x':
                        if ((j + 2) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'o'
                                && sourceLines.get(i).charAt(j + 2) == 'r'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.XOR));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    case 'n':
                        if ((j + 2) < sizeOfLine
                                && sourceLines.get(i).charAt(j + 1) == 'o'
                                && sourceLines.get(i).charAt(j + 2) == 't'
                                && charIsSpaceOrEOF(i, j + 3, sourceLines)) {
                            lexTokenList.add(new LexToken(LexTokenSpan.of(i, j), LexTokenCode.NOT));
                            j += 2;
                        } else {
                            LexIdentifierToken lexIdentifierToken = buildIdentifier(i, j, sourceLines);
                            lexTokenList.add(lexIdentifierToken);
                            j += lexIdentifierToken.getIdentifier().length() - 1;
                        }
                        break;
                    default:
                        StringBuilder identifierOrLiteralSb = new StringBuilder();
                        int count = 0;
                        while (j != sourceLines.get(i).length()
                                && IDENTIFIER_OR_LITERAL_PATTERN.matcher(String.valueOf(sourceLines.get(i).charAt(j))).matches()) {
                            identifierOrLiteralSb.append(sourceLines.get(i).charAt(j));
                            count++;
                            j++;
                        }
                        String identifierOrLiteralSt = identifierOrLiteralSb.toString();
                        if (identifierOrLiteralSt.matches("\\d+")) {
                            lexTokenList.add(LexLiteralToken.ofValue(Integer.parseInt(identifierOrLiteralSt), LexTokenSpan.of(i, j - count)));
                        } else if (identifierOrLiteralSt.matches("^\\d+(\\.\\d+)*$")) {
                            lexTokenList.add(LexLiteralToken.ofValue(Double.parseDouble(identifierOrLiteralSt), LexTokenSpan.of(i, j - count)));
                        } else if (LITERAL_TOKEN_PATTERN.matcher(identifierOrLiteralSt).matches()) {
                            lexTokenList.add(new LexIdentifierToken(identifierOrLiteralSt, LexTokenSpan.of(i, j - count)));
                        } else {
                            throw SyntaxExceptionFactory.badIdentifier(i, j);
                        }

                        j--;
                }
            }
        }
        return lexTokenList;
    }

    public LexIdentifierToken buildIdentifier(int i, int j, List<String> sourceLines) {
        StringBuilder identifier = new StringBuilder();
        int counter = 0;
        while (j != sourceLines.get(i).length()
                && LITERAL_TOKEN_PATTERN.matcher(String.valueOf(sourceLines.get(i).charAt(j))).matches()) {
            identifier.append(sourceLines.get(i).charAt(j));
            counter++;
            j++;
        }

        if (counter > 0) {
            return new LexIdentifierToken(identifier.toString(), LexTokenSpan.of(i, j - counter));
        }

        throw SyntaxExceptionFactory.badIdentifier(i, j);
    }

    public boolean charIsSpaceOrEOF(int i, int j, List<String> sourceLines) {
        int sizeOfLine = sourceLines.get(i).length();
        return ((j < sizeOfLine && sourceLines.get(i).charAt(j) == ' ') || (j == sizeOfLine));
    }

}