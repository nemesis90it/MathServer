package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;

public class ExpressionUtils {

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = ExpressionParser.parse(expression).getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    public static String getDerivative(String expression) {
        Component derivative = ExpressionParser.parse(expression).getDerivative();
        return derivative.simplify().toString();
    }

    public static String simplify(String expression) {
        Component simplifiedExpr = ExpressionParser.parse(expression).simplify();
        return simplifiedExpr.toString();
    }
}
