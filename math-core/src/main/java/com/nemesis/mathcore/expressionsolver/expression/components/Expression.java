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
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class Expression extends Component {

    protected Term term;
    protected ExpressionOperator operator;
    protected Expression subExpression;

    public Expression(Term term, ExpressionOperator operator, Expression subExpression) {

        if (operator.equals(SUBTRACT)) {
            subExpression.getTerm().setFactor(ComponentUtils.cloneAndChangeSign(subExpression.getTerm().getFactor()));
            operator = SUM;
        }

        if (isZero(term)) {
            term = subExpression.getTerm();
            subExpression = subExpression.getSubExpression();
        }

        this.term = term;
        this.operator = operator;
        this.subExpression = subExpression;
    }

    public Expression(Term term, ExpressionOperator operator, Term subExpressionAsTerm) {

        if (operator.equals(SUBTRACT)) {
            subExpressionAsTerm.setFactor(ComponentUtils.cloneAndChangeSign(subExpressionAsTerm.getFactor()));
            operator = SUM;
        }

        if (isZero(term)) {
            this.term = subExpressionAsTerm;
            this.operator = NONE;
            this.subExpression = null;
        } else {
            this.term = term;
            this.operator = operator;
            this.subExpression = new Expression(subExpressionAsTerm);
        }

    }

    public Expression(Term term) {
        this.term = term;
        this.operator = NONE;
    }

    public Expression() {
    }

    private static boolean isZero(Component component) {
        return component.isScalar() && component.getValue().compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public BigDecimal getValue() {
        if (value == null) {
            switch (operator) {
                case NONE:
                    value = term.getValue();
                    break;
                case SUM:
                    value = term.getValue().add(subExpression.getValue());
                    break;
                case SUBTRACT:
                    throw new IllegalStateException("SUBTRACT must be considered as SUM with negative number");
                    //                value = term.getValue().subtract(subExpression.getValue());
                    //                break;
                default:
                    throw new IllegalArgumentException("Illegal expression operator '" + operator + "'");
            }
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
            Term td = Term.getSimplestTerm(termDerivative);
            Term ed = Term.getSimplestTerm(subExprDerivative);
            return new Expression(td, operator, ed);
        }
    }

    @Override
    public Component rewrite(Rule rule) {
        this.setTerm(Term.getSimplestTerm(this.getTerm().rewrite(rule)));
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
            }
            throw new RuntimeException("Unexpected operator [" + operator + "]");
        }
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public void setOperator(ExpressionOperator operator) {
        this.operator = operator;
    }

    public void setSubExpression(Expression subExpression) {
        this.subExpression = subExpression;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
