package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.difference;
import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.sum;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBSTRACT;
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
        } else {
            d = new Term((Factor) derivative);
        }
        return new ParenthesizedExpression(sign, d);
    }

    @Override
    public Component simplify() {
        throw new UnsupportedOperationException();
    }


//    @Override
//    public Term simplify() {
//        String simplifiedExpr;
//        switch (operator) {
//            case NONE:
//                simplifiedExpr = term.simplify();
//                break;
//            case SUM:
//                simplifiedExpr = ExpressionBuilder.sum(term.simplify(), subExpression.simplify());
//                break;
//            case SUBSTRACT:
//                simplifiedExpr = difference(term.simplify(), subExpression.simplify());
//                break;
//            default:
//                throw new RuntimeException("Unexpected expression operator [" + operator + "]");
//        }
//        return "(" + simplifiedExpr + ")";
//    }

    @Override
    public String toString() {

        Term term = expression.getTerm();
        String signChar = sign.equals(MINUS) ? "-" : "";
        Expression subExpression = expression.getSubExpression();

        if (subExpression == null) {
            return signChar + "(" + term + ")";
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
}
