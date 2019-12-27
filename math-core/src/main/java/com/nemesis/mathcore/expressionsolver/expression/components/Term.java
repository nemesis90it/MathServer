package com.nemesis.mathcore.expressionsolver.expression.components;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class Term extends Component {

    private Factor factor;
    private TermOperator operator;
    private Term subTerm;

    public Term(Factor factor, TermOperator operator, Term subTerm) {
        this.factor = factor;
        this.operator = operator;
        this.subTerm = subTerm;
    }

    public Term(Factor factor) {
        this.factor = factor;
        this.operator = NONE;
    }

    @Override
    public BigDecimal getValue() {
        switch (operator) {
            case NONE:
                return factor.getValue();
            case DIVIDE:
                if (subTerm.getOperator().equals(DIVIDE)) { // Particular case: a/b/c = (a/b)/c
                    BigDecimal leftQuotient = MathUtils.divide(factor.getValue(), subTerm.getFactor().getValue());
                    return MathUtils.divide(leftQuotient, subTerm.getSubTerm().getValue());
                }
                return MathUtils.divide(factor.getValue(), subTerm.getValue());
            case MULTIPLY:
                return factor.getValue().multiply(subTerm.getValue());
            default:
                throw new RuntimeException("Illegal term operator '" + operator + "'");
        }
    }

    @Override
    public Component getDerivative(char var) {

        Component factorDerivative = this.factor.getDerivative(var);
        Component subTermDerivative;
        Factor fd;
        Term td;

        switch (operator) {
            case NONE:
                return factorDerivative;
            case DIVIDE:
                subTermDerivative = this.subTerm.getDerivative(var);
                fd = ComponentUtils.getFactor(factorDerivative);
                td = ComponentUtils.getTerm(subTermDerivative);
                return new Term(
                        new ParenthesizedExpression(
                                new Term(fd, MULTIPLY, subTerm),
                                SUBTRACT,
                                new Expression(new Term(factor, MULTIPLY, td))
                        ),
                        DIVIDE,
                        new Term(new Exponential(new ParenthesizedExpression(subTerm), new Constant("2")))
                );
            case MULTIPLY:
                subTermDerivative = this.subTerm.getDerivative(var);
                fd = ComponentUtils.getFactor(factorDerivative);
                td = ComponentUtils.getTerm(subTermDerivative);
                return new Expression(
                        new Term(fd, MULTIPLY, subTerm),
                        SUM,
                        new Expression(new Term(factor, MULTIPLY, td))
                );
            default:
                throw new RuntimeException("Unexpected operator");
        }

    }

    @Override
    public Component rewrite(Rule rule) {
        this.setFactor(ComponentUtils.getFactor(this.getFactor().rewrite(rule)));
        if (this.getSubTerm() != null) {
            this.setSubTerm(ComponentUtils.getTerm(this.getSubTerm().rewrite(rule)));
        }
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.factor.isScalar() && (this.subTerm == null || this.subTerm.isScalar());
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        if (subTerm == null) {
            return "" + factor;
        } else {
            String factorAsString = factor.toString();
            String termAsString = subTerm.toString();
            if (factor instanceof ParenthesizedExpression) {
                factorAsString = "(" + factorAsString + ")";
            }
            if (subTerm.getOperator() == NONE && subTerm.getFactor() instanceof ParenthesizedExpression) {
                termAsString = "(" + termAsString + ")";
            }
            if (operator.equals(DIVIDE)) {
                if (subTerm.getOperator() == NONE && subTerm.getFactor() instanceof ParenthesizedExpression) {
                    return ExpressionBuilder.division(factorAsString, termAsString);
                } else {
                    return ExpressionBuilder.division(factorAsString, termAsString);
                }
            } else if (operator.equals(MULTIPLY)) {
                return ExpressionBuilder.product(factorAsString, termAsString);
            }
        }
        throw new RuntimeException("Unexpected operator [" + operator + "]");
    }

}
