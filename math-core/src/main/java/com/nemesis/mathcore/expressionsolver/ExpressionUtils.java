package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Expression;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.Rules;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
public class ExpressionUtils {

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = ExpressionParser.parse(expression).getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    public static String getDerivative(String expression, char var) {
        Expression parsedExpr = ExpressionParser.parse(expression);
        Component derivative = parsedExpr.getDerivative(var);
        Component simplifiedDerivative = ExpressionUtils.simplify(derivative);
        return simplifiedDerivative.toString();
    }

    public static Component simplify(String expression) {
        Component parsedExpr = ExpressionParser.parse(expression);
        return simplify(parsedExpr);
    }

    public static Component simplify(Component component) {
        System.out.println();
        int executionId = (int) (Math.random() * Integer.MAX_VALUE);
        log.debug("Simplifying [{}]... ExecutionId: [{}]", component, executionId);
        System.out.println();
        boolean isChanged;
        Component rewrittenExpr;
        int iteration = 0;
        do {
            log.debug("Started iteration [{}] of execution [{}]", iteration++, executionId);
            isChanged = false;
            String parsedExprAsString = component.toString();
            for (Rule rule : Rules.rules) {
                String componentAsString = component.toString();
                rewrittenExpr = component.rewrite(rule);
                if (!Objects.equals(rewrittenExpr.toString(), componentAsString)) {
                    log.debug("Applied rule [{}] to expression [{}], result: [{}]", rule.getClass().getSimpleName(), componentAsString, rewrittenExpr);
                } else {
                    log.debug("Applied rule [{}] to expression [{}], no changes", rule.getClass().getSimpleName(), componentAsString);
                }
                isChanged = isChanged || !Objects.equals(rewrittenExpr.toString(), parsedExprAsString);
                component = rewrittenExpr;
            }
            log.debug("End iteration [{}] of execution [{}]", iteration++, executionId);
        } while (isChanged);
        return component;
    }

}
