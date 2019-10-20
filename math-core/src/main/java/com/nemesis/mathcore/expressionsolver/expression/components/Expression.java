package com.nemesis.mathcore.expressionsolver.expression.components;

    /*
          Expression ::= Term + Expression
          Expression ::= Term - Expression
          Expression ::= Term
    */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.*;

public class Expression extends Component {

    protected Term term;
    protected ExpressionOperator operator;
    protected Expression subExpression;

    public Expression(Term term, ExpressionOperator operator, Expression subExpression) {
        this.term = term;
        this.operator = operator;
        this.subExpression = subExpression;
    }

    public Expression(Term term) {
        this.term = term;
        this.operator = ExpressionOperator.NONE;
    }

    public Expression() {
    }

    public Term getTerm() {
        return term;
    }

    public ExpressionOperator getOperator() {
        return operator;
    }

    public Expression getSubExpression() {
        return subExpression;
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal value;
        switch (operator) {
            case NONE:
                value = term.getValue();
                break;
            case SUM:
                value = term.getValue().add(subExpression.getValue());
                break;
            case SUBTRACT:
                value = term.getValue().subtract(subExpression.getValue());
                break;
            default:
                throw new RuntimeException("Illegal expression operator '" + operator + "'");
        }
        this.value = value;
        return value;
    }

    @Override
    public Component getDerivative() {

        Component termDerivative = term.getDerivative();
        if (subExpression == null) {
            return termDerivative;
        } else {
            Component subExprDerivative = subExpression.getDerivative();
            Term td = ComponentUtils.getTerm(termDerivative);
            Term ed = ComponentUtils.getTerm(subExprDerivative);
            return new Expression(td, operator, new Expression(ed));
        }
    }

    @Override
    public Component simplify() {

        // TODO: test it
//        Expression simplifiedExpression = this.sumSimilarMonomials();
        Expression simplifiedExpression = this;

        Component simplifiedTerm = simplifiedExpression.getTerm().simplify();

        ExpressionOperator operator = simplifiedExpression.getOperator();
        if (operator == ExpressionOperator.NONE) {
            return simplifiedTerm;
        }

        Component simplifiedSubExpression = simplifiedExpression.getSubExpression().simplify();

        Monomial leftMonomial = Monomial.getMonomial(simplifiedTerm);
        Monomial rightMonomial = Monomial.getMonomial(simplifiedSubExpression);

        Term defaultTerm = ComponentUtils.getTerm(simplifiedTerm);
        Expression defaultSubExpression = ComponentUtils.getExpression(simplifiedSubExpression);

        Expression defaultExpression = new Expression(defaultTerm, operator, defaultSubExpression);

        if (leftMonomial != null && rightMonomial != null) {
            Term simplifiedExpressionAsTerm;
            if (operator == SUM) {
                simplifiedExpressionAsTerm = Monomial.sum(rightMonomial, leftMonomial);
            } else if (operator == SUBTRACT) {
                simplifiedExpressionAsTerm = Monomial.subtract(rightMonomial, leftMonomial);
            } else {
                throw new RuntimeException("Unexpected operator [" + operator + "]");
            }
            return Objects.requireNonNullElse(simplifiedExpressionAsTerm, defaultExpression);
        }
        return defaultExpression;

    }

    private Expression sumSimilarMonomials() {
        List<Monomial> monomials = this.getMonomials(this, SUM);

        if (monomials == null) {
            return this;
        }

        Map<Base, List<Monomial>> monomialsByBase = monomials.stream().collect(Collectors.groupingBy(Monomial::getBase));

        BinaryOperator<Monomial> accumulator = (m1, m2) -> Monomial.getMonomial(Monomial.sum(m1, m2));

        List<Monomial> heterogeneousMonomials = new ArrayList<>();
        monomialsByBase.forEach((base, homogeneousMonomials) -> {
            Monomial sum = homogeneousMonomials.stream().reduce(Monomial.getZero(base), accumulator);
            heterogeneousMonomials.add(sum);
        });

        return this.monomialsToExpression(heterogeneousMonomials.iterator());
    }

    private List<Monomial> getMonomials(Expression expression, ExpressionOperator operator) {
        List<Monomial> monomials = new ArrayList<>();
        Expression subExpression = expression.getSubExpression();
        Monomial monomial = Monomial.getMonomial(expression.getTerm());
        if (monomial != null) {
            monomials.add(monomial);
            if (operator == SUBTRACT) {
                monomial.getCoefficient().changeSign();
            }
            if (subExpression != null) {
                List<Monomial> otherMonomials = this.getMonomials(subExpression, expression.getOperator());
                if (otherMonomials != null) {
                    monomials.addAll(otherMonomials);
                }
            } else {
                return monomials;
            }
        } else {
            return null;
        }
        return monomials;
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

    @Override
    public String toString() {

        if (subExpression == null) {
            return term.toString();
        } else {
            if (operator.equals(SUM)) {
                return ExpressionBuilder.sum(term.toString(), subExpression.toString());
            } else if (operator.equals(SUBTRACT)) {
                return ExpressionBuilder.difference(term.toString(), subExpression.toString());
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public void setOperator(ExpressionOperator operator) {
        this.operator = operator;
    }

    public void setSubExpression(Expression subExpression) {
        this.subExpression = subExpression;
    }
}
