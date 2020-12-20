package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.stringbuilder.LatexBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.*;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public class AbsExpression extends WrappedExpression {

    public AbsExpression(Term term, ExpressionOperator operator, Expression subExpression) {
        super(term, operator, subExpression);
    }

    public AbsExpression(Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        super(term, operator, subExpressionAsTerm);
    }

    public AbsExpression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        super(sign, term, operator, subExpression);
    }

    public AbsExpression(Sign sign, Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        super(sign, term, operator, subExpressionAsTerm);
    }

    public AbsExpression(Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        super(leftFactor, operator, rightFactor);
    }

    public AbsExpression(Sign sign, Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        super(sign, leftFactor, operator, rightFactor);
    }

    public AbsExpression(Sign sign, Term term) {
        super(sign, term);
    }

    public AbsExpression(Sign sign, Factor factor) {
        super(sign, factor);
    }

    public AbsExpression(Factor factor) {
        super(factor);
    }

    public AbsExpression(Term term) {
        super(term);
    }

    public AbsExpression(Sign sign, Expression expr) {
        super(sign, expr);
    }

    public AbsExpression(Expression expression) {
        super(expression);
    }

    public AbsExpression() {
        super();
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
        return ExpressionBuilder.addSign(sign.toString(), ExpressionBuilder.toAbsExpression(content));
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
        return LatexBuilder.addSign(sign.toString(), ExpressionBuilder.toAbsExpression(content));
    }

    @Override
    public Component getDerivative(Variable var) {
        throw new UnsupportedOperationException("Abs expression is not derivable");
    }

    @Override
    public AbsExpression getClone() {
        return new AbsExpression(super.sign, super.getExpression().getClone());
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Infinity i) {
            return i.getSign() == PLUS ? -1 : 1;
        } else if (c instanceof AbsExpression e) {
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

    @Override
    public Classifier classifier() {
        return new AbsExpressionClassifier(this.getExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbsExpression that = (AbsExpression) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class AbsExpressionClassifier extends Factor.Classifier {

        private Expression expression;

        public AbsExpressionClassifier(Expression expression) {
            super(ParenthesizedExpression.class);
            this.expression = expression;
        }
    }

}
