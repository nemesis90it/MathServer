package com.nemesis.mathcore.expressionsolver.expression.components;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBSTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.*;

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

                if (subTermDerivative instanceof Term) {
                    td = (Term) subTermDerivative;
                } else if (subTermDerivative instanceof Factor) {
                    td = new Term((Factor) subTermDerivative);
                } else {
                    td = new Term(new ParenthesizedExpression((Expression) subTermDerivative));
                }

                return new Term(
                        new ParenthesizedExpression(
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

                if (subTermDerivative instanceof Term) {
                    td = (Term) subTermDerivative;
                } else if (subTermDerivative instanceof Factor) {
                    td = new Term((Factor) subTermDerivative);
                } else {
                    td = new Term(new ParenthesizedExpression((Expression) subTermDerivative));
                }

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
    public Component simplify() {

        Component simplifiedRightFactor = factor.simplify();
        Component simplifiedTerm;

        switch (this.operator) {
            case NONE:
                return simplifiedRightFactor;
            case DIVIDE:
                simplifiedTerm = this.simplifyQuotient(simplifiedRightFactor);
                if (simplifiedTerm != null) return simplifiedTerm;
                break;
            case MULTIPLY:
                simplifiedTerm = this.simplifyProduct(simplifiedRightFactor);
                if (simplifiedTerm != null) return simplifiedTerm;
            default:
                throw new RuntimeException("Unexpected operator [" + this.operator + "]");
        }

        return this;
    }

    private Term simplifyProduct(Component simplifiedRightFactor) {
        Term product;
        switch (subTerm.getOperator()) {
            case NONE:
                Component simplifiedLeftFactor = subTerm.getFactor().simplify();
                Monomial leftMonomial = Monomial.getMonomial(simplifiedLeftFactor);
                if (leftMonomial == null) {
                    return this;
                }
                Monomial rightMonomial = Monomial.getMonomial(simplifiedRightFactor);
                if (rightMonomial == null) {
                    return this;
                }
                product = Monomial.multiply(rightMonomial, leftMonomial);
                return Objects.requireNonNullElse(product, this);
            case MULTIPLY:
            /*  Monomial multiplication chain, form left to right (example):
                -------------------- TERM -----------------
                ----------SubTerm----------  MUL --FACTOR--
                --SubTerm--  MUL   --Fact--   *     x
                  (3*x)      *       (2*x)

                 multiply[multiply[(3*x), (2*x)], x]
             */
                Component simplifiedSubTerm = subTerm.simplify();
                if (simplifiedSubTerm instanceof Monomial && simplifiedRightFactor instanceof Monomial) {
                    product = Monomial.multiply((Monomial) simplifiedSubTerm, (Monomial) simplifiedRightFactor);
                    return Objects.requireNonNullElse(product, this);
                } else {
                    return this;
                }
            case DIVIDE:
                // TODO
                return this;
        }
        return null;
    }

    private Component simplifyQuotient(Component simplifiedRightFactor) {
        switch (subTerm.getOperator()) {
            case NONE:
                Component simplifiedLeftFactor = subTerm.getFactor().simplify();
                Monomial divisor = Monomial.getMonomial(simplifiedLeftFactor);
                if (divisor == null) {
                    return this;
                }
                Monomial dividend = Monomial.getMonomial(simplifiedRightFactor);
                if (dividend == null) {
                    return this;
                }
                return Monomial.divide(dividend, divisor);
            case MULTIPLY:
                // TODO
                return this;
            case DIVIDE:
                // TODO
                return this;
        }
        return this;
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
