package com.nemesis.mathcore.expressionsolver.expression.components;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.models.RationalFunction;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
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
                subTermDerivative = this.subTerm.getDerivative();
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
    public Component simplify() {

        if (this.factor.getSign() == MINUS && this.operator != NONE && this.getSubTerm().getFactor().getSign() == MINUS) {
            this.factor = ComponentUtils.cloneAndChangeSign(this.factor);
            this.subTerm.setFactor(ComponentUtils.cloneAndChangeSign(this.getSubTerm().getFactor()));
        }

        Component simplifiedFactor = factor.simplify();
        if (this.operator == NONE) {
            return simplifiedFactor;
        }

        Component simplifiedSubTerm = null;
        if (subTerm != null) {
            simplifiedSubTerm = subTerm.simplify();
        }

        /* Apply distributive property, if possible */

        if (simplifiedFactor instanceof Constant && operator.equals(MULTIPLY)) {
            Constant constant = (Constant) simplifiedFactor;
            if (simplifiedSubTerm instanceof ParenthesizedExpression) {
                ParenthesizedExpression parExpression = (ParenthesizedExpression) simplifiedSubTerm;
                if (parExpression.getSign() == (MINUS)) {
                    constant = new Constant(constant.getValue().multiply(new BigDecimal("-1")));
                }
                return ComponentUtils.applyConstantToExpression(parExpression.getExpression(), constant, this.operator);
            }
        }

        /* Apply operator, if possible */

        Term result = null;

        Component simplifiedRightFactor;
        if (subTerm != null && subTerm.getOperator() == NONE) {
            simplifiedRightFactor = subTerm.getFactor().simplify();
        } else {
            simplifiedRightFactor = simplifiedSubTerm;
        }

        Monomial leftMonomial = Monomial.getMonomial(simplifiedFactor);
        Monomial rightMonomial = Monomial.getMonomial(simplifiedRightFactor);

        if (rightMonomial != null && leftMonomial != null) {
            BiFunction<Monomial, Monomial, Term> monomialOperation;
            switch (this.operator) {
                case DIVIDE:
                    monomialOperation = Monomial::divide;
                    break;
                case MULTIPLY:
                    monomialOperation = Monomial::multiply;
                    break;
                default:
                    throw new RuntimeException("Unexpected operator [" + this.operator + "]");
            }

            result = monomialOperation.apply(leftMonomial, rightMonomial);
        } else {
//            RationalFunction leftRationalFunction = RationalFunction.getRationalFunction(simplifiedFactor);
//            RationalFunction rightRationalFunction = RationalFunction.getRationalFunction(simplifiedRightFactor);
//
//            if (leftRationalFunction != null && rightRationalFunction != null) {
//                result = RationalFunction.applyOperation(this.operator, leftRationalFunction, rightRationalFunction);
//            }
        }

        if (result != null) {
            return result;
        } else {
            // If simplifiedFactor or simplifiedRightFactor or the result of the operation isn't a monomial or a rational function , return following term:
            return new Term(ComponentUtils.getFactor(simplifiedFactor), this.operator, ComponentUtils.getTerm(simplifiedSubTerm));
        }
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
            if (operator.equals(DIVIDE)) {
                if (subTerm.getOperator() == NONE && subTerm.getFactor() instanceof ParenthesizedExpression) {
                    return ExpressionBuilder.division(factor.toString(), "(" + subTerm.toString() + ")");
                } else {
                    return ExpressionBuilder.division(factor.toString(), subTerm.toString());
                }
            } else if (operator.equals(MULTIPLY)) {
                return ExpressionBuilder.product(factor.toString(), subTerm.toString());
            }
        }
        throw new RuntimeException("Unexpected operator [" + operator + "]");
    }

}
