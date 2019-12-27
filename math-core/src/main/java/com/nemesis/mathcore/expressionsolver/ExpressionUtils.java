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

    public static String getDerivative(String expression, char var) {
        Component derivative = ExpressionParser.parse(expression).getDerivative(var);
        Component simplifiedDerivative = ExpressionUtils.simplify(derivative);
        return simplifiedDerivative.toString();
    }

    public static Component simplify(String expression) {
        Component parsedExpr = ExpressionParser.parse(expression);
        return simplify(parsedExpr);
    }

    public static Component simplify(Component component) {
        boolean changes;
        Component rewrittenExpr;
        do {
            changes = false;
            String parsedExprAsString = component.toString();
            for (Rule rule : Rules.rules) {
                rewrittenExpr = component.rewrite(rule);
                changes = changes || !Objects.equals(rewrittenExpr.toString(), parsedExprAsString);
                component = rewrittenExpr;
            }
        } while (changes);
        return component;
    }

}
