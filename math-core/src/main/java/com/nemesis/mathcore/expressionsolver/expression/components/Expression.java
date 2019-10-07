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
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;

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
        if (value == null) {
            BigDecimal value;
            switch (operator) {
                case NONE:
                    value = term.getValue();
                    break;
                case SUM:
                    value = term.getValue().add(subExpression.getValue());
                    break;
                case SUBSTRACT:
                    value = term.getValue().subtract(subExpression.getValue());
                    break;
                default:
                    throw new RuntimeException("Illegal expression operator '" + operator + "'");
            }
            this.value = value;
        }
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
        Component simplifiedTerm = term.simplify();

        if (this.operator == ExpressionOperator.NONE) {
            return simplifiedTerm;
        } else {
            Component simplifiedSubExpression;
            simplifiedSubExpression = subExpression.simplify();

            Monomial leftMonomial = Monomial.getMonomial(simplifiedTerm);
            Monomial rightMonomial = Monomial.getMonomial(simplifiedSubExpression);

            Term defaultTerm = ComponentUtils.getTerm(simplifiedTerm);
            Expression defaultSubExpression = ComponentUtils.getExpression(simplifiedSubExpression);

            Expression defaultExpression = new Expression(defaultTerm, operator, defaultSubExpression);

            if (leftMonomial != null && rightMonomial != null) {
                Term simplifiedExpression;
                if (this.operator == SUM) {
                    simplifiedExpression = Monomial.sum(rightMonomial, leftMonomial);
                } else if (this.operator == SUBSTRACT) {
                    simplifiedExpression = Monomial.subtract(rightMonomial, leftMonomial);
                } else {
                    throw new RuntimeException("Unexpected operator [" + this.operator + "]");
                }
                return Objects.requireNonNullElse(simplifiedExpression, defaultExpression);
            }
            return defaultExpression;
        }
    }

    @Override
    public String toString() {

        if (subExpression == null) {
            return term.toString();
        } else {
            if (operator.equals(SUM)) {
                return ExpressionBuilder.sum(term.toString(), subExpression.toString());
            } else if (operator.equals(SUBSTRACT)) {
                return ExpressionBuilder.difference(term.toString(), subExpression.toString());
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }
}
