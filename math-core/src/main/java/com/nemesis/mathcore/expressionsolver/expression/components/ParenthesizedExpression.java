package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.Comparator;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;


public class ParenthesizedExpression extends Base {

    private Expression expression;

    public ParenthesizedExpression(Term term, ExpressionOperator operator, Expression subExpression) {
        expression = new Expression(term, operator, subExpression);
    }

    public ParenthesizedExpression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        expression = new Expression(term, operator, subExpression);
        super.sign = sign;
    }

    public ParenthesizedExpression(Sign sign, Term term) {
        expression = new Expression(term);
        super.sign = sign;
    }

    public ParenthesizedExpression(Term term) {
        expression = new Expression(term);
    }

    public ParenthesizedExpression(Sign sign, Expression expr) {
        expression = new Expression(expr.getTerm(), expr.getOperator(), expr.getSubExpression());
        super.sign = sign;
    }

    public ParenthesizedExpression(Expression expression) {
        this.expression = expression;
    }

    public ParenthesizedExpression() {
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Term getTerm() {
        return expression.getTerm();
    }

    public ExpressionOperator getOperator() {
        return expression.getOperator();
    }

    public Expression getSubExpression() {
        return expression.getSubExpression();
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal absValue = expression.getValue();
        return sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public Component getDerivative() {
        Component derivative = expression.getDerivative();
        return new ParenthesizedExpression(sign, ComponentUtils.getTerm(derivative));
    }

    @Override
    public Component rewrite(Rule rule) {
        expression.setTerm(ComponentUtils.getTerm(expression.getTerm().rewrite(rule)));
        if (expression.getSubExpression() != null) {
            expression.setSubExpression(ComponentUtils.getExpression(expression.getSubExpression().rewrite(rule)));
        }
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.expression.isScalar();
    }

    @Override
    public String toString() {

        Term term = expression.getTerm();
        Expression subExpression = expression.getSubExpression();

        String content;
        if (subExpression == null) {
            content = term.toString();
        } else {
            ExpressionOperator operator = expression.getOperator();
            if (operator.equals(SUM)) {
                content = ExpressionBuilder.sum(term.toString(), subExpression.toString());
            } else if (operator.equals(SUBTRACT)) {
                content = ExpressionBuilder.difference(term.toString(), subExpression.toString());
            } else {
                throw new RuntimeException("Unexpected operator [" + operator + "]");
            }
        }
        if (sign.equals(MINUS)) {
            return "-(" + content + ")";
        } else {
            return content;
        }
    }

    public Expression getExpression() {
        return expression;
    }

    public void setTerm(Term term) {
        this.expression.setTerm(term);
    }


    public void setOperator(ExpressionOperator operator) {
        this.expression.setOperator(operator);
    }

    public void setSubExpression(Expression subExpression) {
        this.expression.setSubExpression(subExpression);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ParenthesizedExpression) {
            Comparator<ParenthesizedExpression> exprComparator = Comparator.comparing(ParenthesizedExpression::getExpression);
            Comparator<ParenthesizedExpression> comparator = exprComparator.thenComparing(ParenthesizedExpression::getSign);
            return comparator.compare(this, (ParenthesizedExpression) o);
        } else {
            return Base.compare(this, o);
        }
    }

}
