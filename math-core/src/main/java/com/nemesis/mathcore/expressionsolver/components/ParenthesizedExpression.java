package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.stringbuilder.LatexBuilder;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.*;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
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

    public ParenthesizedExpression(Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        expression = new Expression(new Term(leftFactor), operator, new Term(rightFactor));
        super.sign = Sign.PLUS;
    }

    public ParenthesizedExpression(Sign sign, Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        expression = new Expression(new Term(leftFactor), operator, new Term(rightFactor));
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
        BigDecimal expressionValue = expression.getValue();
        return sign.equals(PLUS) ? expressionValue : expressionValue.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public Component getDerivative(Variable var) {
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
        } else {
            return super.getValueAsConstant();
        }
    }

    @Override
    public ParenthesizedExpression getClone() {
        return new ParenthesizedExpression(sign, expression.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        return this.expression.getDomain(variable);
    }

    @Override
    public Set<Variable> getVariables() {
        return expression.getVariables();
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
        if (c instanceof ParenthesizedExpression e) {
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
    public boolean contains(TermOperator termOperator) {
        return this.getExpression().contains(termOperator);
    }

    @Override
    public boolean contains(Variable variable) {
        return this.expression.contains(variable);
    }

    @Override
    public Classifier classifier() {
        return new ExpressionClassifier(this.getExpression());
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
    private static class ExpressionClassifier extends Factor.Classifier {

        private Expression expression;

        public ExpressionClassifier(Expression expression) {
            super(ParenthesizedExpression.class);
            this.expression = expression;
        }
    }

}
