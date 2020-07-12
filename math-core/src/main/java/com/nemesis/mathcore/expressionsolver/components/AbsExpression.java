package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.stringbuilder.LatexBuilder;

import java.math.BigDecimal;
import java.util.Comparator;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.*;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class AbsExpression extends ParenthesizedExpression {

    public AbsExpression(Sign sign, Expression expression) {
        super(sign, expression);
    }

    public AbsExpression(Expression expression) {
        super(PLUS, expression);
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

    @Override
    public Component getDerivative(Variable var) {
        throw new UnsupportedOperationException("Abs expression is not derivable");
    }

    @Override
    public ParenthesizedExpression getClone() {
        return new AbsExpression(super.sign, super.getExpression().getClone());
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof AbsExpression e) {
            Comparator<AbsExpression> exprComparator = Comparator.comparing(AbsExpression::getExpression);
            Comparator<AbsExpression> comparator = exprComparator.thenComparing(AbsExpression::getSign);
            return comparator.compare(this, e);
        } else if (c instanceof Base b) {
            return compare(this, b);
        } else if (c instanceof Exponential e) {
            return new Exponential(this, new Constant(1)).compareTo(e);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }

}
