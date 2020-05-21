package com.nemesis.mathcore.utils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.MATH_CONTEXT;

// TODO calculate functions as BigDecimal (not as "double")

public class TrigonometricFunctions {

    public BigDecimal sin(BigDecimal arg) {
        return BigDecimal.valueOf(Math.sin(arg.doubleValue()));
    }

    public BigDecimal cos(BigDecimal arg) {
        return BigDecimal.valueOf(Math.cos(arg.doubleValue()));
    }

    public BigDecimal sec(BigDecimal arg) {
        return BigDecimal.ONE.divide(cos(arg), MATH_CONTEXT);
    }

    public BigDecimal tan(BigDecimal arg) {
        return sin(arg).divide(cos(arg), MATH_CONTEXT);
    }

    public BigDecimal tg(BigDecimal arg) {
        return tan(arg);
    }

    public BigDecimal cotan(BigDecimal arg) {
        return cos(arg).divide(sin(arg), MATH_CONTEXT);
    }

    public BigDecimal cot(BigDecimal arg) {
        return cotan(arg);
    }

    public BigDecimal cotg(BigDecimal arg) {
        return cotan(arg);
    }

    public BigDecimal ctg(BigDecimal arg) {
        return cotan(arg);
    }

    public BigDecimal cosec(BigDecimal arg) {
        return BigDecimal.ONE.divide(sin(arg), MATH_CONTEXT);
    }

    public BigDecimal csc(BigDecimal arg) {
        return cosec(arg);
    }

    public BigDecimal arcsin(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arccos(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arcsec(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arctan(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arctg(BigDecimal arg) {
        return arctan(arg);
    }

    public BigDecimal arccotan(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arccot(BigDecimal arg) {
        return arccotan(arg);
    }

    public BigDecimal arccotg(BigDecimal arg) {
        return arccot(arg);
    }

    public BigDecimal arcctg(BigDecimal arg) {
        return arccot(arg);
    }

    public BigDecimal arccosec(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arccsc(BigDecimal arg) {
        return arccosec(arg);
    }

    public BigDecimal sinh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal cosh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal sech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal tanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal tgh(BigDecimal arg) {
        return tanh(arg);
    }

    public BigDecimal cotanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal coth(BigDecimal arg) {
        return cotanh(arg);
    }

    public BigDecimal cotgh(BigDecimal arg) {
        return cotanh(arg);
    }

    public BigDecimal ctgh(BigDecimal arg) {
        return cotanh(arg);
    }

    public BigDecimal cosech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal csch(BigDecimal arg) {
        return cosech(arg);
    }

    public BigDecimal arsinh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arcosh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arsech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal artanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal artgh(BigDecimal arg) {
        return artanh(arg);
    }

    public BigDecimal arcotanh(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arcoth(BigDecimal arg) {
        return arcotanh(arg);
    }

    public BigDecimal arcotgh(BigDecimal arg) {
        return arcotanh(arg);
    }

    public BigDecimal arctgh(BigDecimal arg) {
        return arcotanh(arg);
    }

    public BigDecimal arcosech(BigDecimal arg) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public BigDecimal arcsch(BigDecimal arg) {
        return arcosech(arg);
    }


}
