package com.nemesis.mathcore.expressionsolver.expression.components;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.BiFunction;

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
                fd = ComponentUtils.getFactor(factorDerivative);
                td = ComponentUtils.getTerm(subTermDerivative);
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

        Component simplifiedFactor = factor.simplify();

        // Apply distributive property, if possible
        if (simplifiedFactor instanceof Constant && !operator.equals(NONE)) {
            Component simplifiedSubTerm = subTerm.simplify();
            if (simplifiedSubTerm instanceof ParenthesizedExpression) {
                ParenthesizedExpression parExpr = this.applyDistributiveProperty((Constant) simplifiedFactor, (ParenthesizedExpression) simplifiedSubTerm);
                return parExpr.getSign().equals(Sign.PLUS) ? parExpr.getExpression() : parExpr;
            }
        }

        // Apply operator, if possible
        Component simplifiedTerm;
        switch (this.operator) {
            case NONE:
                return simplifiedFactor;
            case DIVIDE:
                simplifiedTerm = this.simplifyQuotient(simplifiedFactor);
                if (simplifiedTerm != null) return simplifiedTerm;
                break;
            case MULTIPLY:
                simplifiedTerm = this.simplifyProduct(simplifiedFactor);
                if (simplifiedTerm != null) return simplifiedTerm;
            default:
                throw new RuntimeException("Unexpected operator [" + this.operator + "]");
        }

        // No simplification available
        return this;
    }

    private Term simplifyProduct(Component simplifiedLeftFactor) {

        Term product;
        Monomial leftMonomial;
        Monomial rightMonomial;

        switch (subTerm.getOperator()) {
            case NONE:
                Component simplifiedRightFactor = subTerm.getFactor().simplify();
                leftMonomial = Monomial.getMonomial(simplifiedLeftFactor);
                if (leftMonomial == null) {
                    return this;
                }

                rightMonomial = Monomial.getMonomial(simplifiedRightFactor);
                if (rightMonomial == null) {
                    return this;
                }
                product = Monomial.multiply(leftMonomial, rightMonomial);
                return Objects.requireNonNullElse(product, this);
            case MULTIPLY:
            /*  Monomial multiplication chain, form left to right (example):
                -------------------- TERM -----------------
                ----------SubTerm----------  MUL --FACTOR--
                --SubTerm--  MUL   --Fact--   *     x
                  (3*x)      *       (2*x)

                 multiply[multiply[(3*x), (2*x)], x]
             */
                leftMonomial = Monomial.getMonomial(simplifiedLeftFactor);
                rightMonomial = Monomial.getMonomial(subTerm.simplify());

                if (rightMonomial != null && leftMonomial != null) {
                    product = Monomial.multiply(leftMonomial, rightMonomial);
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

    private Component simplifyQuotient(Component simplifiedLeftFactor) {
        Monomial dividend;
        Monomial divisor;
        switch (subTerm.getOperator()) {
            case NONE:
                dividend = Monomial.getMonomial(simplifiedLeftFactor);
                if (dividend == null) {
                    return this;
                }
                Component simplifiedRightFactor = subTerm.getFactor().simplify();
                divisor = Monomial.getMonomial(simplifiedRightFactor);
                if (divisor == null) {
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

    private ParenthesizedExpression applyDistributiveProperty(Constant constant, ParenthesizedExpression parExpression) {

        BiFunction<Factor, Term, Term> termBuilder = (Factor factor, Term subTerm) -> new Term(factor, operator, subTerm);

        if (parExpression != null) {
            Expression expr = parExpression.getExpression();
            Sign parExpressionSign = parExpression.getSign();
            ParenthesizedExpression result = this.applyConstantToExpression(expr, termBuilder, constant);
            result.setSign(parExpressionSign);
            return result;
        }
        return null;
    }

    private ParenthesizedExpression applyConstantToExpression(Expression expr, BiFunction<Factor, Term, Term> termBuilder, Constant constant) {

        Term simplifiedTerm = ComponentUtils.getTerm(termBuilder.apply(constant, expr.getTerm()).simplify());
        ParenthesizedExpression result = new ParenthesizedExpression(simplifiedTerm);

        if (!Objects.equals(expr.getOperator(), ExpressionOperator.NONE)) {
            result.setTerm(simplifiedTerm);
            result.setOperator(expr.getOperator());
            result.setSubExpression(this.applyConstantToExpression(expr.getSubExpression(), termBuilder, constant).getExpression());
        }

        return result;
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
