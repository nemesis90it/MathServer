package com.nemesis.mathcore.expressionsolver.utils;

import java.math.BigDecimal;

public class SyntaxUtils {

    public static final String DECIMAL_NUM_REGEX = "^[0-9]+\\.[0-9]+$";
    public static final String GENERIC_NUM_REGEX = "^[0-9]+(\\.[0-9]+)?$";

    public static boolean isValidSymbolForFactor(String term, int pos) {
        return isDigit(term.charAt(pos)) ||
                isPoint(term, pos) ||
                isExclamationMark(term, pos) ||
                isMinus(term, pos) ||
                isCaret(term, pos);
    }

    private static boolean isCaret(String term, int pos) {
        return pos < term.length() && term.charAt(pos) == '^';
    }

    private static boolean isMinus(String term, int pos) {
        return pos < term.length() && term.charAt(pos) == '-';
    }

    public static boolean isDigit(char c) {
        return (c > 47 && c < 58);
    }

    public static boolean isPoint(String term, int i) {
        return i < term.length() && term.charAt(i) == '.';
    }

    static boolean isExclamationMark(String term, int i) {
        return i < term.length() && term.charAt(i) == '!';
    }

    public static boolean isSyntaxValidForFactorial(String factorial) {
        return factorial.matches("[0-9]+(\\.0+)?!");
    }

    public static boolean isDecimalNumber(String s) {
        return s.matches(DECIMAL_NUM_REGEX);
    }

    public static boolean isNumber(String s) {
        return s.matches(GENERIC_NUM_REGEX);
    }

    public static void checkParenthesis(String expression) {
        int i = expression.indexOf('(');
        if (i != -1) {
            i++;
            int openedPar = 1;
            int closedPar = 0;
            while (i < expression.length()) {
                if (expression.charAt(i) == ')') {
                    closedPar++;
                } else if (expression.charAt(i) == '(') {
                    openedPar++;
                }
                if (closedPar > openedPar) {
                    String errorMsg = "Invalid expression [" + expression + "]: closed parenthesis at index [" + i + "] without opened.";
                    throw new IllegalArgumentException(errorMsg);
                }
                i++;
            }

            if (closedPar != openedPar) {
                String errorMsg = "Invalid expression '" + expression + "': parenthesis must be pairs.";
                throw new IllegalArgumentException(errorMsg);
            }
        }
    }

    public static int getClosedParenthesisIndex(String expression, Integer openParIndex) {

        int currentIndex = (openParIndex == null ? expression.indexOf('(') : openParIndex) + 1;
        int openedPar = 1;
        int closedPar = 0;
        while (currentIndex < expression.length() && closedPar != openedPar) {
            if (expression.charAt(currentIndex) == ')') {
                closedPar++;
            } else if (expression.charAt(currentIndex) == '(') {
                openedPar++;
            }
            currentIndex++;
        }

        if (currentIndex == expression.length() && closedPar != openedPar) {
            String errorMsg = "Invalid expression '" + expression + "': parenthesis must be pairs.";
            throw new IllegalArgumentException(errorMsg);
        } else {
            return --currentIndex;
        }
    }

    public static BigDecimal removeNonSignificantZeros(BigDecimal rawResult) {
        if (rawResult.toString().contains(".")) {
            char[] chars = rawResult.toString().toCharArray();
            int i = chars.length - 1;
            while (chars[i] == '0') {
                i--;
            }
            if (chars[i] == '.') {
                i--;
            }
            return new BigDecimal(new String(chars).substring(0, i + 1));
        } else {
            return rawResult;
        }
    }

    public static int getClosedPipeIndex(String expression, Integer openPipeIndex) {
        // TODO
        throw new UnsupportedOperationException("Abs value is not supported yet");
//        int currentIndex = (openPipeIndex == null ? expression.indexOf('|') : openPipeIndex) + 1;
//        int openedPipe = 1;
//        int closedPar = 0;
//        while (currentIndex < expression.length() && closedPar != openedPipe) {
//            if (expression.charAt(currentIndex) == ')') {
//                closedPar++;
//            } else if (expression.charAt(currentIndex) == '(') {
//                openedPipe++;
//            }
//            currentIndex++;
//        }
//
//        if (currentIndex == expression.length() && closedPar != openedPipe) {
//            String errorMsg = "Invalid expression '" + expression + "': parenthesis must be pairs.";
//            throw new IllegalArgumentException(errorMsg);
//        } else {
//            return --currentIndex;
//        }
    }
}
