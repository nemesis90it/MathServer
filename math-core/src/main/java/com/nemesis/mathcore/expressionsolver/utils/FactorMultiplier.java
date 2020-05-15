package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.utils.MathUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static java.math.BigDecimal.ONE;

public class FactorMultiplier {

    public static Function<Collection<Factor>, ? extends Factor> get(Class<? extends Factor> factorClass){
        return multiplierByType.getOrDefault(factorClass, genericMultiplier);
    }

    private static final Function<Collection<Factor>, ? extends Factor> genericMultiplier = factors ->
            factors.stream().reduce(new Constant(1), (f1, f2) -> new ParenthesizedExpression(new Term(f1, MULTIPLY, f2)));

    private static final Map<Class<? extends Component>, Function<Collection<Factor>, ? extends Factor>> multiplierByType = new HashMap<>();

    static {
        multiplierByType.put(Constant.class, constants -> {
            BinaryOperator<Factor> constantMultiplier = (c1, c2) -> {
                if (c1 instanceof Constant && c2 instanceof Constant) {
                    return new Constant(MathUtils.multiply(c1.getValue(), c2.getValue()));
                } else if (c1 instanceof Exponential c1_exponential && c2 instanceof Exponential c2_exponential
                        && c1_exponential.getExponent().isScalar() && c2_exponential.getExponent().isScalar()) {
                    return new Exponential(c1_exponential.getBase(), new Constant(MathUtils.add(c1_exponential.getExponent().getValue(), c2_exponential.getExponent().getValue())));
                } else if (c1 instanceof Exponential exponential && exponential.getExponent() instanceof Constant exponent) {
                    return new Exponential(exponential.getBase(), new Constant(MathUtils.add(exponent.getValue(), ONE)));
                } else if (c2 instanceof Exponential exponential && exponential.getExponent() instanceof Constant exponent) {
                    return new Exponential(exponential.getBase(), new Constant(MathUtils.add(exponent.getValue(), ONE)));
                } else {
                    return Factor.getFactor(new Term(c1, MULTIPLY, c2));
                }
            };
            Constant identity = new Constant(ONE);
            return constants.stream().reduce(identity, constantMultiplier);
        });

        multiplierByType.put(Logarithm.class, logarithms -> {
            BinaryOperator<Factor> logarithmMultiplier = (l1, l2) -> {
                if (l1 instanceof Logarithm log_1 && l2 instanceof Logarithm) {
                    return new Exponential(log_1, new Constant(2));
                } else if (l1 instanceof Exponential l1_exponential && l2 instanceof Exponential l2_exponential
                        && l1_exponential.getExponent().isScalar() && l2_exponential.getExponent().isScalar()) {
                    return new Exponential(l1_exponential.getBase(), new Constant(MathUtils.add(l1_exponential.getExponent().getValue(), l2_exponential.getExponent().getValue())));
                } else if (l1 instanceof Exponential exponential && exponential.getExponent() instanceof Constant exponent) {
                    return new Exponential(exponential.getBase(), new Constant(MathUtils.add(exponent.getValue(), ONE)));
                } else if (l2 instanceof Exponential exponential && exponential.getExponent() instanceof Constant exponent) {
                    return new Exponential(exponential.getBase(), new Constant(MathUtils.add(exponent.getValue(), ONE)));
                } else {
                    return Factor.getFactor(new Term(l1, MULTIPLY, l2));
                }
            };

            Logarithm anyLogOfProvided;

            final Factor factor = logarithms.stream().findFirst().get();

            if (factor instanceof Logarithm log) {
                anyLogOfProvided = log;
            } else if (factor instanceof Exponential exp) {
                anyLogOfProvided = (Logarithm) exp.getBase();
            } else {
                throw new RuntimeException("Logarithm or Exponential expected, found [" + factor.getClass() + "]");
            }

            Exponential identity = new Exponential(anyLogOfProvided, new Constant(0));
            return logarithms.stream().reduce(identity, logarithmMultiplier);

        });

        // TODO add more types
    }

}
