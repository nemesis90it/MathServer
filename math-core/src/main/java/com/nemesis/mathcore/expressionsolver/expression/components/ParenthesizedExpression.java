package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.difference;
import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.sum;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.NONE;
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
        Term d;
        if (derivative instanceof Term) {
            d = (Term) derivative;
        } else if (derivative instanceof Expression) {
            d = new Term(new ParenthesizedExpression((Expression) derivative));
        } else {
            d = new Term((Factor) derivative);
        }
        return new ParenthesizedExpression(sign, d);
    }

    @Override
    public Component simplify() {
        Component simplifiedExpression = expression.simplify();
        if (simplifiedExpression instanceof Term) {
            Term simplifiedExpressionAsTerm = (Term) simplifiedExpression;
            if (!simplifiedExpressionAsTerm.getOperator().equals(NONE) && sign.equals(MINUS)) {
                return new ParenthesizedExpression(sign, simplifiedExpressionAsTerm);
            } else {
                return new Expression(simplifiedExpressionAsTerm);
            }
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
            } else if (operator.equals(SUBSTRACT)) {
                return signChar + "(" + difference(term.toString(), subExpression.toString()) + ")";
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }

    @Override
    public boolean absEquals(Object obj) {
        return obj instanceof ParenthesizedExpression && Objects.equals(this.expression, ((ParenthesizedExpression) obj).expression);
    }
}
