package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.Constant;
import com.nemesis.mathcore.expressionsolver.expression.components.Factor;

public class Monomial extends Polinomial {

    private final Constant coefficient;
    private final Factor variable;
    private final Factor exponent;

    public Monomial(Constant coefficient, Factor base, Factor exponent) {
        this.coefficient = coefficient;
        this.variable = base;
        this.exponent = exponent;
    }

    public static Monomial multiply(Monomial rightMonomial, Monomial leftMonomial) {
        // TODO
        throw new UnsupportedOperationException();
    }
}