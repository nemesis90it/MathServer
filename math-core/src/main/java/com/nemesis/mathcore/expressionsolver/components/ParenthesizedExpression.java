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
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;


public class ParenthesizedExpression extends WrappedExpression {

    public ParenthesizedExpression(Term term, ExpressionOperator operator, Expression subExpression) {
        super(term, operator, subExpression);
    }

    public ParenthesizedExpression(Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        super(term, operator, subExpressionAsTerm);
    }

    public ParenthesizedExpression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        super(sign, term, operator, subExpression);
    }

    public ParenthesizedExpression(Sign sign, Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        super(sign, term, operator, subExpressionAsTerm);
    }

    public ParenthesizedExpression(Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        super(leftFactor, operator, rightFactor);
    }

    public ParenthesizedExpression(Sign sign, Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        super(sign, leftFactor, operator, rightFactor);
    }

    public ParenthesizedExpression(Sign sign, Term term) {
        super(sign, term);
    }

    public ParenthesizedExpression(Sign sign, Factor factor) {
        super(sign, factor);
    }

    public ParenthesizedExpression(Factor factor) {
        super(factor);
    }

    public ParenthesizedExpression(Term term) {
        super(term);
    }

    public ParenthesizedExpression(Sign sign, Expression expr) {
        super(sign, expr);
    }

    public ParenthesizedExpression(Expression expression) {
        super(expression);
    }

    public ParenthesizedExpression() {
        super();
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal expressionValue = expression.getValue();
        return sign.equals(PLUS) ? expressionValue : expressionValue.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public Component getDerivative(Variable var) {
        Component derivative = expression.getDerivative(var);
        return new ParenthesizedExpression(sign, Term.getTerm(derivative));
    }

    @Override
    public ParenthesizedExpression getClone() {
        return new ParenthesizedExpression(sign, expression.getClone());
    }

    @Override
    public String toString() {

        Term term = expression.getTerm();
        ExpressionOperator operator = expression.getOperator();
        Expression subExpression = expression.getSubExpression();

        String content;
        if (subExpression == null) {
            content = term.toString();
        } else {
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
        if (sign.equals(MINUS)) {
            return "-(" + content + ")";
        } else {
            return content;
        }
    }

    @Override
    public String toLatex() {

        Term term = expression.getTerm();
        ExpressionOperator operator = expression.getOperator();
        Expression subExpression = expression.getSubExpression();

        String content;
        String termAsLatex = term.toLatex();

        if (subExpression == null) {
            content = termAsLatex;
        } else {
            String subExpressionAsLatex = subExpression.toLatex();
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
        if (sign.equals(MINUS)) {
            return "-(" + content + ")";
        } else {
            return content;
        }
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Infinity i) {
            return i.getSign() == PLUS ? -1 : 1;
        } else if (c instanceof ParenthesizedExpression e) {
            Comparator<ParenthesizedExpression> exprComparator = Comparator.comparing(ParenthesizedExpression::getExpression);
            Comparator<ParenthesizedExpression> comparator = exprComparator.thenComparing(ParenthesizedExpression::getSign);
            return comparator.compare(this, e);
        } else if (c instanceof Base b) {
            return Base.compare(this, b);
        } else if (c instanceof Exponential e) {
            return new Exponential(this, new Constant(1)).compareTo(e);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }

    @Override
    public Classifier classifier() {
        return new ParExpressionClassifier(this.getExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ParenthesizedExpression that = (ParenthesizedExpression) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class ParExpressionClassifier extends Factor.Classifier {

        private Expression expression;

        public ParExpressionClassifier(Expression expression) {
            super(ParenthesizedExpression.class);
            this.expression = expression;
        }
    }

}
