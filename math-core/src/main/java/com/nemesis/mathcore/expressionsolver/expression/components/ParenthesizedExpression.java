package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.difference;
import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.sum;
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
    public Component simplify() {

        /* Remove useless nesting, moving the inner parenthesized expression on top of the tree */
        if (expression.getOperator() == ExpressionOperator.NONE
                && expression.getTerm().getOperator() == TermOperator.NONE
                && expression.getTerm().getFactor() instanceof ParenthesizedExpression) {

            ParenthesizedExpression innerParExpression = (ParenthesizedExpression) expression.getTerm().getFactor();
            sign = !sign.equals(innerParExpression.getSign()) ? MINUS : PLUS;
            expression = innerParExpression.getExpression();
        }

        Expression expression;
        Sign sign = this.sign;

        if (sign == MINUS) {
            expression = ComponentUtils.applyConstantToExpression(this.expression, new Constant("-1"), TermOperator.MULTIPLY);
            sign = PLUS;
        } else {
            expression = this.expression;
        }

        /* Generic simplifications */
        Component simplifiedExpression = expression.simplify();
        if (simplifiedExpression instanceof Term) {
            return new Expression((Term) simplifiedExpression);
        } else if (simplifiedExpression instanceof Expression) {
            return new ParenthesizedExpression(sign, (Expression) simplifiedExpression);
        } else if (simplifiedExpression instanceof Factor) {
            return new ParenthesizedExpression(sign, new Expression(new Term((Factor) simplifiedExpression)));
        } else {
            throw new RuntimeException("Unexpected type [" + simplifiedExpression.getClass() + "] for simplified expression");
        }
    }

    @Override
    public String toString() {

        Term term = expression.getTerm();
        String signChar = sign.equals(MINUS) ? "-" : "";
        Expression subExpression = expression.getSubExpression();

        if (subExpression == null) {
            return ExpressionBuilder.addSign(signChar, term.toString());
        } else {
            ExpressionOperator operator = expression.getOperator();
            if (operator.equals(SUM)) {
                return signChar + "(" + sum(term.toString(), subExpression.toString() + ")");
            } else if (operator.equals(SUBTRACT)) {
                return signChar + "(" + difference(term.toString(), subExpression.toString()) + ")";
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }

    @Override
    public boolean absEquals(Object obj) {
        return obj instanceof ParenthesizedExpression && Objects.equals(this.expression, ((ParenthesizedExpression) obj).expression);
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
}
