package com.nemesis.mathcore.expressionsolver.stringbuilder;

import com.nemesis.mathcore.expressionsolver.operators.Sign;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ExpressionBuilder {

    public static String power(String a, String b) {
        if (isZero(a)) {
            if (isZero(b)) {
                return INDETERMINATE;
            }
            return ZERO;
        }
        if (isZero(b)) {
            return ONE;
        }
        if (isOne(a)) {
            return ONE;
        }
        if (isOne(b)) {
            return a;
        }
        if (isEmpty(b)) {
            return a;
        }
        if (isEmpty(a)) {
            throw new IllegalArgumentException("Base cannot be empty string");
        }
        return a + "^" + b;
    }

    public static String toParenthesized(String string) {
        if (string.startsWith("|") && string.endsWith("|")) {
            return string;
        }
        if (string.startsWith("(") && string.endsWith(")")) {
            return string;
        }
        return "(" + string + ")";
    }

    public static String toAbsExpression(String string) {
        if (string.startsWith("|") && string.endsWith("|")) {
            return string;
        }
        if (string.startsWith("(") && string.endsWith(")")) {
            return "|" + string.substring(1, string.length() - 1) + "|";
        }
        return "|" + string + "|";
    }

    public static String division(String a, String b) {
        if (isZero(a)) {
            if (isZero(b)) {
                return INDETERMINATE;
            }
            return ZERO;
        }
        if (isZero(b)) {
            return String.valueOf(INFINITY);
        }
        if (isOne(b)) {
            return a;
        }
        if (isEmpty(b)) {
            return a;
        }
        if (isEmpty(a)) {
            throw new IllegalArgumentException("Base cannot be empty string");
        }
        return a + "/" + b;
    }

    public static String product(String a, String b) {
        if (isZero(a) || isZero(b)) {
            return ZERO;
        }
        if (isOne(a)) {
            if (isEmpty(b)) {
                return a;
            } else {
                return b;
            }
        }
        if (isOne(b)) {
            return a;
        }
        if (isMinusOne(a)) {
            if (b.startsWith(MINUS)) {
                return b.substring(1);
            } else {
                return MINUS + b;
            }
        }
        if (isMinusOne(b)) {
            if (a.startsWith(MINUS)) {
                return a.substring(1);
            } else {
                return MINUS + a;
            }
        }
        if (a.matches(".*[a-z]$") || b.matches("^[a-z].*") || b.startsWith("(") || b.startsWith("|") || a.endsWith(")")
                || b.startsWith(String.valueOf(PI_CHAR)) || b.startsWith(String.valueOf(E_CHAR))) {
            return a + b;
        }
        if (isEmpty(b)) {
            return a;
        }
        if (isEmpty(a)) {
            return b;
        }
        return a + "*" + b;
    }

    public static String difference(String a, String b) {
        if (isZero(a)) {
            return negateString(b);
        }
        if (isZero(b)) {
            return a;
        }
        if (b.startsWith(MINUS)) {
            return a + PLUS + b;
        }
        if (isEmpty(b)) {
            return a;
        }
        if (isEmpty(a)) {
            return negateString(b);
        }
        return a + MINUS + b;
    }

    public static String sum(String a, String b) {
        if (isZero(a)) {
            if (isZero(b)) {
                return ZERO;
            }
            return b;
        }
        if (isZero(b)) {
            return a;
        }
        if (b.startsWith(MINUS)) {
            return a + b;
        }
        if (b.startsWith(PLUS)) {
            return sum(a, b.substring(1));
        }
        if (isEmpty(b)) {
            return a;
        }
        if (isEmpty(a)) {
            return b;
        }
        return a + PLUS + b;
    }

    public static String naturalLogarithm(Sign sign, String argument) {
        final String logarithm = "ln(%s)".formatted(argument);
        if (sign.equals(Sign.MINUS)) {
            return MINUS + logarithm;
        } else {
            return logarithm;
        }
    }

    public static String logarithm(Sign sign, BigDecimal base, String argument) {
        final String logarithm = "log(%s,%s)".formatted(base, argument);
        if (sign.equals(Sign.MINUS)) {
            return MINUS + logarithm;
        } else {
            return logarithm;
        }
    }

    public static String addSign(String sign, String s) {
        if (sign.equals(MINUS)) {
            if (s.startsWith(MINUS)) {
                return s;
            }
            return MINUS + s;
        }
        return s;
    }

    private static String negateString(String b) {
        if (isZero(b)) {
            return ZERO;
        }
        if (b.startsWith(MINUS)) {
            return b;
        }
        return MINUS + b;
    }

    private static boolean isZero(String s) {
        return s.matches(IS_ZERO_REGEXP);
    }

    private static boolean isOne(String s) {
        return s.matches(IS_ONE_REGEXP);
    }

    private static boolean isMinusOne(String s) {
        return s.matches(IS_MINUS_ONE_REGEXP);
    }
}