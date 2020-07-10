package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.*;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.*;

public class QuadraticEquationResolver {

    private static final Map<Pair<DeltaType, RelationalOperator>, SolutionBuilder> solutionBuilders = new HashMap<>();

    static {

        final SolutionBuilder singlePointSolutionBuilder = (a, b, c, variable, operator) -> {
            SingleDelimiterInterval.Type intervalType = switch (operator) {
                case EQUALS, LESS_THAN_OR_EQUALS -> SingleDelimiterInterval.Type.EQUALS;
                case NOT_EQUALS, GREATER_THAN -> SingleDelimiterInterval.Type.NOT_EQUALS;
                default -> throw new IllegalArgumentException("Unexpected operator [" + operator + "]");
            };
            final Term delimiter = new Term(new Expression(Term.getTerm(getMinusB(b))), DIVIDE, getTwoA(a));
            return Collections.singleton(new SingleDelimiterInterval(variable.toString(), intervalType, delimiter));
        };

        final SolutionBuilder noPointSolutionBuilderForZeroDelta = (a, b, c, variable, operator) -> {
            NoDelimiterInterval.Type intervalType = switch (operator) {
                case GREATER_THAN_OR_EQUALS -> NoDelimiterInterval.Type.FOR_EACH;
                case LESS_THAN -> NoDelimiterInterval.Type.VOID;
                default -> throw new IllegalArgumentException("Unexpected operator [" + operator + "]");
            };
            return Collections.singleton(new NoDelimiterInterval(variable.toString(), intervalType));
        };

        final SolutionBuilder noPointSolutionBuilderForNegativeDelta = (a, b, c, variable, operator) -> {
            NoDelimiterInterval.Type intervalType = switch (operator) {
                case EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS -> NoDelimiterInterval.Type.VOID;
                case NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS -> NoDelimiterInterval.Type.FOR_EACH;
            };
            return Collections.singleton(new NoDelimiterInterval(variable.toString(), intervalType));
        };


        final SolutionBuilder doublePointSolutionBuilder = (a, b, c, variable, operator) -> {

            final Term deltaSquareRoot = new Term(new RootFunction(2, new ParenthesizedExpression(getDelta(a, b, c))));
            final Factor minusB = getMinusB(b);
            final Component twoA = getTwoA(a);

            final Component s1 = new Term(new Expression(Term.getTerm(minusB), SUBTRACT, deltaSquareRoot), DIVIDE, twoA);
            final Component s2 = new Term(new Expression(Term.getTerm(minusB), SUM, deltaSquareRoot), DIVIDE, twoA);

            if (!s1.isScalar() || !s2.isScalar()) {
                throw new UnsupportedOperationException("Multiple variable equations is not supported yet");
            }

            final List<BigDecimal> delimiters = Arrays.asList(s1.getValue(), s2.getValue());
            Collections.sort(delimiters);

            final Set<GenericInterval> solutions = new TreeSet<>();

            final Constant leftDelimiter = new Constant(delimiters.get(0));
            final Constant rightDelimiter = new Constant(delimiters.get(1));

            final String variableName = variable.toString();

            switch (operator) {
                case EQUALS -> {
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.EQUALS, leftDelimiter));
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.EQUALS, rightDelimiter));
                }
                case NOT_EQUALS -> {
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.NOT_EQUALS, leftDelimiter));
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.NOT_EQUALS, rightDelimiter));
                }
                case GREATER_THAN -> {
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.LESS_THAN, leftDelimiter));
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.GREATER_THAN, rightDelimiter));
                }
                case GREATER_THAN_OR_EQUALS -> {
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.LESS_THAN_OR_EQUALS, leftDelimiter));
                    solutions.add(new SingleDelimiterInterval(variableName, SingleDelimiterInterval.Type.GREATER_THAN_OR_EQUALS, rightDelimiter));
                }
                case LESS_THAN -> {
                    solutions.add(new DoubleDelimitersInterval(variableName, DoubleDelimitersInterval.Type.STRICTLY_BETWEEN, leftDelimiter, rightDelimiter));
                }
                case LESS_THAN_OR_EQUALS -> {
                    solutions.add(new DoubleDelimitersInterval(variableName, DoubleDelimitersInterval.Type.BETWEEN, leftDelimiter, rightDelimiter));
                }
            }

            return solutions;
        };

        solutionBuilders.put(Pair.of(DeltaType.ZERO, EQUALS), singlePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, NOT_EQUALS), singlePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, GREATER_THAN), singlePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, GREATER_THAN_OR_EQUALS), noPointSolutionBuilderForZeroDelta);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, LESS_THAN), noPointSolutionBuilderForZeroDelta);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, LESS_THAN_OR_EQUALS), singlePointSolutionBuilder);

        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, EQUALS), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, NOT_EQUALS), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, GREATER_THAN), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, GREATER_THAN_OR_EQUALS), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, LESS_THAN), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, LESS_THAN_OR_EQUALS), noPointSolutionBuilderForNegativeDelta);

        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, EQUALS), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, NOT_EQUALS), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, GREATER_THAN), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, GREATER_THAN_OR_EQUALS), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, LESS_THAN), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, LESS_THAN_OR_EQUALS), doublePointSolutionBuilder);

    }

    private QuadraticEquationResolver() {
    }

    public static Set<GenericInterval> resolve(Polynomial polynomial, RelationalOperator operator, Variable variable) {

        Set<Factor> aCoefficient = new TreeSet<>();
        Set<Factor> bCoefficient = new TreeSet<>();
        List<Monomial> cCoefficient = new ArrayList<>();

        final char variableName = variable.getName();

        for (Monomial monomial : polynomial.getMonomials()) {

            final Monomial.LiteralPart literalPart = monomial.getLiteralPart();

            Set<Exponential> exponentialSetWithRequestedVariable = new HashSet<>();

            for (Exponential exponential : literalPart) {
                if (exponential.getBase().contains(variable)) {
                    if (ComponentUtils.isOne(exponential.getExponent())) {
                        if (!bCoefficient.isEmpty()) {
                            throw new IllegalArgumentException("Found more than one monomial of degree 1 for variable [" + variableName + "] in quadratic function: [" + polynomial.toString() + "]");
                        }
                        Monomial monomialWithDegreeOne = monomial.getClone();
                        bCoefficient.add(monomialWithDegreeOne.getCoefficient());
                        bCoefficient.addAll(monomialWithDegreeOne.getLiteralPart());
                        bCoefficient.remove(exponential); // Remove the exponential containing the variable (it isn't part of 'b' coefficient)
                    } else if (exponential.isScalar() && exponential.getValue().compareTo(new BigDecimal(2)) == 0) {
                        if (!aCoefficient.isEmpty()) {
                            throw new IllegalArgumentException("Found more than one monomial of degree 2 for variable [" + variableName + "] in quadratic function: [" + polynomial.toString() + "]");
                        }
                        Monomial monomialWithDegreeTwo = monomial.getClone();
                        aCoefficient.add(monomialWithDegreeTwo.getCoefficient());
                        aCoefficient.addAll(monomialWithDegreeTwo.getLiteralPart());
                        aCoefficient.remove(exponential); // Remove the exponential containing the variable (it isn't part of 'a' coefficient)
                    } else {
                        throw new IllegalArgumentException("Unexpected degree in quadratic function: [" + polynomial.toString() + "]");
                    }
                    exponentialSetWithRequestedVariable.add(exponential);
                } // Else, continue to search the exponential containing the variable, if any.
            }

            // If at the end of the loop over literalPart, no exponential with variable was found, current monomial is considered part of 'c' coefficient

            if (exponentialSetWithRequestedVariable.size() > 2) {
                // Only one exponential of degree 2 and one exponential with degree 1 are expected
                throw new IllegalArgumentException("Unexpected monomial [" + monomial.toString() + "] in quadratic function: [" + polynomial.toString() + "]");
            } else if (exponentialSetWithRequestedVariable.isEmpty()) {
                cCoefficient.add(monomial);
            }
        }

        if (aCoefficient.isEmpty()) {
            // These cases are already managed in com.nemesis.mathcore.expressionsolver.ExpressionUtils#resolve
            if (bCoefficient.isEmpty()) {
                throw new IllegalArgumentException("Unexpected degree [0] for variable [" + variable + "] in quadratic function: [" + polynomial.toString() + "]");
            } else {
                throw new IllegalArgumentException("Unexpected degree [1] for variable [" + variable + "] in quadratic function: [" + polynomial.toString() + "]");
            }
        }

        final Term a = Term.buildTerm(aCoefficient.iterator(), MULTIPLY);

        final Base b = bCoefficient.size() == 1 ?
                new ParenthesizedExpression(bCoefficient.stream().findFirst().orElse(new Constant(1))) :
                new ParenthesizedExpression(Term.buildTerm(bCoefficient.iterator(), MULTIPLY));

        final Component c = ComponentUtils.sumSimilarMonomialsAndConvertToExpression(cCoefficient);

        Expression delta = getDelta(a, b, c);

        final DeltaType deltaType;

        if (delta.isScalar()) {
            if (isNegative(delta)) {
                deltaType = DeltaType.NEGATIVE;
            } else if (ComponentUtils.isZero(delta)) {
                deltaType = DeltaType.ZERO;
            } else {
                deltaType = DeltaType.POSITIVE;
            }
        } else {
            throw new UnsupportedOperationException("Delta is not defined (contains variables)"); // TODO: manage this case
        }

        final SolutionBuilder solutionBuilder = solutionBuilders.get(Pair.of(deltaType, operator));

        return solutionBuilder.getSolutions(a, b, c, variable, operator);
    }

    private static Expression getDelta(Term a, Base b, Component c) {
        return new Expression(
                new Term(new Exponential(b, new Constant(2))),
                SUBTRACT,
                new Term(new Term(new Constant(4), MULTIPLY, a), MULTIPLY, c));
    }

    private static boolean isNegative(Component component) {
        return component.getValue().compareTo(BigDecimal.ZERO) < 0;
    }

    private static Factor getMinusB(Base b) {
        return ComponentUtils.cloneAndChangeSign(b);
    }

    private static Term getTwoA(Term a) {
        return new Term(new Constant(2), MULTIPLY, a);
    }

    @FunctionalInterface
    private interface SolutionBuilder {
        Set<GenericInterval> getSolutions(Term a, Base b, Component c, Variable variable, RelationalOperator operator);
    }

}
