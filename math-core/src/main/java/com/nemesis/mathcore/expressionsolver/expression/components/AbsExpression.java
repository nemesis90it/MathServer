package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.LatexBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.*;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class AbsExpression extends ParenthesizedExpression {

    public AbsExpression(Sign sign, Expression component) {
        super(sign, component);
    }

    @Override
    public BigDecimal getValue() {
        final Expression expression = super.getExpression();
        BigDecimal expressionAbsValue = expression.getValue().abs();
        return sign.equals(PLUS) ? expressionAbsValue : expressionAbsValue.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public String toString() {

        final Expression expression = super.getExpression();

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
            } else if (operator.equals(NONE)) {
                content = term.toString();
            } else {
                throw new RuntimeException("Unexpected operator [" + operator + "]");
            }
        }
        String signChar = sign.equals(MINUS) ? "-" : "";
        return signChar + "|" + content + "|";
    }

    @Override
    public String toLatex() {

        final Expression expression = super.getExpression();

        Term term = expression.getTerm();
        Expression subExpression = expression.getSubExpression();

        String content;
        String termAsLatex = term.toLatex();

        if (subExpression == null) {
            content = termAsLatex;
        } else {
            String subExpressionAsLatex = subExpression.toLatex();
            ExpressionOperator operator = expression.getOperator();
            if (operator.equals(SUM)) {
                content = LatexBuilder.sum(termAsLatex, subExpressionAsLatex);
            } else if (operator.equals(SUBTRACT)) {
                content = LatexBuilder.difference(termAsLatex, subExpressionAsLatex);
            } else if (operator.equals(NONE)) {
                content = termAsLatex;
            } else {
                throw new RuntimeException("Unexpected operator [" + operator + "]");
            }
        }
        String signChar = sign.equals(MINUS) ? "-" : "";
        return signChar + "|" + content + "|";
    }
}
