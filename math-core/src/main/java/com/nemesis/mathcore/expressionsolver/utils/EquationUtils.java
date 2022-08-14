package com.nemesis.mathcore.expressionsolver.utils;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Variable;
import com.nemesis.mathcore.expressionsolver.equations.LinearEquationResolver;
import com.nemesis.mathcore.expressionsolver.equations.QuadraticEquationResolver;
import com.nemesis.mathcore.expressionsolver.intervals.model.Union;
import com.nemesis.mathcore.expressionsolver.models.Equation;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import lombok.extern.slf4j.Slf4j;

import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isZero;

@Slf4j
public class EquationUtils {

    public static Union resolve(Equation equation, Variable variable) {

        log.info("Resolving equation [{}]", equation);

        Component leftComponent = equation.getLeftComponent();
        Component rightComponent = equation.getRightComponent();
        RelationalOperator operator = equation.getOperator();

        if (!isZero(rightComponent)) {
            throw new UnsupportedOperationException("Only equation in normal form are supported (f(" + variable.getName() + ")=0)");
        }

        final Polynomial polynomial = Polynomial.getPolynomial(leftComponent);
        if (polynomial != null) {
            Integer degree = polynomial.getDegree(variable);
            if (degree != null) {
                return switch (degree) {
                    case 0 -> throw new UnsupportedOperationException("Not implemented"); // TODO
                    case 1 -> LinearEquationResolver.resolve(polynomial, operator, variable);
                    case 2 -> QuadraticEquationResolver.resolve(polynomial, operator, variable);
                    default -> throw new UnsupportedOperationException("Resolution of equation with degree > 2 is not supported yet");
                };
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException("Equation resolution is supported only for polynomials");
        }

    }

    public static Equation simplify(Equation equation, Variable variable) {
        Component leftComponent = ExpressionUtils.simplify(equation.getLeftComponent());
        Component rightComponent = ExpressionUtils.simplify(equation.getRightComponent());
        return new Equation(leftComponent, equation.getOperator(), rightComponent);
    }
}
