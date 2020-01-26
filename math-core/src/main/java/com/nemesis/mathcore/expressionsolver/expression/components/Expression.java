package com.nemesis.mathcore.expressionsolver.expression.components;

    /*
          Expression ::= Term + Expression
          Expression ::= Term - Expression
          Expression ::= Term
    */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;

@Data
@EqualsAndHashCode(callSuper = false)
public class Expression extends Component {

    protected Term term;
    protected ExpressionOperator operator;
    protected Expression subExpression;

    public Expression(Term term, ExpressionOperator operator, Expression subExpression) {
        this.term = term;
        this.subExpression = subExpression;
        if (operator.equals(SUBTRACT)) {
            Term subTerm = this.subExpression.getTerm();
            subTerm.setFactor(ComponentUtils.cloneAndChangeSign(subTerm.getFactor()));
            this.operator = SUM;
        } else {
            this.operator = operator;
        }
    }

    public Expression(Term term, ExpressionOperator operator, Term subExpressionAsTerm) {
        this.term = term;
        this.subExpression = new Expression(subExpressionAsTerm);
        if (operator.equals(SUBTRACT)) {
            Term subTerm = this.subExpression.getTerm();
            subTerm.setFactor(ComponentUtils.cloneAndChangeSign(subTerm.getFactor()));
            this.operator = SUM;
        } else {
            this.operator = operator;
        }
    }

    public Expression(Term term) {
        this.term = term;
        this.operator = ExpressionOperator.NONE;
    }

    public Expression() {
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal value;
        switch (operator) {
            case NONE:
                value = term.getValue();
                break;
            case SUM:
                value = term.getValue().add(subExpression.getValue());
                break;
            case SUBTRACT:
                throw new RuntimeException("SUBTRACT must be considered as SUM with negative number");
//                value = term.getValue().subtract(subExpression.getValue());
//                break;
            default:
                throw new RuntimeException("Illegal expression operator '" + operator + "'");
        }
        this.value = value;
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
        return new Constant(this.getValue());
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
