package com.nemesis.mathcore.expressionsolver.expression.components;

    /*
          Expression ::= Term + Expression
          Expression ::= Term - Expression
          Expression ::= Term
    */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.*;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isZero;

@Data
public class Expression extends Component {

    protected Term term;
    protected ExpressionOperator operator;
    protected Expression subExpression;

    public Expression(Term term, ExpressionOperator operator, Expression subExpression) {
        this.build(term, operator, subExpression);
    }

    public Expression(Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        this.build(term, operator, new Expression(subExpressionAsTerm));
    }

    public Expression(Term term) {
        this.build(term, NONE, null);
    }

    public Expression() {
    }

    public void setTerm(Term term) {
        this.build(term, this.operator, this.subExpression);
    }

    public void setOperator(ExpressionOperator operator) {
        if (this.operator == NONE || this.operator == null) {
            this.operator = operator; // Expression is in building state
        } else {
            this.build(this.term, operator, this.subExpression);
        }
    }

    public void setSubExpression(Expression subExpression) {
        this.build(this.term, this.operator, subExpression);
    }

    private void build(Term term, ExpressionOperator operator, Expression subExpression) {

        if (operator == null) {
            operator = NONE;
        }

        if (operator.equals(SUBTRACT)) {
            final Factor originalFactor = subExpression.getTerm().getFactor();
            subExpression.getTerm().setFactor(ComponentUtils.cloneAndChangeSign(originalFactor));
            operator = SUM;
        }

        if ((term == null || isZero(term)) && (subExpression == null || isZero(subExpression))) {
            this.term = new Term(new Constant(0));
            this.operator = NONE;
            this.subExpression = null;
        } else if (term == null || isZero(term)) {
            this.build(subExpression.getTerm(), subExpression.getOperator(), subExpression.getSubExpression());
        } else if (subExpression == null || isZero(subExpression)) {
            this.term = term;
            this.operator = NONE;
            this.subExpression = null;
        } else {
            this.term = term;
            this.operator = operator;
            this.subExpression = subExpression;
        }
    }

    @Override
    public BigDecimal getValue() {
        switch (operator) {
            case NONE -> value = term.getValue();
            case SUM -> value = term.getValue().add(subExpression.getValue());
            case SUBTRACT -> throw new IllegalStateException("SUBTRACT must be considered as SUM with negative number");
            default -> throw new IllegalArgumentException("Illegal expression operator '" + operator + "'");
        }
        return value;
    }

    @Override
    public Component getDerivative(char var) {

        Component termDerivative = term.getDerivative(var);
        if (subExpression == null) {
            return termDerivative;
        } else {
            Component subExprDerivative = subExpression.getDerivative(var);
            Term td = Term.getTerm(termDerivative);
            Term ed = Term.getTerm(subExprDerivative);
            return new Expression(td, operator, ed);
        }
    }

    @Override
    public Component rewrite(Rule rule) {
        this.setTerm(Term.getTerm(this.getTerm().rewrite(rule)));
        if (this.getSubExpression() != null) {
            this.setSubExpression(ComponentUtils.getExpression(this.getSubExpression().rewrite(rule)));
        }
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return term.isScalar() && (this.subExpression == null || this.subExpression.isScalar());
    }

    @Override
    public Constant getValueAsConstant() {

        if (!this.isScalar()) {
            throw new NoValueException("This component is not a scalar");
        }

        ConstantFunction thisAsConstantFunction = Factor.getFactorOfSubtype(this, ConstantFunction.class);
        if (thisAsConstantFunction != null) {
            return thisAsConstantFunction;
        } else {
            BigDecimal value = this.getValue();
            if (!MathUtils.isIntegerValue(value) && MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
                return new ConstantFunction(this);
            } else {
                return new Constant(value);
            }
        }
    }

    @Override
    public Expression getClone() {
        return new Expression(term.getClone(), operator, subExpression != null ? subExpression.getClone() : null);
    }

    @Override
    public Domain getDomain(Variable variable) {
        Domain domain = new Domain();
        if (term.contains(variable)) {
            domain.addIntervals(term.getDomain(variable).getIntervals());
        }
        if (subExpression != null && subExpression.contains(variable)) {
            domain.addIntervals(subExpression.getDomain(variable).getIntervals());
        }
        return domain;
    }

    @Override
    public String toString() {

        String termAsString = term.toString();
        if (subExpression == null) {
            return termAsString;
        } else {
            if (term.getOperator() == TermOperator.NONE && term.getFactor() instanceof ParenthesizedExpression) {
                termAsString = "(" + termAsString + ")";
            }
            if (operator.equals(SUM)) {
                return ExpressionBuilder.sum(termAsString, subExpression.toString());
            } else if (operator.equals(SUBTRACT)) {
                return ExpressionBuilder.difference(termAsString, subExpression.toString());
            } else if (operator.equals(NONE)) {
                return termAsString;
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }


    @Override
    public String toLatex() {

        String termAsLatex = term.toLatex();
        if (subExpression == null) {
            return termAsLatex;
        } else {
            if (term.getOperator() == TermOperator.NONE && term.getFactor() instanceof ParenthesizedExpression) {
                termAsLatex = "(" + termAsLatex + ")";
            }
            final String subExpressionAsLatex = subExpression.toLatex();
            if (operator.equals(SUM)) {
                return ExpressionBuilder.sum(termAsLatex, subExpressionAsLatex);
            } else if (operator.equals(SUBTRACT)) {
                return ExpressionBuilder.difference(termAsLatex, subExpressionAsLatex);
            } else if (operator.equals(NONE)) {
                return termAsLatex;
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Expression expression) {
            Comparator<Expression> comparatorByTerm = Comparator.comparing(Expression::getTerm);
            if (this.subExpression != null) {
                Comparator<Expression> comparatorByTermAndSubExpression = comparatorByTerm.thenComparing(Expression::getSubExpression);
                Comparator<Expression> comparator = comparatorByTermAndSubExpression.thenComparing(Expression::getOperator);
                return comparator.compare(this, expression);
            } else {
                return comparatorByTerm.compare(this, expression);
            }
        } else if (c instanceof Exponential exponential) {
            return new Exponential(new ParenthesizedExpression(this), new Constant(1)).compareTo(exponential);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }

    @Override
    public boolean contains(TermOperator termOperator) {
        return this.getTerm().contains(termOperator) ||
                this.getSubExpression() != null && this.getSubExpression().contains(termOperator);
    }

    @Override
    public boolean contains(Variable variable) {
        return term.contains(variable) || subExpression.contains(variable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expression that = (Expression) o;
        return Objects.equals(term, that.term) &&
                operator == that.operator &&
                Objects.equals(subExpression, that.subExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, operator, subExpression);
    }
}
