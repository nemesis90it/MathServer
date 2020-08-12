package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.NONE;

public abstract class WrappedExpression extends Base{

    protected Expression expression;

    public WrappedExpression(Term term, ExpressionOperator operator, Expression subExpression) {
        expression = new Expression(term, operator, subExpression);
    }

    public WrappedExpression(Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        expression = new Expression(term, operator, subExpressionAsTerm);
    }

    public WrappedExpression(Sign sign, Term term, ExpressionOperator operator, Expression subExpression) {
        expression = new Expression(term, operator, subExpression);
        super.sign = sign;
    }

    public WrappedExpression(Sign sign, Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        expression = new Expression(term, operator, subExpressionAsTerm);
        super.sign = sign;
    }

    public WrappedExpression(Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        expression = new Expression(new Term(leftFactor), operator, new Term(rightFactor));
        super.sign = Sign.PLUS;
    }

    public WrappedExpression(Sign sign, Factor leftFactor, ExpressionOperator operator, Factor rightFactor) {
        expression = new Expression(new Term(leftFactor), operator, new Term(rightFactor));
        super.sign = sign;
    }

    public WrappedExpression(Sign sign, Term term) {
        expression = new Expression(term);
        super.sign = sign;
    }

    public WrappedExpression(Sign sign, Factor factor) {
        expression = new Expression(new Term(factor));
        super.sign = sign;
    }

    public WrappedExpression(Factor factor) {
        expression = new Expression(new Term(factor));
    }

    public WrappedExpression(Term term) {
        expression = new Expression(term);
    }

    public WrappedExpression(Sign sign, Expression expr) {
        expression = new Expression(expr.getTerm(), expr.getOperator(), expr.getSubExpression());
        super.sign = sign;
    }

    public WrappedExpression(Expression expression) {
        this.expression = expression;
    }

    public WrappedExpression() {
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
    public Domain getDomain(Variable variable) {
        return this.expression.getDomain(variable);
    }

    @Override
    public Set<Variable> getVariables() {
        return expression.getVariables();
    }

    @Override
    public boolean contains(TermOperator termOperator) {
        return this.expression.contains(termOperator);
    }

    @Override
    public boolean contains(Variable variable) {
        return this.expression.contains(variable);
    }

}
