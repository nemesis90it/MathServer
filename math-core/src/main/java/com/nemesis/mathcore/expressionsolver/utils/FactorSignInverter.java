package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.operators.Sign;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;

public class FactorSignInverter {

    @SuppressWarnings("unchecked")
    public static <T extends Factor> Factor cloneAndChangeSign(T factor) {

        Sign sign = factor.getSign().equals(MINUS) ? PLUS : MINUS;
        final SignInverter<T> signInverter = (SignInverter<T>) signInverters.get(factor.getClass());

        if (signInverter == null) {
            throw new UnexpectedComponentTypeException("Unexpected factor type [" + factor.getClass() + "]");
        }

        return signInverter.negate(sign, factor);

    }

    @FunctionalInterface
    private interface SignInverter<T extends Factor> {
        Factor negate(Sign sign, T factor);
    }

    private static final Map<Class<? extends Factor>, SignInverter<? extends Factor>> signInverters = new HashMap<>();

    static {
        signInverters.put(Logarithm.class, (Sign sign, Logarithm logarithm) ->
                new Logarithm(sign, new BigDecimal(logarithm.getBase().toPlainString()), logarithm.getArgument().getClone()));

        signInverters.put(Variable.class, (Sign sign, Variable variable) ->
                new Variable(sign, variable.getName()));

        signInverters.put(Exponential.class, (Sign sign, Exponential exponential) ->
                new Exponential(sign, exponential.getBase().getClone(), exponential.getExponent().getClone()));

        signInverters.put(AbsExpression.class, (Sign sign, AbsExpression absExpression) ->
                new AbsExpression(sign, absExpression.getExpression().getClone()));

        signInverters.put(ParenthesizedExpression.class, (Sign sign, ParenthesizedExpression parenthesizedExpression) ->
                new ParenthesizedExpression(sign, parenthesizedExpression.getExpression().getClone()));

        signInverters.put(RootFunction.class, (Sign sign, RootFunction rootFunction) ->
                new RootFunction(sign, rootFunction.getRootIndex(), rootFunction.getArgument().getClone()));

        signInverters.put(Factorial.class, (Sign sign, Factorial factorial) ->
                new Factorial(sign, factorial.getArgument().getClone()));

        signInverters.put(MathUnaryFunction.class, (Sign sign, MathUnaryFunction mathUnaryFunction) ->
                new MathUnaryFunction(sign, mathUnaryFunction.getFunction(), mathUnaryFunction.getFunctionName(), mathUnaryFunction.getArgument().getClone()));

        signInverters.put(Constant.class, (Sign sign, Constant constant) -> {
            BigDecimal value = constant.getValue();
            boolean isNegative = value.compareTo(BigDecimal.ZERO) < 0;
            Sign constantSign = isNegative ? MINUS : PLUS;
            sign = sign == constantSign ? PLUS : MINUS;
            if (sign == PLUS) {
                value = value.abs();
            }
            return new Constant(sign, new BigDecimal(value.toPlainString()));
        });
    }

}
