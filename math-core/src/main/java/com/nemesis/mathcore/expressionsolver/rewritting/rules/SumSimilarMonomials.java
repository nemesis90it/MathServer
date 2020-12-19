package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.monomial.Monomial;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.FactorSignInverter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.*;

public class SumSimilarMonomials implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return c -> (c instanceof Expression || c instanceof WrappedExpression);
    }

    @Override
    public Function<Component, ? extends Component> transformer() {

        return component -> {

            Component originalComponent = component.getClone();
            Expression expression;
            Sign sign = Sign.PLUS;

            if (component instanceof WrappedExpression) {
                expression = ((WrappedExpression) component).getExpression();
                sign = ((WrappedExpression) component).getSign();
            } else {
                expression = (Expression) component;
            }
            List<Monomial> monomials = this.getMonomials(expression, SUM);

            if (monomials.size() > 1) {
                final Expression result = ComponentUtils.sumSimilarMonomialsAndConvertToExpression(monomials);
                if (component instanceof AbsExpression) {
                    return new AbsExpression(sign, result);
                } else if (component instanceof ParenthesizedExpression) {
                    return new ParenthesizedExpression(sign, result);
                } else {
                    return result;
                }
            }
            return originalComponent;
        };
    }


    private List<Monomial> getMonomials(Expression expression, ExpressionOperator operator) {

        final List<Monomial> allMonomials = new ArrayList<>();
        final Monomial monomial;
        final Term term = expression.getTerm();

        // TODO: manage AbsExpression
        if (term.getOperator() == TermOperator.NONE && term.getFactor() instanceof ParenthesizedExpression parExpression) { // term can be a polynomial
            final List<Monomial> monomials = this.getMonomials(parExpression.getExpression(), parExpression.getOperator());
            if (!monomials.isEmpty()) {
                allMonomials.addAll(monomials);
            } else { // First element of the expression isn't a polynomial. TODO: continue searching for other monomials?
                return new ArrayList<>();
            }
        } else {
            monomial = Monomial.getMonomial(term);
            if (monomial != null) {
                if (operator == SUBTRACT) {
                    monomial.setCoefficient((Constant) FactorSignInverter.cloneAndChangeSign(monomial.getCoefficient()));
                }
                allMonomials.add(monomial);
            } else { // First element of the expression isn't a monomial. TODO: continue searching for other monomials?
                return new ArrayList<>();
            }
        }

        Expression subExpression = this.getSubExpression(expression);
        if (subExpression != null) {
            List<Monomial> otherMonomials = this.getMonomials(subExpression, expression.getOperator());
            if (!otherMonomials.isEmpty()) {
                allMonomials.addAll(otherMonomials);
            }
        } else {
            return allMonomials;
        }

        return allMonomials;
    }

    private Expression getSubExpression(Expression expression) {
        Expression subExpression = expression.getSubExpression();
        if (subExpression != null) {
            Term subExpressionTerm = subExpression.getTerm();
            while (subExpression.getOperator() == NONE
                    && subExpressionTerm.getOperator() == TermOperator.NONE
                    && subExpressionTerm.getFactor() instanceof WrappedExpression) {
                // Jump one (useless) level of the tree
                subExpression = ((WrappedExpression) subExpressionTerm.getFactor()).getExpression();
                if (subExpression != null) {
                    subExpressionTerm = subExpression.getTerm();
                } else {
                    return null;
                }
            }
        }
        return subExpression;
    }


}
