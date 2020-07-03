package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.equations.LinearEquationResolver;
import com.nemesis.mathcore.expressionsolver.equations.QuadraticEquationResolver;
import com.nemesis.mathcore.expressionsolver.expression.components.Component;
import com.nemesis.mathcore.expressionsolver.expression.components.Constant;
import com.nemesis.mathcore.expressionsolver.expression.components.Expression;
import com.nemesis.mathcore.expressionsolver.expression.components.Variable;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.Rules;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isZero;

@Slf4j
public class ExpressionUtils {

    public static BigDecimal evaluate(String expression) {
        final Expression parsedExpression = ExpressionParser.parse(expression);
        BigDecimal rawResult = parsedExpression.getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    public static Component getDerivative(String expression, Variable var) {
        Expression parsedExpr = ExpressionParser.parse(expression);
        Component simplifiedDerivative = getDerivative(parsedExpr, var);
        return simplifiedDerivative;
    }

    public static Component getDerivative(Component function, Variable var) {
        Component derivative = function.getDerivative(var);
        Component simplifiedDerivative = ExpressionUtils.simplify(derivative);
        return simplifiedDerivative;
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

    public static Domain getDomain(String expression, Variable variable) {
        return simplify(expression).getDomain(variable);
    }

    public static Set<GenericInterval> resolve(Component leftComponent, RelationalOperator operator, Component rightComponent, Variable variable) {

        if (!(rightComponent instanceof Constant constant && !isZero(constant))) {
            throw new UnsupportedOperationException("Only equation in normal form are supported (f(" + variable.getName() + ")=0)");
        }

        if (operator != RelationalOperator.EQUALS && operator != RelationalOperator.NOT_EQUALS) {
            throw new UnsupportedOperationException("inequalities resolution is not supported yet");
        }

        Set<GenericInterval> result = new HashSet<>();

        final Polynomial polynomial = Polynomial.getPolynomial(leftComponent);
        if (polynomial != null) {
            Integer degree = polynomial.getDegree(variable);
            if (degree != null) {
                switch (degree) {
                    case 0 -> result.add(new NoDelimiterInterval(variable.getName(), NoDelimiterInterval.Type.FOR_EACH));
                    case 1 -> result.addAll(LinearEquationResolver.resolve(polynomial, operator, variable));
                    case 2 -> result.addAll(QuadraticEquationResolver.resolve(polynomial, operator, variable));
                    default -> throw new UnsupportedOperationException("Resolution of equation with degree > 2 is not supported yet");
                }
            } else {
                return Collections.singleton(new NoDelimiterInterval(variable.getName(), NoDelimiterInterval.Type.UNDEFINED));
                // TODO: throw UnsupportedOperationException ?
            }
        } else {
            throw new UnsupportedOperationException("Equation resolution is supported only for polynomials");
        }

        return result;
    }
}
