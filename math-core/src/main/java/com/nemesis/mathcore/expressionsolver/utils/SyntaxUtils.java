package com.nemesis.mathcore.expressionsolver.utils;

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

    public static int getIndexOfClosedParenthesis(String expression, int indexOfOpenParenthesis) {

        int indexOfClosedParenthesis = indexOfOpenParenthesis + 1;
        int openedPar = 1;
        int closedPar = 0;
        while (indexOfClosedParenthesis < expression.length() && closedPar != openedPar) {
            if (expression.charAt(indexOfClosedParenthesis) == ')') {
                closedPar++;
            } else if (expression.charAt(indexOfClosedParenthesis) == '(') {
                openedPar++;
            }
            indexOfClosedParenthesis++;
        }

        if (indexOfClosedParenthesis == expression.length() && closedPar != openedPar) {
            String errorMsg = "Invalid expression '" + expression + "': parenthesis must be pairs.";
            throw new IllegalArgumentException(errorMsg);
        } else {
            return --indexOfClosedParenthesis;
        }
    }
}
