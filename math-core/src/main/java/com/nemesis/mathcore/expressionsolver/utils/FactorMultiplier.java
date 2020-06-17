package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.utils.MathUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static java.math.BigDecimal.ONE;

public class FactorMultiplier {

    public static Function<Collection<Factor>, ? extends Factor> get(Class<? extends Factor> factorClass) {
        return multiplierByType.getOrDefault(factorClass, genericMultiplier);
    }

    private static final Function<Collection<Factor>, ? extends Factor> genericMultiplier = factors ->
            factors.stream().reduce(new Constant(1), (f1, f2) -> new ParenthesizedExpression(new Term(f1, MULTIPLY, f2)));

    private static final Map<Class<? extends Component>, Function<Collection<Factor>, ? extends Factor>> multiplierByType = new HashMap<>();

    static {
        multiplierByType.put(Constant.class, constants -> {
            BinaryOperator<Factor> constantMultiplier = (c1, c2) -> new Constant(MathUtils.multiply(c1.getValue(), c2.getValue()));
            Constant identity = new Constant(ONE);
            return constants.stream().reduce(identity, constantMultiplier);
        });

        multiplierByType.put(Logarithm.class, logarithms -> {
            BinaryOperator<Factor> logarithmMultiplier = (l1, l2) -> {

                final Exponential l1_exponential = Exponential.getExponential(l1);
                final Exponential l2_exponential = Exponential.getExponential(l2);

                Sign sign = l1_exponential.getSign() == l2_exponential.getSign() ? Sign.PLUS : Sign.MINUS;

                if (Objects.equals(l1_exponential.getBase(), l2_exponential.getBase())) {
                    if (l1_exponential.getExponent().isScalar() && l2_exponential.getExponent().isScalar()) {
                        return new Exponential(sign, l1_exponential.getBase(), new Constant(MathUtils.add(l1_exponential.getExponent().getValue(), l2_exponential.getExponent().getValue())));
                    } else {
                        return new Exponential(sign, l1_exponential.getBase(), new ParenthesizedExpression(Term.getTerm(l1_exponential.getExponent()), SUM, Term.getTerm(l2_exponential.getExponent())));
                    }
                } else {
                    return Factor.getFactor(sign, new Term(l1, MULTIPLY, l2));
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
