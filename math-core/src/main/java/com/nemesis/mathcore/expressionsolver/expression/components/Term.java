package com.nemesis.mathcore.expressionsolver.expression.components;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;

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
                td = subTermDerivative instanceof Term ? (Term) subTermDerivative : new Term((Factor) subTermDerivative);
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
    public Component simplify() {

        Component simplifiedFactor = factor.simplify();

        switch (operator) {
            case NONE:
                return simplifiedFactor;
            case DIVIDE:
                // TODO
                break;
            case MULTIPLY:

                if (subTerm.operator.equals(NONE)) {
                    Monomial leftMonomial = this.getMonomial(subTerm.factor);
                    if (leftMonomial == null) {
                        return this;
                    }
                    Monomial rightMonomial = this.getMonomial(simplifiedFactor);
                    if (rightMonomial == null) {
                        return this;
                    }
                    return Monomial.multiply(rightMonomial, leftMonomial);
                }

                if (subTerm.operator.equals(MULTIPLY)) {
                    /*  Monomial multiplication chain, form left to right (example):
                        -------------------- TERM -----------------
                        ----------SubTerm----------  MUL --FACTOR--
                        --SubTerm--  MUL   --Fact--   *     x
                          (3*x)      *       (2*x)

                         multiply[multiply[(3*x), (2*x)], x]
                     */
                    Component simplifiedSubTerm = subTerm.simplify();
                    if (simplifiedSubTerm instanceof Monomial && simplifiedFactor instanceof Monomial) {
                        return Monomial.multiply((Monomial) simplifiedSubTerm, (Monomial) simplifiedFactor);
                    }
                }
                if (subTerm.operator.equals(DIVIDE)) {
                    // TODO
                }
                return this;
            default:
                throw new RuntimeException("Unexpected operator [" + operator + "]");
        }

        return this;
    }

    /* Monomial Tree
        ----------------------------FACTOR---------------------------
        --------------------------EXPRESSION-------------------------
        ----------------LEFT_TERM------------------    NONE      null
        -LEFT_FACT-  MUL  -----------TERM----------
            	          -RIGHT_FACT-  NONE  null
         CONST       *     FACT
         CONST       *     EXPON
         FACT        *     CONST
         EXPON       *     CONST
     */
    private Monomial getMonomial(Component component) {

        Term leftTerm;
        if (component instanceof ParenthesizedExpression) {
            ParenthesizedExpression expression = (ParenthesizedExpression) component;
            if (expression.getOperator().equals(ExpressionOperator.NONE)) {
                leftTerm = expression.getTerm();
            } else {
                return null;
            }
        } else {
            leftTerm = (Term) component;
        }

        if (leftTerm.operator.equals(MULTIPLY) && leftTerm.getSubTerm().getOperator().equals(NONE)) {
            Factor rightFactor = leftTerm.getSubTerm().getFactor();
            Factor leftFactor = leftTerm.getFactor();
            if (leftFactor instanceof Constant) {
                return this.buildMonomial((Constant) leftFactor, rightFactor);
            }
            if (rightFactor instanceof Constant) {
                return this.buildMonomial((Constant) rightFactor, leftFactor);
            }
        }

        return null;
    }

    private Monomial buildMonomial(Constant leftFactor, Factor rightFactor) {
        if (rightFactor instanceof ParenthesizedExpression && ((ParenthesizedExpression) rightFactor).getOperator() == ExpressionOperator.NONE) {
            return null; // Factor cannot be a term
        }
        if (rightFactor instanceof Exponential) {
            Exponential rightFactorExponential = (Exponential) rightFactor;
            return new Monomial(leftFactor, rightFactorExponential.getBase(), rightFactorExponential.getExponent());
        } else {
            return new Monomial(leftFactor, rightFactor, new Constant("1"));
        }
    }


//    @Override
//    public String simplify() {
//        String simplified;
//
//        switch (operator) {
//            case NONE:
//                simplified = factor.simplify();
//                break;
//            case DIVIDE:
//                simplified = ExpressionBuilder.division(factor.simplify(), subTerm.simplify());
//                break;
//            case MULTIPLY:
//                simplified = ExpressionBuilder.product(factor.simplify(), subTerm.simplify());
//                break;
//            default:
//                throw new RuntimeException("Unexpected operator [" + operator + "]");
//        }
//
//        if (simplified.contains("x")) {
//            return simplified;
//        } else {
//            return String.valueOf(ExpressionParser.evaluate(simplified));
//        }
//    }

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
