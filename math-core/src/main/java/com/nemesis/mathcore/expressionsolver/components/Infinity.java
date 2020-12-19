package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.utils.Constants;

import java.math.BigDecimal;

public class Infinity extends Constant {

    public Infinity() {
        super();
    }


    public Infinity(Sign sign) {
        super.sign = sign;
    }

    @Override
    public BigDecimal getValue() {
        throw new NoValueException("Infinity has no value");
    }

    @Override
    public Boolean isScalar() {
        return false;
    }


    @Override
    public String toString() {
        String signChar = sign == Sign.PLUS ? "" : "-";
        return signChar + Constants.INFINITY;
    }

    @Override
    public int compareTo(Component c) {
        return sign == Sign.PLUS ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toLatex() {
        String signChar = sign == Sign.PLUS ? "" : "-";
        return signChar + Constants.INFINITY;
    }
}
