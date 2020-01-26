package com.nemesis.mathcore.expressionsolver.expression.components;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.OneTermReduction;
import com.nemesis.mathcore.expressionsolver.rewritting.rules.ZeroTermReduction;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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

    public Term(Component factor, TermOperator operator, Component subTerm) {
        this.factor = Factor.getFactor(factor);
        this.operator = operator;
        this.subTerm = Term.getSimplestTerm(subTerm);
    }

    public Term(Factor factor, TermOperator operator, Component subTerm) {
        this.factor = factor;
        this.operator = operator;
        this.subTerm = Term.getSimplestTerm(subTerm);
    }

    public Term(Component factor, TermOperator operator, Term subTerm) {
        this.factor = Factor.getFactor(factor);
        this.operator = operator;
        this.subTerm = subTerm;
    }

    public Term(Factor factor, TermOperator operator, Term subTerm) {
        this.factor = factor;
        this.operator = operator;
        this.subTerm = subTerm;
    }

    public Term(Component component) {
        Term t = getSimplestTerm(component);
        this.factor = t.getFactor();
        this.operator = t.getOperator();
        this.subTerm = t.getSubTerm();
    }


    public Term(Factor factor) {
        this.factor = factor;
        this.operator = NONE;
    }

    public static Term getSimplestTerm(Component component) {

        if (component instanceof Expression) {
            Expression expression = (Expression) component;
            if (expression.getOperator().equals(ExpressionOperator.NONE)) {
                return getSimplestTerm(expression.getTerm());
            } else {
                return new Term(new ParenthesizedExpression((Expression) component));
            }
        }

        if (component instanceof ParenthesizedExpression) {
            ParenthesizedExpression parExpression = (ParenthesizedExpression) component;
            if (parExpression.getOperator().equals(ExpressionOperator.NONE)) {
                Expression expression;
                if (parExpression.getSign() == MINUS) {
                    // Remove MINUS sign, changing all signs inside parenthesis
                    expression = ComponentUtils.applyConstantToExpression(parExpression.getExpression(), new Constant("-1"), TermOperator.MULTIPLY);
                } else {
                    expression = parExpression.getExpression();
                }
                // Call this method again so that will be executed the "Expression" case (see above)
                return getSimplestTerm(expression);
            }
        }

        if (component instanceof Factor) {
            return new Term((Factor) component);
        }

        if (component instanceof Monomial) {
            throw new UnsupportedOperationException("Move to Monomial class");
        }

        if (component instanceof Term) {
            return (Term) component;
        }

        throw new IllegalArgumentException("Unexpected type [" + component.getClass() + "]");
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
                fd = Factor.getFactor(factorDerivative);
                td = Term.getSimplestTerm(subTermDerivative);
                return new Term(
                        new ParenthesizedExpression(
                                new Term(fd, MULTIPLY, subTerm),
                                SUBTRACT,
                                new Term(factor, MULTIPLY, td)
                        ),
                        DIVIDE,
                        new Exponential(new ParenthesizedExpression(subTerm), new Constant("2"))
                );
            case MULTIPLY:
                subTermDerivative = this.subTerm.getDerivative(var);
                fd = Factor.getFactor(factorDerivative);
                td = Term.getSimplestTerm(subTermDerivative);
                return new Expression(
                        new Term(fd, MULTIPLY, subTerm),
                        SUM,
                        new Term(factor, MULTIPLY, td)
                );
            default:
                throw new RuntimeException("Unexpected operator");
        }

    }

    @Override
    public Component rewrite(Rule rule) {
        this.setFactor(Factor.getFactor(this.getFactor().rewrite(rule)));
        if (this.getSubTerm() != null) {
            this.setSubTerm(Term.getSimplestTerm(this.getSubTerm().rewrite(rule)));
        }
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.factor.isScalar() && (this.subTerm == null || this.subTerm.isScalar());
    }

    @Override
    public Constant getValueAsConstant() {

        if (!this.isScalar()) {
            throw new NoValueException("This component is not a scalar");
        }

        // TODO: Do not use rules outside method ExpressionUtils.simplify

        BigDecimal value = this.getValue();

        List<Rule> termRules = Arrays.asList(new ZeroTermReduction(), new OneTermReduction());

        Component simplifiedComponent = this;
        for (Rule rule : termRules) {
            simplifiedComponent = simplifiedComponent.rewrite(rule);
        }

        Term simplifiedTerm = Term.getSimplestTerm(simplifiedComponent);

        boolean isRational = simplifiedTerm.getOperator() == DIVIDE && !MathUtils.isIntegerValue(value);
        boolean isIrrational = !MathUtils.isIntegerValue(value);

        if (isRational && MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            Constant numerator = simplifiedTerm.getFactor().getValueAsConstant();
            Constant denominator = simplifiedTerm.getSubTerm().getValueAsConstant();
            return new Fraction(numerator, denominator);
        } else if (isIrrational && MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            if (simplifiedTerm.getOperator() == NONE) {
                return factor.getValueAsConstant();
            } else {
                return new ConstantFunction(simplifiedTerm);
            }
        } else {
            return new Constant(value);
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
            String factorAsString = factor.toString();
            String termAsString = subTerm.toString();
            if (factor instanceof ParenthesizedExpression) {
                factorAsString = "(" + factorAsString + ")";
            }
            if (subTerm.getOperator() == NONE && subTerm.getFactor() instanceof ParenthesizedExpression
                    || (operator == DIVIDE && subTerm.getOperator() == MULTIPLY)) {
                termAsString = "(" + termAsString + ")";
            }
            if (operator.equals(DIVIDE)) {
                return ExpressionBuilder.division(factorAsString, termAsString);
            } else if (operator.equals(MULTIPLY)) {
                return ExpressionBuilder.product(factorAsString, termAsString);
            }
        }
        throw new RuntimeException("Unexpected operator [" + operator + "]");
    }

}
