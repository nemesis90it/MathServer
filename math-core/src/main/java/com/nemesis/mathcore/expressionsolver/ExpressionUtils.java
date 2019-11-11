package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.Rules;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;

public class ExpressionUtils {

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = ExpressionParser.parse(expression).getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    public static String getDerivative(String expression) {
        Component derivative = ExpressionParser.parse(expression).getDerivative();
        for (Rule rule : Rules.rules) {
            derivative = derivative.rewrite(rule);
        }
        return derivative.toString();
    }

    public static String simplify(String expression) {
        Component parsedExpr = ExpressionParser.parse(expression);
        for (Rule rule : Rules.rules) {
            parsedExpr = parsedExpr.rewrite(rule);
        }
        return parsedExpr.toString();
    }

}
