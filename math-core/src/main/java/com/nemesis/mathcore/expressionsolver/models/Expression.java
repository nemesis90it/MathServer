package com.nemesis.mathcore.expressionsolver.models;

    /*
          Expression ::= Term + Expression
          Expression ::= Term - Expression
          Expression ::= Term
    */

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.ExpressionBuilder.*;
import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.models.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.IS_ZERO_REGEXP;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class Expression extends Factor {

    protected Term term;
    protected ExpressionOperator operator;
    protected Expression subExpression;

    public Expression(Term term, ExpressionOperator operator, Expression subExpression) {
        this.term = term;
        this.operator = operator;
        this.subExpression = subExpression;
    }

    public Expression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        super.sign = sign;
        this.term = term;
        this.operator = operator;
        this.subExpression = subExpression;
    }

    public Expression(Sign sign, Expression absExpression) {
        this.sign = sign;
        this.term = absExpression.getTerm();
        this.operator = absExpression.getOperator();
        this.subExpression = absExpression.getSubExpression();
    }

    public Expression(Sign sign, Term term) {
        this.sign = sign;
        this.term = term;
        this.operator = ExpressionOperator.NONE;
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
            BigDecimal absValue;
            switch (operator) {
                case NONE:
                    absValue = term.getValue();
                    break;
                case SUM:
                    absValue = term.getValue().add(subExpression.getValue());
                    break;
                case SUBSTRACT:
                    absValue = term.getValue().subtract(subExpression.getValue());
                    break;
                default:
                    throw new RuntimeException("Illegal expression operator '" + operator + "'");
            }
            value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
        }
        return value;

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
            derivative = signChar + termDerivative;
        } else {
            String subExprDerivative = subExpression.getDerivative();
            if (operator.equals(SUM)) {
                derivative = addSign(signChar, sum(termDerivative, subExprDerivative));
            } else if (operator.equals(SUBSTRACT)) {
                derivative = addSign(signChar, difference(termDerivative, subExprDerivative));
            } else {
                throw new RuntimeException("Unexpected operator [" + operator + "]");
            }
        }

        if (derivative.length() == 0) {
            derivative = "0";
        }
        return derivative;
    }

    @Override
    public String toString() {
        String signChar = sign.equals(MINUS) ? "-" : "";
        if (subExpression == null) {
            return signChar + term;
        } else {
            if (operator.equals(SUM)) {
                return addSign(signChar, sum(term.toString(), subExpression.toString()));
            } else if (operator.equals(SUBSTRACT)) {
                return addSign(signChar, difference(term.toString(), subExpression.toString()));
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }
}
