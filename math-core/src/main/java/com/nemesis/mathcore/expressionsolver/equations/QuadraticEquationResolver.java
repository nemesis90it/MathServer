package com.nemesis.mathcore.expressionsolver.equations;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class QuadraticEquationResolver {

    private QuadraticEquationResolver() {
    }

    public static Set<GenericInterval> resolve(Polynomial polynomial, RelationalOperator operator, Variable variable) {

        Set<Factor> aCoefficient = new TreeSet<>();
        Set<Factor> bCoefficient = new TreeSet<>();
        List<Monomial> cCoefficient = new ArrayList<>();

        for (Monomial monomial : polynomial.getMonomials()) {

            final Monomial.LiteralPart literalPart = monomial.getLiteralPart();

            Set<Exponential> exponentialSetWithRequestedVariable = new HashSet<>();

            for (Exponential exponential : literalPart) {
                if (exponential.getBase().contains(variable)) {
                    if (ComponentUtils.isOne(exponential.getExponent())) {
                        if (!bCoefficient.isEmpty()) {
                            throw new IllegalArgumentException("Found more than one monomial of degree 1 with requested variable " + variable.getName() + " in quadratic function: [" + polynomial.toString() + "]");
                        }
                        Monomial monomialWithDegreeOne = monomial.getClone();
                        bCoefficient.add(monomialWithDegreeOne.getCoefficient());
                        bCoefficient.addAll(monomialWithDegreeOne.getLiteralPart());
                        bCoefficient.remove(exponential); // Remove the exponential containing the variable (it isn't part of 'b' coefficient)
                    } else if (exponential.isScalar() && exponential.getValue().compareTo(new BigDecimal(2)) == 0) {
                        if (!aCoefficient.isEmpty()) {
                            throw new IllegalArgumentException("Found more than one monomial of degree 2 with requested variable " + variable.getName() + " in quadratic function: [" + polynomial.toString() + "]");
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
            if (bCoefficient.isEmpty()) {
                // no variables! (solution:  c=0)
                return new TreeSet<>(); // TODO
            } else {
                return LinearEquationResolver.resolve(polynomial, operator, variable);
            }
        }

        final Term a = Term.buildTerm(aCoefficient.iterator(), MULTIPLY);

        final Base b = bCoefficient.size() == 1 ?
                new ParenthesizedExpression(bCoefficient.stream().findFirst().orElse(new Constant(1))) :
                new ParenthesizedExpression(Term.buildTerm(bCoefficient.iterator(), MULTIPLY));

        final Component c = ComponentUtils.sumSimilarMonomialsAndConvertToExpression(cCoefficient);

        Expression delta = new Expression(
                new Term(new Exponential(b, new Constant(2))),
                SUBTRACT,
                new Term(new Term(new Constant(4), MULTIPLY, a), MULTIPLY, c));

        final Factor minusB = ComponentUtils.cloneAndChangeSign(b);
        final Term twoA = new Term(new Constant(2), MULTIPLY, a);

        if (delta.isScalar()) {
            if (ComponentUtils.isZero(delta)) {
                Component solution = new Term(new Expression(Term.getTerm(minusB)), DIVIDE, twoA);
                return Collections.singleton(new SingleDelimiterInterval(variable.getName(), operator, solution));
            } else if (delta.getValue().compareTo(BigDecimal.ZERO) < 0) {
                return Collections.singleton(new NoDelimiterInterval(variable.getName(), NoDelimiterInterval.Type.VOID));
            }
        }

        final Term deltaSquareRoot = new Term(new RootFunction(2, new ParenthesizedExpression(delta)));

        Component s1 = new Term(new Expression(Term.getTerm(minusB), SUBTRACT, deltaSquareRoot), DIVIDE, twoA);
        Component s2 = new Term(new Expression(Term.getTerm(minusB), SUM, deltaSquareRoot), DIVIDE, twoA);
        Set<GenericInterval> solutions = new TreeSet<>();
        solutions.add(new SingleDelimiterInterval(variable.getName(), operator, s1));
        solutions.add(new SingleDelimiterInterval(variable.getName(), operator, s2));
        return solutions;

    }
}
