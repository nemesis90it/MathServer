package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;

public class LinearEquationResolver {

    private LinearEquationResolver() {
    }

    public static Set<GenericInterval> resolve(Polynomial polynomial, RelationalOperator operator, Variable variable) {

        List<Monomial> numeratorMonomials = new ArrayList<>();
        Constant coefficient = null;

        for (Monomial monomial : polynomial.getMonomials()) {

            Set<Exponential> exponentialWithRequestedVariable = monomial.getLiteralPart().stream()
                    .filter(exponential -> exponential.getBase().contains(variable))
                    .collect(Collectors.toSet());

            if (exponentialWithRequestedVariable.size() > 1) {
                throw new IllegalArgumentException("Unexpected monomial [" + monomial.toString() + "] in linear function: [" + polynomial.toString() + "]");
            } else if (exponentialWithRequestedVariable.isEmpty()) {
                numeratorMonomials.add(monomial);
                continue;
            }

            if (coefficient != null) {
                throw new IllegalArgumentException("Found two monomial with requested variable " + variable.getName() + "in linear function: [" + polynomial.toString() + "]");
            }

            coefficient = monomial.getCoefficient();
        }

        if (coefficient == null) {
            throw new IllegalArgumentException("No monomial found with requested variable " + variable.getName() + "in linear function: [" + polynomial.toString() + "]");
        }

        final Expression numeratorExpression = ComponentUtils.monomialsToExpression(numeratorMonomials.iterator());

        Sign sign = coefficient.getSign() == MINUS ? PLUS : MINUS;

        final Term solution = new Term(new ParenthesizedExpression(sign, numeratorExpression), TermOperator.DIVIDE, new Term(coefficient));

        return Collections.singleton(new SingleDelimiterInterval(variable.getName(), operator, solution));
    }
}
