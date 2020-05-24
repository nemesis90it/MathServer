package com.nemesis.mathcore.expressionsolver;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.*;

public class LatexBuilder {

    private static final String INDETERMINATE = "indeterminate";
    private static final String INFINITY = "∞";
    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String MINUS = "-";

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
        return a + "^" + b;
    }

    public static String division(String a, String b) {
        if (isZero(a)) {
            if (isZero(b)) {
                return INDETERMINATE;
            }
            return ZERO;
        }
        if (isZero(b)) {
            return INFINITY;
        }
        if (isOne(b)) {
            return a;
        }
        return "\\frac{" + a + "}{" + b + "}";
    }

    public static String product(String a, String b) {
        if (isZero(a) || isZero(b)) {
            return ZERO;
        }
        if (isOne(a)) {
            return b;
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
        if (b.matches("^[a-z].*") || b.startsWith("(") || a.endsWith(")")
                || b.startsWith(String.valueOf(PI_CHAR)) || b.startsWith(String.valueOf(E_CHAR))) {
            return a + b;
        }
        return a + "\\times " + b;
    }

    public static String difference(String a, String b) {
        if (isZero(a)) {
            if (isZero(b)) {
                return ZERO;
            }
            if (b.startsWith(MINUS)) {
                return b;
            }
            return MINUS + b;
        }
        if (isZero(b)) {
            return a;
        }
        if (b.startsWith(MINUS)) {
            return a + "+" + b;
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
        return a + "+" + b;
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