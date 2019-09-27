package com.nemesis.mathcore.expressionsolver.models;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.models.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.models.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.models.TermOperator.MULTIPLY;

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
        this.operator = TermOperator.NONE;
    }

    public Factor getFactor() {
        return factor;
    }

    public TermOperator getOperator() {
        return operator;
    }

    public Term getSubTerm() {
        return subTerm;
    }

    @Override
    public BigDecimal getValue() {
        switch (operator) {
            case NONE:
                return factor.getValue();
            case DIVIDE:
                return MathUtils.divide(factor.getValue(), subTerm.getValue());
            case MULTIPLY:
                return factor.getValue().multiply(subTerm.getValue());
            default:
                throw new RuntimeException("Illegal term operator '" + operator + "'");
        }
    }

    @Override
    public Component getDerivative() {

        Component factorDerivative = this.factor.getDerivative();
        Component subTermDerivative;
        Factor fd;
        Term td;

        switch (operator) {

            case NONE:
                return factorDerivative;

            case DIVIDE:
                subTermDerivative = this.subTerm.getDerivative();
                fd = factorDerivative instanceof Factor ? (Factor) factorDerivative : new ParenthesizedExpression((Term) factorDerivative);
                td = subTermDerivative instanceof Term ? (Term) subTermDerivative : new Term((Factor) subTermDerivative);
                return new Term(
                        new Expression(
                                new Term(fd, MULTIPLY, subTerm),
                                SUBSTRACT,
                                new Expression(new Term(factor, MULTIPLY, td))
                        ),
                        DIVIDE,
                        new Term(new Exponential(new ParenthesizedExpression(subTerm), new Constant("2")))
                );

            case MULTIPLY:
                subTermDerivative = this.subTerm.getDerivative();
                fd = factorDerivative instanceof Factor ? (Factor) factorDerivative : new ParenthesizedExpression((Term) factorDerivative);
                td = subTermDerivative instanceof Term ? (Term) subTermDerivative : new Term((Factor) subTermDerivative);
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
    public String simplify() {
        if (subTerm == null) {
            return factor.simplify();
        } else {
            switch (operator) {
                case DIVIDE:
                    return ExpressionBuilder.division(factor.simplify(), subTerm.simplify());
                case MULTIPLY:
                    return ExpressionBuilder.product(factor.simplify(), subTerm.simplify());
                default:
                    throw new RuntimeException("Unexpected operator [" + operator + "]");
            }
        }
    }

    @Override
    public String toString() {
        if (subTerm == null) {
            return "" + factor;
        } else {
            if (operator.equals(DIVIDE)) {
                return ExpressionBuilder.division(factor.toString(), subTerm.toString());
            } else if (operator.equals(MULTIPLY)) {
                return ExpressionBuilder.product(factor.toString(), subTerm.toString());
            }
        }
        throw new RuntimeException("Unexpected operator [" + operator + "]");
    }

}
