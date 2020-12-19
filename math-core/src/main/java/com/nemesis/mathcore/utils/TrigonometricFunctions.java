package com.nemesis.mathcore.utils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.MATH_CONTEXT;

// TODO calculate functions as BigDecimal (not as "double")

public class TrigonometricFunctions {

    public static BigDecimal sin(BigDecimal arg) {
        return BigDecimal.valueOf(Math.sin(arg.doubleValue()));
    }

    public static BigDecimal cos(BigDecimal arg) {
        return BigDecimal.valueOf(Math.cos(arg.doubleValue()));
    }

    public static BigDecimal sec(BigDecimal arg) {
        return BigDecimal.ONE.divide(cos(arg), MATH_CONTEXT);
    }

    public static BigDecimal tan(BigDecimal arg) {
        return sin(arg).divide(cos(arg), MATH_CONTEXT);
    }

    public static BigDecimal tg(BigDecimal arg) {
        return tan(arg);
    }

    public static BigDecimal cotan(BigDecimal arg) {
        return cos(arg).divide(sin(arg), MATH_CONTEXT);
    }

    public static BigDecimal cot(BigDecimal arg) {
        return cotan(arg);
    }

    public static BigDecimal cotg(BigDecimal arg) {
        return cotan(arg);
    }

    public static BigDecimal ctg(BigDecimal arg) {
        return cotan(arg);
    }

    public static BigDecimal cosec(BigDecimal arg) {
        return BigDecimal.ONE.divide(sin(arg), MATH_CONTEXT);
    }

    public static BigDecimal csc(BigDecimal arg) {
        return cosec(arg);
    }

    public static BigDecimal arcsin(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arccos(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arcsec(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arctan(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arctg(BigDecimal arg) {
        return arctan(arg);
    }

    public static BigDecimal arccotan(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arccot(BigDecimal arg) {
        return arccotan(arg);
    }

    public static BigDecimal arccotg(BigDecimal arg) {
        return arccot(arg);
    }

    public static BigDecimal arcctg(BigDecimal arg) {
        return arccot(arg);
    }

    public static BigDecimal arccosec(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arccsc(BigDecimal arg) {
        return arccosec(arg);
    }

    public static BigDecimal sinh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal cosh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal sech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal tanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal tgh(BigDecimal arg) {
        return tanh(arg);
    }

    public static BigDecimal cotanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal coth(BigDecimal arg) {
        return cotanh(arg);
    }

    public static BigDecimal cotgh(BigDecimal arg) {
        return cotanh(arg);
    }

    public static BigDecimal ctgh(BigDecimal arg) {
        return cotanh(arg);
    }

    public static BigDecimal cosech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal csch(BigDecimal arg) {
        return cosech(arg);
    }

    public static BigDecimal arsinh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arcosh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arsech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal artanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal artgh(BigDecimal arg) {
        return artanh(arg);
    }

    public static BigDecimal arcotanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arcoth(BigDecimal arg) {
        return arcotanh(arg);
    }

    public static BigDecimal arcotgh(BigDecimal arg) {
        return arcotanh(arg);
    }

    public static BigDecimal arctgh(BigDecimal arg) {
        return arcotanh(arg);
    }

    public static BigDecimal arcosech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static BigDecimal arcsch(BigDecimal arg) {
        return arcosech(arg);
    }


}
