package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Expression;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.Rules;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        int executionId = (int) (Math.random() * Integer.MAX_VALUE);
        log.debug("Simplifying [{}]... ExecutionId: [{}]\n", component, executionId);
        boolean componentIsChanged;
        Component rewrittenComponent;
        int iteration = 0;
        Set<String> componentTransformationHistory = new HashSet<>();
        componentTransformationHistory.add(component.toString());
        do {
            log.debug("Started iteration [{}] of execution [{}]", iteration, executionId);
            componentIsChanged = false;
            String originalComponentAsString;
            for (Rule rule : Rules.rules) {
                originalComponentAsString = component.toString();
                rewrittenComponent = component.rewrite(rule);
                final String rewrittenComponentAsString = rewrittenComponent.toString();
                final boolean componentHasChangedByCurrentRule = !Objects.equals(rewrittenComponentAsString, originalComponentAsString);
                if (componentHasChangedByCurrentRule) {
                    if (componentTransformationHistory.contains(rewrittenComponentAsString)) {
                        log.warn("Loop detected with rewritten component [{}]: no more rules will be applied", rewrittenComponentAsString);
                        return rewrittenComponent;
                    } else {
                        componentTransformationHistory.add(rewrittenComponentAsString);
                    }
                }
                componentIsChanged = componentIsChanged || componentHasChangedByCurrentRule;
                component = rewrittenComponent;
            }
            log.debug("End iteration [{}] of execution [{}]\n", iteration++, executionId);
        } while (componentIsChanged);
        return component;
    }

}
