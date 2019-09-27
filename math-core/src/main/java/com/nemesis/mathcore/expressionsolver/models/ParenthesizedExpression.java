package com.nemesis.mathcore.expressionsolver.models;

import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.difference;
import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.sum;
import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.models.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.IS_ZERO_REGEXP;

public class ParenthesizedExpression extends Expression {

    public ParenthesizedExpression(Term term, ExpressionOperator operator, Expression subExpression) {
        super(term, operator, subExpression);
    }

    public ParenthesizedExpression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        super(sign, term, operator, subExpression);
    }

    public ParenthesizedExpression(Sign sign, Expression absExpression) {
        super(sign, absExpression);
    }

    public ParenthesizedExpression(Sign sign, Term term) {
        super(sign, term);
    }

    public ParenthesizedExpression(Term term) {
        super(term);
    }

    @Override
    public String getDerivative() {

        String derivative;
        String signChar = sign.equals(MINUS) ? "-" : "";
        String termDerivative = term.getDerivative();

        if (termDerivative.matches(IS_ZERO_REGEXP)) {
            termDerivative = "";
        }

        if (subExpression == null) {
            derivative = termDerivative;
        } else {
            String subExprDerivative = subExpression.getDerivative();
            if (operator.equals(SUM)) {
                derivative = sum(termDerivative, subExprDerivative);
            } else if (operator.equals(SUBSTRACT)) {
                derivative = difference(termDerivative, subExprDerivative);
            } else {
                throw new RuntimeException("Unexpected operator [" + operator + "]");
            }
        }
        return signChar + "(" + derivative + ")";
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
