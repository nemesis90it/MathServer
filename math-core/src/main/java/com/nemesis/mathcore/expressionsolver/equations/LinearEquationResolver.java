package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
import com.nemesis.mathcore.expressionsolver.models.intervals.DoublePointInterval;
import com.nemesis.mathcore.expressionsolver.models.intervals.GenericInterval;
import com.nemesis.mathcore.expressionsolver.models.intervals.Intervals;
import com.nemesis.mathcore.expressionsolver.models.intervals.SinglePointInterval;
import com.nemesis.mathcore.expressionsolver.monomial.LiteralPart;
import com.nemesis.mathcore.expressionsolver.monomial.Monomial;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.FactorSignInverter;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;

public class LinearEquationResolver {

    private static final Infinity PLUS_INFINITY = new Infinity(PLUS);
    private static final Infinity MINUS_INFINITY = new Infinity(MINUS);

    private static final IntervalBuilder EQ_intervalBuilder = (variable, solution) -> new SinglePointInterval(variable, new Point(solution, Point.Type.EQUALS));
    private static final IntervalBuilder NEQ_intervalBuilder = (variable, solution) -> new SinglePointInterval(variable, new Point(solution, Point.Type.NOT_EQUALS));
    private static final IntervalBuilder GTE_intervalBuilder = (variable, solution) -> new DoublePointInterval(variable, DoublePointInterval.Type.GREATER_THAN_OR_EQUALS, solution, PLUS_INFINITY);
    private static final IntervalBuilder LTE_intervalBuilder = (variable, solution) -> new DoublePointInterval(variable, DoublePointInterval.Type.LESS_THAN_OR_EQUALS, MINUS_INFINITY, solution);
    private static final IntervalBuilder GT_intervalBuilder = (variable, solution) -> new DoublePointInterval(variable, DoublePointInterval.Type.GREATER_THAN, solution, PLUS_INFINITY);
    private static final IntervalBuilder LT_intervalBuilder = (variable, solution) -> new DoublePointInterval(variable, DoublePointInterval.Type.LESS_THAN, MINUS_INFINITY, solution);


    private LinearEquationResolver() {
    }

    public static Intervals resolve(Polynomial polynomial, RelationalOperator operator, Variable variable) {

        Set<Factor> aCoefficient = new TreeSet<>();
        List<Monomial> bCoefficient = new ArrayList<>();

        Sign aCoefficientSign = PLUS;

        final String variableName = String.valueOf(variable.getName());

        for (Monomial monomial : polynomial.getMonomials()) {

            final LiteralPart literalPart = monomial.getLiteralPart();

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

        bCoefficient.forEach(monomial -> monomial.setCoefficient((Constant) FactorSignInverter.cloneAndChangeSign(monomial.getCoefficient())));

        final Expression numerator = ComponentUtils.monomialsToExpression(bCoefficient.iterator());
        final Term denominator = Term.buildTerm(aCoefficient.iterator(), TermOperator.MULTIPLY);

        final Term solution = new Term(new ParenthesizedExpression(numerator), TermOperator.DIVIDE, denominator);

        final Component simplifiedSolution = ExpressionUtils.simplify(solution);

        IntervalBuilder intervalBuilder = switch (operator) {
            case EQ -> EQ_intervalBuilder;
            case NEQ -> NEQ_intervalBuilder;
            case GT -> switch (aCoefficientSign) {
                case PLUS -> GT_intervalBuilder;
                case MINUS -> LT_intervalBuilder;
            };
            case GTE -> switch (aCoefficientSign) {
                case PLUS -> GTE_intervalBuilder;
                case MINUS -> LTE_intervalBuilder;
            };
            case LT -> switch (aCoefficientSign) {
                case PLUS -> LT_intervalBuilder;
                case MINUS -> GT_intervalBuilder;
            };
            case LTE -> switch (aCoefficientSign) {
                case PLUS -> LTE_intervalBuilder;
                case MINUS -> GTE_intervalBuilder;
            };
        };

        return new Intervals(intervalBuilder.apply(variableName, simplifiedSolution));

    }

    private interface IntervalBuilder extends BiFunction<String, Component, GenericInterval> {
    }
}
