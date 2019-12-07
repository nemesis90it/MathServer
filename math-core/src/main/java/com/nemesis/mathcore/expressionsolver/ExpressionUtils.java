package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.Rules;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;
import java.util.Objects;

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
        Component rewrittenExpr;
        boolean changes;
        Component parsedExpr = ExpressionParser.parse(expression);
        do {
            changes = false;
            String parsedExprAsString = parsedExpr.toString();
            for (Rule rule : Rules.rules) {
                rewrittenExpr = parsedExpr.rewrite(rule);
                changes = changes || !Objects.equals(rewrittenExpr.toString(), parsedExprAsString);
                parsedExpr = rewrittenExpr;
            }
        } while (changes);
        return parsedExpr.toString();
    }

}
