package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.*;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;

public class SumSimilarMonomials implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return c -> (c instanceof Expression || c instanceof ParenthesizedExpression);
    }

    @Override
    public Function<Component, ? extends Component> transformer() {

        return component -> {

            Expression expression;
            if (component instanceof ParenthesizedExpression) {
                expression = ((ParenthesizedExpression) component).getExpression();
            } else {
                expression = (Expression) component;
            }

            List<Monomial> monomials = this.getMonomials(expression, SUM);
            if (monomials.size() > 1) {
                Expression result = this.monomialsToExpression(this.sumSimilarMonomials(monomials).iterator());
                return result.toString().equals(component.toString()) ? component : result; // This rule can change uselessly the component structure
            }
            return component;
        };
    }

    private List<Monomial> getMonomials(Expression expression, ExpressionOperator operator) {
        List<Monomial> monomials = new ArrayList<>();
        Monomial monomial = Monomial.getMonomial(expression.getTerm());
        if (monomial != null) {
            monomials.add(monomial);
            if (operator == SUBTRACT) {
                monomial.setCoefficient((Constant) ComponentUtils.cloneAndChangeSign(monomial.getCoefficient()));
            }
            Expression subExpression = this.getSubExpression(expression);
            if (subExpression != null) {
                List<Monomial> otherMonomials = this.getMonomials(subExpression, expression.getOperator());
                if (!otherMonomials.isEmpty()) {
                    monomials.addAll(otherMonomials);
                }
            } else {
                return monomials;
            }
        } else { // First element of the expression isn't a monomial. TODO: continue searching for other monomials
            return new ArrayList<>();
        }
        return monomials;
    }

    private Expression getSubExpression(Expression expression) {
        Expression subExpression = expression.getSubExpression();
        if (subExpression != null) {
            Term subExpressionTerm = subExpression.getTerm();
            while (subExpression.getOperator() == NONE
                    && subExpressionTerm.getOperator() == TermOperator.NONE
                    && subExpressionTerm.getFactor() instanceof ParenthesizedExpression) {
                // Jump one (useless) level of the tree
                subExpression = ((ParenthesizedExpression) subExpressionTerm.getFactor()).getExpression();
                if (subExpression != null) {
                    subExpressionTerm = subExpression.getTerm();
                } else {
                    return null;
                }
            }
        }
        return subExpression;
    }

    private List<Monomial> sumSimilarMonomials(Collection<Monomial> monomials) {

        List<Monomial> heterogeneousMonomials = new ArrayList<>();
        BinaryOperator<Monomial> monomialAccumulator = (m1, m2) -> Monomial.getMonomial(Monomial.sum(m1, m2));

        Map<TreeSet<Exponential>, List<Monomial>> similarMonomialsGroups = monomials.stream()
                .collect(Collectors.groupingBy(Monomial::getLiteralPart));

        similarMonomialsGroups.forEach((exponentialSet, similarMonomials) -> {
            Monomial sum = similarMonomials.stream().reduce(Monomial.getZero(exponentialSet), monomialAccumulator);
            heterogeneousMonomials.add(sum);
        });

        Collections.sort(heterogeneousMonomials);
        return heterogeneousMonomials;
    }

    private Expression monomialsToExpression(Iterator<Monomial> iterator) {
        if (iterator.hasNext()) {
            Expression expression = new Expression();
            Monomial monomial = iterator.next();
            final TreeSet<Exponential> literalPart = monomial.getLiteralPart();
            Term term;
            if (!literalPart.isEmpty()) {
                term = new Term(monomial.getCoefficient(), MULTIPLY, Term.buildTerm(literalPart.iterator(), MULTIPLY));
            } else {
                term = new Term(monomial.getCoefficient());
            }
            expression.setTerm(term);
            if (iterator.hasNext()) {
                expression.setOperator(SUM);
                expression.setSubExpression(this.monomialsToExpression(iterator));
            } else {
                expression.setOperator(NONE);
            }
            return expression;
        }
        return new Expression(new Term(new Constant("0"))); // TODO: return null?
    }

}
