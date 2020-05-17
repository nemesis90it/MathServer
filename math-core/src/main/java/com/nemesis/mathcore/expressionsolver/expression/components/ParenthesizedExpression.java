package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.*;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;


public class ParenthesizedExpression extends Base {

    private Expression expression;

    public ParenthesizedExpression(Term term, ExpressionOperator operator, Expression subExpression) {
        expression = new Expression(term, operator, subExpression);
    }

    public ParenthesizedExpression(Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        expression = new Expression(term, operator, subExpressionAsTerm);
    }

    public ParenthesizedExpression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        expression = new Expression(term, operator, subExpression);
        super.sign = sign;
    }

    public ParenthesizedExpression(Sign sign, Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        expression = new Expression(term, operator, subExpressionAsTerm);
        super.sign = sign;
    }

    public ParenthesizedExpression(Sign sign, Term term) {
        expression = new Expression(term);
        super.sign = sign;
    }

    public ParenthesizedExpression(Sign sign, Factor factor) {
        expression = new Expression(new Term(factor));
        super.sign = sign;
    }

    public ParenthesizedExpression(Factor factor) {
        expression = new Expression(new Term(factor));
    }

    public ParenthesizedExpression(Term term) {
        expression = new Expression(term);
    }

    public ParenthesizedExpression(Sign sign, Expression expr) {
        expression = new Expression(expr.getTerm(), expr.getOperator(), expr.getSubExpression());
        super.sign = sign;
    }

    public ParenthesizedExpression(Expression expression) {
        this.expression = expression;
    }

    public ParenthesizedExpression() {
    }

    public void setTerm(Term term) {
        this.expression.setTerm(term);
    }

    public void setOperator(ExpressionOperator operator) {
        this.expression.setOperator(operator);
    }

    public void setSubExpression(Expression subExpression) {
        this.expression.setSubExpression(subExpression);
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
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

    public Expression getExpression() {
        return expression;
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal absValue = expression.getValue();
        return sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public Component getDerivative(char var) {
        Component derivative = expression.getDerivative(var);
        return new ParenthesizedExpression(sign, Term.getTerm(derivative));
    }

    @Override
    public Component rewrite(Rule rule) {
        Component rewrittenTerm = expression.getTerm().rewrite(rule);
        expression.setTerm(Term.getTerm(rewrittenTerm));
        if (expression.getSubExpression() != null) {
            expression.setSubExpression(ComponentUtils.getExpression(expression.getSubExpression().rewrite(rule)));
        }
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.expression.isScalar();
    }

    @Override
    public Constant getValueAsConstant() {
        if (this.getOperator() == NONE) {
            return this.getTerm().getValueAsConstant();
        }
        return new ConstantFunction(this);
    }

    @Override
    public String toString() {

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
        if (sign.equals(MINUS)) {
            return "-(" + content + ")";
        } else {
            return content;
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ParenthesizedExpression) {
            Comparator<ParenthesizedExpression> exprComparator = Comparator.comparing(ParenthesizedExpression::getExpression);
            Comparator<ParenthesizedExpression> comparator = exprComparator.thenComparing(ParenthesizedExpression::getSign);
            return comparator.compare(this, (ParenthesizedExpression) o);
        } else {
            return Base.compare(this, o);
        }
    }

    @Override
    public boolean contains(TermOperator termOperator) {
        return this.getExpression().contains(termOperator);
    }

    @Override
    public Classifier classifier() {
        return new ExpressionClassifier(this.getExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParenthesizedExpression that = (ParenthesizedExpression) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class ExpressionClassifier extends Factor.Classifier {

        private Expression expression;

        public ExpressionClassifier(Expression expression) {
            super(ParenthesizedExpression.class);
            this.expression = expression;
        }
    }

}
