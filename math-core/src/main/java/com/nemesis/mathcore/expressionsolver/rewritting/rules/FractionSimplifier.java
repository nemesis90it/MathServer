package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Constant;
import com.nemesis.mathcore.expressionsolver.expression.components.Fraction;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.Predicate;

public class FractionSimplifier implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component -> component instanceof Fraction
                && !(((Fraction) component).getNumerator() instanceof Fraction)
                && !(((Fraction) component).getDenominator() instanceof Fraction);
    }

    @Override
    public Function<Component, Fraction> transformer() {

        return component -> {
            Fraction f = (Fraction) component;
            BigDecimal numeratorValue = f.getNumerator().getValue();
            BigDecimal denominatorValue = f.getDenominator().getValue();
            if (MathUtils.isIntegerValue(numeratorValue) && MathUtils.isIntegerValue(denominatorValue)) {
                BigInteger intNumerator = numeratorValue.toBigIntegerExact();
                BigInteger intDenominator = denominatorValue.toBigIntegerExact();
                BigInteger gcd = intNumerator.gcd(intDenominator);
                f.setNumerator(new Constant(intNumerator.divide(gcd)));
                f.setDenominator(new Constant(intDenominator.divide(gcd)));
            }
            return f;
        };
    }
}
