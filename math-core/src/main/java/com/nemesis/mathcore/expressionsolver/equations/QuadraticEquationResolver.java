package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.models.DeltaType;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.models.Polynomial;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.models.intervals.*;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.*;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;

public class QuadraticEquationResolver {

    private static final Map<Pair<DeltaType, RelationalOperator>, SolutionBuilder> solutionBuilders = new HashMap<>();


// TODO

    static {

        final SolutionBuilder singlePointSolutionBuilder = (a, b, c, variable, operator) -> {
            Point.Type type = switch (operator) {
                case EQ, LTE -> Point.Type.EQUALS;
                case NEQ, GT -> Point.Type.NOT_EQUALS;
                default -> throw new IllegalArgumentException("Unexpected operator [" + operator + "]");
            };
            final Term delimiter = new Term(new Expression(getMinusB(b)), DIVIDE, getTwoA(a));
            return new Intervals(new SinglePointInterval(variable.toString(), new Point(delimiter, type)));
        };

        final SolutionBuilder noPointSolutionBuilderForZeroDelta = (a, b, c, variable, operator) -> {
            final GenericInterval interval = switch (operator) {
                case GTE -> new DoublePointInterval(variable.toString(), Delimiter.MINUS_INFINITY, Delimiter.PLUS_INFINITY); // FOR EACH
                case LT -> new NoPointInterval(variable.toString());
                default -> throw new IllegalArgumentException("Unexpected operator [" + operator + "]");
            };
            return new Intervals(Collections.singleton(interval));
        };

        final SolutionBuilder noPointSolutionBuilderForNegativeDelta = (a, b, c, variable, operator) -> {
            final GenericInterval interval = switch (operator) {
                case EQ, LT, LTE -> new NoPointInterval(variable.toString());
                case NEQ, GT, GTE -> new DoublePointInterval(variable.toString(), Delimiter.MINUS_INFINITY, Delimiter.PLUS_INFINITY); // FOR EACH
            };
            return new Intervals(Collections.singleton(interval));
        };


        final SolutionBuilder doublePointSolutionBuilder = (a, b, c, variable, operator) -> {

            final Term deltaSquareRoot = new Term(new RootFunction(2, new ParenthesizedExpression(getDelta(a, b, c))));
            final Term minusB = getMinusB(b);
            final Component twoA = getTwoA(a);

            final Component s1 = new Term(new ParenthesizedExpression(minusB, SUBTRACT, deltaSquareRoot), DIVIDE, twoA);
            final Component s2 = new Term(new ParenthesizedExpression(minusB, SUM, deltaSquareRoot), DIVIDE, twoA);

            if (!s1.isScalar() || !s2.isScalar()) {
                throw new UnsupportedOperationException("Multiple variable equations is not supported yet");
            }

            List<Component> simplifiedSolutions = Stream.of(s1, s2).parallel().map(ExpressionUtils::simplify).collect(Collectors.toList());

            Map<Constant, BigDecimal> solutionsMap = Map.of(
                    simplifiedSolutions.get(0).getValueAsConstant(), s1.getValue(),
                    simplifiedSolutions.get(1).getValueAsConstant(), s2.getValue()
            );

            List<Constant> delimiters = solutionsMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            final Constant leftDelimiter = delimiters.get(0);
            final Constant rightDelimiter = delimiters.get(1);

            final Set<GenericInterval> solutions = new TreeSet<>();

            final String variableName = variable.toString();

            switch (operator) {
                case EQ -> {
                    solutions.add(new SinglePointInterval(variableName, new Point(leftDelimiter, Point.Type.EQUALS)));
                    solutions.add(new SinglePointInterval(variableName, new Point(rightDelimiter, Point.Type.EQUALS)));
                }
                case NEQ -> {
                    solutions.add(new SinglePointInterval(variableName, new Point(leftDelimiter, Point.Type.NOT_EQUALS)));
                    solutions.add(new SinglePointInterval(variableName, new Point(rightDelimiter, Point.Type.NOT_EQUALS)));
                }
                case GT -> {
                    solutions.add(new DoublePointInterval(variableName, Delimiter.MINUS_INFINITY, new Delimiter(Delimiter.Type.OPEN, leftDelimiter)));
                    solutions.add(new DoublePointInterval(variableName, new Delimiter(Delimiter.Type.OPEN, rightDelimiter), Delimiter.PLUS_INFINITY));
                }
                case GTE -> {
                    solutions.add(new DoublePointInterval(variableName, Delimiter.MINUS_INFINITY, new Delimiter(Delimiter.Type.CLOSED, leftDelimiter)));
                    solutions.add(new DoublePointInterval(variableName, new Delimiter(Delimiter.Type.CLOSED, rightDelimiter), Delimiter.PLUS_INFINITY));
                }
                case LT -> {
                    solutions.add(new DoublePointInterval(variableName, DoublePointInterval.Type.STRICTLY_BETWEEN, leftDelimiter, rightDelimiter));
                }
                case LTE -> {
                    solutions.add(new DoublePointInterval(variableName, DoublePointInterval.Type.BETWEEN, leftDelimiter, rightDelimiter));
                }
            }

            return new Intervals(solutions);
        };

        solutionBuilders.put(Pair.of(DeltaType.ZERO, EQ), singlePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, NEQ), singlePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, GT), singlePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, GTE), noPointSolutionBuilderForZeroDelta);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, LT), noPointSolutionBuilderForZeroDelta);
        solutionBuilders.put(Pair.of(DeltaType.ZERO, LTE), singlePointSolutionBuilder);

        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, EQ), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, NEQ), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, GT), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, GTE), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, LT), noPointSolutionBuilderForNegativeDelta);
        solutionBuilders.put(Pair.of(DeltaType.NEGATIVE, LTE), noPointSolutionBuilderForNegativeDelta);

        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, EQ), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, NEQ), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, GT), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, GTE), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, LT), doublePointSolutionBuilder);
        solutionBuilders.put(Pair.of(DeltaType.POSITIVE, LTE), doublePointSolutionBuilder);

    }

    private QuadraticEquationResolver() {
    }

    public static Intervals resolve(Polynomial polynomial, RelationalOperator operator, Variable variable) {

        Set<Factor> aCoefficient = new TreeSet<>();
        Set<Factor> bCoefficient = new TreeSet<>();
        List<Monomial> cCoefficient = new ArrayList<>();

        final char variableName = variable.getName();

        for (Monomial monomial : polynomial.getMonomials()) {

            final Monomial.LiteralPart literalPart = monomial.getLiteralPart();

            Set<Exponential> exponentialSetWithRequestedVariable = new HashSet<>();

            for (Exponential exponential : literalPart) {
                if (exponential.getBase().contains(variable)) {
                    final Factor exponent = exponential.getExponent();
                    if (ComponentUtils.isOne(exponent)) {
                        if (!bCoefficient.isEmpty()) {
                            throw new IllegalArgumentException("Found more than one monomial of degree 1 for variable [" + variableName + "] in quadratic function: [" + polynomial.toString() + "]");
                        }
                        Monomial monomialWithDegreeOne = monomial.getClone();
                        bCoefficient.add(monomialWithDegreeOne.getCoefficient());
                        bCoefficient.addAll(monomialWithDegreeOne.getLiteralPart());
                        bCoefficient.remove(exponential); // Remove the exponential containing the variable (it isn't part of 'b' coefficient)
                    } else if (exponent.isScalar() && exponent.getValue().compareTo(new BigDecimal(2)) == 0) {
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

    private static Term getMinusB(Base b) {
        return Term.getTerm(ComponentUtils.cloneAndChangeSign(b));
    }

    private static Term getTwoA(Term a) {
        return new Term(new Constant(2), MULTIPLY, a);
    }

    @FunctionalInterface
    private interface SolutionBuilder {
        Intervals getSolutions(Term a, Base b, Component c, Variable variable, RelationalOperator operator);
    }

}
