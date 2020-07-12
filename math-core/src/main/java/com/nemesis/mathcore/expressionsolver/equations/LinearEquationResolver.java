package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;

public class LinearEquationResolver {

    private LinearEquationResolver() {
    }

    public static Intervals resolve(Polynomial polynomial, RelationalOperator operator, Variable variable) {

        Set<Factor> aCoefficient = new TreeSet<>();
        List<Monomial> bCoefficient = new ArrayList<>();

        Sign aCoefficientSign = PLUS;

        final char variableName = variable.getName();

        for (Monomial monomial : polynomial.getMonomials()) {

            final Monomial.LiteralPart literalPart = monomial.getLiteralPart();

            Set<Exponential> exponentialSetWithRequestedVariable = new HashSet<>();

            for (Exponential exponential : literalPart) {
                if (exponential.getBase().contains(variable)) {
                    if (ComponentUtils.isOne(exponential.getExponent())) {
                        if (!aCoefficient.isEmpty()) {
                            throw new IllegalArgumentException("Found more than one monomial of degree 1 for variable [" + variableName + "] in linear function: [" + polynomial.toString() + "]");
                        }
                        Monomial monomialWithDegreeOne = monomial.getClone();
                        aCoefficientSign = monomialWithDegreeOne.getCoefficient().getValue().compareTo(BigDecimal.ZERO) >= 0 ? PLUS : MINUS;
                        aCoefficient.add(monomialWithDegreeOne.getCoefficient());
                        aCoefficient.addAll(monomialWithDegreeOne.getLiteralPart());
                        aCoefficient.remove(exponential); // Remove the exponential containing the variable (it isn't part of 'a' coefficient)
                    } else {
                        throw new IllegalArgumentException("Unexpected degree in linear function: [" + polynomial.toString() + "] for variable [" + variableName + "]");
                    }
                    exponentialSetWithRequestedVariable.add(exponential);
                } // Else, continue to search the exponential containing the variable, if any.
            }

            // If at the end of the loop over literalPart, no exponential with variable was found, current monomial is considered part of 'b' coefficient

            if (exponentialSetWithRequestedVariable.size() > 1) {
                // Only one exponential with degree 1 are expected
                throw new IllegalArgumentException("Unexpected monomial [" + monomial.toString() + "] in linear function: [" + polynomial.toString() + "]");
            } else if (exponentialSetWithRequestedVariable.isEmpty()) {
                bCoefficient.add(monomial);
            }
        }

        if (aCoefficient.isEmpty()) {
            // This case is already managed in com.nemesis.mathcore.expressionsolver.ExpressionUtils#resolve
            throw new IllegalArgumentException("Unexpected degree [0] for variable [" + variable + "] in linear function: [" + polynomial.toString() + "]");
        }

        bCoefficient.forEach(monomial -> monomial.setCoefficient((Constant) ComponentUtils.cloneAndChangeSign(monomial.getCoefficient())));

        final Expression numerator = ComponentUtils.monomialsToExpression(bCoefficient.iterator());
        final Term denominator = Term.buildTerm(aCoefficient.iterator(), TermOperator.MULTIPLY);

        final Term solution = new Term(new ParenthesizedExpression(numerator), TermOperator.DIVIDE, denominator);

        SingleDelimiterInterval.Type intervalType = switch (operator) {
            case EQUALS -> SingleDelimiterInterval.Type.EQUALS;
            case NOT_EQUALS -> SingleDelimiterInterval.Type.NOT_EQUALS;
            case GREATER_THAN -> aCoefficientSign == PLUS ? SingleDelimiterInterval.Type.GREATER_THAN : SingleDelimiterInterval.Type.LESS_THAN;
            case GREATER_THAN_OR_EQUALS -> aCoefficientSign == PLUS ? SingleDelimiterInterval.Type.GREATER_THAN_OR_EQUALS : SingleDelimiterInterval.Type.LESS_THAN_OR_EQUALS;
            case LESS_THAN -> aCoefficientSign == PLUS ? SingleDelimiterInterval.Type.LESS_THAN : SingleDelimiterInterval.Type.GREATER_THAN;
            case LESS_THAN_OR_EQUALS -> aCoefficientSign == PLUS ? SingleDelimiterInterval.Type.LESS_THAN_OR_EQUALS : SingleDelimiterInterval.Type.GREATER_THAN_OR_EQUALS;
        };

        final Component simplifiedSolution = ExpressionUtils.simplify(solution);
        return new Intervals(new SingleDelimiterInterval(variable.toString(), intervalType, simplifiedSolution));

    }
}
