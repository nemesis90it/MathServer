package com.nemesis.mathcore.expressionsolver.models;

    /*
          Expression ::= Term + Expression
          Expression ::= Term - Expression
          Expression ::= Term
    */

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class Expression extends Factor {

    private Term term;
    private ExpressionOperator operator;
    private Expression subExpression;

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
    public String toString() {
        if (subExpression == null) {
            if (sign.equals(PLUS)) {
                return "" + term;
            } else {
                return sign + "(" + term + ")";
            }
        } else {
            if (sign.equals(PLUS)) {
                return "(" + term + ")" + operator + "(" + subExpression + ")";
            } else {
                return sign + "((" + term + ")" + operator + "(" + subExpression + "))";
            }
        }
    }
}
