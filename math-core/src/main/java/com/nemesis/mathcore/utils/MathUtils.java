package com.nemesis.mathcore.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.*;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;


public class MathUtils {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final BigDecimal ERROR = divide(ONE, new BigDecimal(10).pow(SCALE));

    public static boolean isIntegerValue(BigDecimal bd) {
        return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0;
    }

    public static BigInteger factorial(BigInteger n) {
        BigInteger result = BigInteger.ONE;

        while (!n.equals(BigInteger.ZERO)) {
            result = result.multiply(n);
            n = n.subtract(BigInteger.ONE);
        }

        return result;
    }

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, SCALE, ROUNDING_MODE);
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b, MATH_CONTEXT);
    }

    public static BigDecimal add(BigDecimal a, BigDecimal term) {
        return a.add(term).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal pow(BigDecimal b, Integer e) {
        return b.pow(e, MATH_CONTEXT);
    }

    public static BigDecimal binomialCoefficient(Integer n, Integer k) {

        BigDecimal n_factorial = new BigDecimal(factorial(BigInteger.valueOf(n)));
        BigDecimal k_factorial = new BigDecimal(factorial(BigInteger.valueOf(k)));
        BigDecimal n_k_factorial = new BigDecimal(factorial(BigInteger.valueOf(n - k)));

        return divide(n_factorial, multiply(k_factorial, n_k_factorial));
    }

    public static BigDecimal bernoulliNumber(Integer m) {

        if (m < 0) {
            throw new IllegalArgumentException("Input must be positive");
        }

        if (m == 0) {
            return BigDecimal.ONE;
        }

        Integer j = 0;
        Integer mPlusOne = m + 1;
        Integer mMinusOne = m - 1;
        BigDecimal result = BigDecimal.ZERO;

        do {
            BigDecimal term = multiply(binomialCoefficient(mPlusOne, j), bernoulliNumber(j));
            result = add(result, term);
            j++;
        } while (j.compareTo(mMinusOne) <= 0);

        return multiply(
                divide(MINUS_ONE_DECIMAL, new BigDecimal(mPlusOne)),
                result
        );
    }

    // ⅇ^x
    public static BigDecimal exponential(BigDecimal x) {

        Integer n = 0;
        BigDecimal result = ZERO;
        BigDecimal term;

        do {
            term = divide(x.pow(n), (new BigDecimal(MathUtils.factorial(BigInteger.valueOf(n)))));
            result = add(result, term);
            n++;
        } while (term.compareTo(ERROR) > 0);

        return result;
    }

    // TODO: find faster solution!
    public static BigDecimal ln(BigDecimal x) {
        Integer n = 0;
        BigDecimal result = ZERO;
        BigDecimal term;

        do {
            BigDecimal xPow2 = pow(x, 2);
            BigDecimal a = xPow2.subtract(ONE);
            BigDecimal b = xPow2.add(ONE);
            BigDecimal c = divide(a, b);
            BigDecimal d = pow(c, 2 * n + 1);
            BigDecimal e = divide(ONE, new BigDecimal(2 * n + 1));
            term = multiply(e, d);
            result = add(result, term);
            n++;
        } while (term.abs().compareTo(ERROR) > 0);

        return result;
    }


    public static BigDecimal sin(BigDecimal x) {

        Integer n = 0;
        BigDecimal result = ZERO;
        BigDecimal term;

        do {
            term = multiply(
                    divide(
                            pow(MINUS_ONE_DECIMAL, n),
                            new BigDecimal(factorial(BigInteger.valueOf(2 * n + 1)))),
                    x.pow(2 * n + 1)
            );

            result = add(result, term);
            n++;
        } while (term.abs().compareTo(ERROR) > 0);

        return result;
    }

    public static BigDecimal cos(BigDecimal x) {

        Integer n = 0;
        BigDecimal result = ZERO;
        BigDecimal term;

        do {
            term = multiply(
                    divide(MINUS_ONE_DECIMAL.pow(n), new BigDecimal(factorial(BigInteger.valueOf(2 * n)))),
                    (x.pow(2 * n))
            );

            result = add(result, term);
            n++;
        } while (term.abs().compareTo(ERROR) > 0);

        return result;

    }


    public static BigDecimal tan(BigDecimal x) {
        return divide(sin(x), cos(x));
    }

//    public static String decCovertTo(long n, byte b) throws PositionException{
//        if(n<1 || b<2) return "0";
//        NodePosList<Character> cifre = new NodePosList();
//        long resto = n%b;
//        char c;
//        if(resto>9) c=(char)(resto+65-10);
//        else c=(char)(resto+48);
//        cifre.addFrist(c);
//        n/=b;
//        while(n!=0){
//            resto=n%b;
//            if(resto>9) c=(char)(resto+65-10);
//            else c=(char)(resto+48);
//            cifre.addLast(c);
//            n/=b;
//        }
//        String temp = IOUtility.removeSpace(cifre.viewConcat());
//        return IOUtility.reverse(temp);
//    }
}
