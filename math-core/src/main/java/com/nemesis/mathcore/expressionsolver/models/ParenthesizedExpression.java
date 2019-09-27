package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;

import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.difference;
import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.sum;
import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.models.Sign.MINUS;

public class ParenthesizedExpression extends Expression {

    public ParenthesizedExpression(Term term, ExpressionOperator operator, Expression subExpression) {
        super(term, operator, subExpression);
    }

    public ParenthesizedExpression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        super(term, operator, subExpression);
        super.sign = sign;
    }

    public ParenthesizedExpression(Sign sign, Term term) {
        super(term);
        super.sign = sign;
    }

    public ParenthesizedExpression(Term term) {
        super(term);
    }

    public ParenthesizedExpression(Sign sign, Expression expression) {
        super(expression.getTerm(), expression.getOperator(), expression.getSubExpression());
        super.sign = sign;
    }

    @Override
    public Component getDerivative() {
        Component derivative = super.getDerivative();
        Term d;
        if (derivative instanceof Term) {
            d = (Term) derivative;
        } else {
            d = new Term((Factor) derivative);
        }
        return new ParenthesizedExpression(sign, d);
    }

    @Override
    public String simplify() {
        String simplifiedExpr;
        switch (operator) {
            case NONE:
                simplifiedExpr = term.simplify();
                break;
            case SUM:
                simplifiedExpr = ExpressionBuilder.sum(term.simplify(), subExpression.simplify());
                break;
            case SUBSTRACT:
                simplifiedExpr = difference(term.simplify(), subExpression.simplify());
                break;
            default:
                throw new RuntimeException("Unexpected expression operator [" + operator + "]");
        }
        return "(" + simplifiedExpr + ")";
    }

    @Override
    public String toString() {
        String signChar = sign.equals(MINUS) ? "-" : "";
        if (subExpression == null) {
            return signChar + "(" + term + ")";
        } else {
            if (operator.equals(SUM)) {
                return signChar + "(" + sum(term.toString(), subExpression.toString() + ")");
            } else if (operator.equals(SUBSTRACT)) {
                return signChar + "(" + difference(term.toString(), subExpression.toString()) + ")";
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }
}
