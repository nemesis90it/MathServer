package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.*;

public class SimilarMonomialsReduction implements Rule {

    @Override
    public Predicate<Component> condition() {
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
                return this.monomialsToExpression(this.sumSimilarMonomials(monomials).iterator());
            }
            return expression;
        };
    }

    private List<Monomial> getMonomials(Expression expression, ExpressionOperator operator) {
        List<Monomial> monomials = new ArrayList<>();
        Expression subExpression = expression.getSubExpression();
        Monomial monomial = Monomial.getMonomial(expression.getTerm());
        if (monomial != null) {
            monomials.add(monomial);
            if (operator == SUBTRACT) {
                monomial.setCoefficient((Constant) ComponentUtils.cloneAndChangeSign(monomial.getCoefficient()));
            }
            if (subExpression != null) {
                List<Monomial> otherMonomials = this.getMonomials(subExpression, expression.getOperator());
                if (!otherMonomials.isEmpty()) {
                    monomials.addAll(otherMonomials);
                }
            } else {
                return monomials;
            }
        } else {
            return new ArrayList<>();
        }
        return monomials;
    }

    private List<Monomial> sumSimilarMonomials(Collection<Monomial> monomials) {

        List<Monomial> heterogeneousMonomials = new ArrayList<>();
        BinaryOperator<Monomial> monomialAccumulator = (m1, m2) -> Monomial.getMonomial(Monomial.sum(m1, m2));

        Map<Exponential, List<Monomial>> similarMonomialsGroups = monomials.stream()
                .collect(Collectors.groupingBy(m -> new Exponential(m.getBase(), m.getExponent())));

        similarMonomialsGroups.forEach((exponential, similarMonomials) -> {
            Monomial sum = similarMonomials.stream().reduce(Monomial.getZero(exponential), monomialAccumulator);
            heterogeneousMonomials.add(sum);
        });

        Collections.sort(heterogeneousMonomials);
        return heterogeneousMonomials;
    }

    private Expression monomialsToExpression(Iterator<Monomial> iterator) {
        if (iterator.hasNext()) {
            Expression expression = new Expression();
            expression.setTerm(ComponentUtils.getTerm(iterator.next()));
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
