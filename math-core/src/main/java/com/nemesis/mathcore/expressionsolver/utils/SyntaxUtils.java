package com.nemesis.mathcore.expressionsolver.utils;

import java.math.BigDecimal;

public class SyntaxUtils {


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
            return -1;
        } else {
            return --currentIndex;
        }
    }

    public static BigDecimal removeNonSignificantZeros(BigDecimal value) {
        if (value.toString().contains(".")) {
            char[] chars = value.toString().toCharArray();
            int i = chars.length - 1;
            while (chars[i] == '0') {
                i--;
            }
            if (chars[i] == '.') {
                i--;
            }
            return new BigDecimal(new String(chars).substring(0, i + 1));
        } else if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return value;
        }
    }

}
