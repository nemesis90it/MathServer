package com.nemesis.mathcore.expressionsolver.expression.components;

    /*
          Expression ::= Term + Expression
          Expression ::= Term - Expression
          Expression ::= Term
    */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
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
        // TODO: remove sign
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
    public Component getDerivative() {

        Component termDerivative = term.getDerivative();
        if (subExpression == null) {
            return termDerivative;
        } else {
            Component subExprDerivative = subExpression.getDerivative();
            Term td = termDerivative instanceof Term ? (Term) termDerivative : new Term((Factor) termDerivative);
            Term ed = subExprDerivative instanceof Term ? (Term) subExprDerivative : new Term((Factor) subExprDerivative);
            return new Expression(td, operator, new Expression(ed));
        }
    }

    @Override
    public Component simplify() {
        throw new UnsupportedOperationException();
    }


//    @Override
//    public Term simplify() {
//        String simplified;
//        switch (operator) {
//            case NONE:
//                simplified = term.simplify();
//                break;
//            case SUM:
//                simplified = ExpressionBuilder.sum(term.simplify(), subExpression.simplify());
//                break;
//            case SUBSTRACT:
//                simplified = ExpressionBuilder.difference(term.simplify(), subExpression.simplify());
//                break;
//            default:
//                throw new RuntimeException("Unexpected expression operator [" + operator + "]");
//        }
//        if (simplified.contains("x")) {
//            return simplified;
//        } else {
//            return String.valueOf(ExpressionParser.evaluate(simplified));
//        }
//    }

    @Override
    public String toString() {
        String signChar = sign.equals(MINUS) ? "-" : "";
        if (subExpression == null) {
            return signChar + term;
        } else {
            if (operator.equals(SUM)) {
                return ExpressionBuilder.addSign(signChar, ExpressionBuilder.sum(term.toString(), subExpression.toString()));
            } else if (operator.equals(SUBSTRACT)) {
                return ExpressionBuilder.addSign(signChar, ExpressionBuilder.difference(term.toString(), subExpression.toString()));
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }
}
