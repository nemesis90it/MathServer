package com.nemesis.mathcore.expressionsolver.components;

/*
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
 */

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.intervals.GenericInterval;
import com.nemesis.mathcore.expressionsolver.models.Monomial;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.stringbuilder.LatexBuilder;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.utils.MathUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.*;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.*;

@Data
public class Term extends Component {

    private Factor factor;
    private TermOperator operator;
    private Term subTerm;

    public Term(Component factorAsComponent, TermOperator operator, Component subTermAsComponent) {

        Factor factor = Factor.getFactor(factorAsComponent);
        Term subTerm = null;
        if (subTermAsComponent != null) {
            subTerm = Term.getTerm(subTermAsComponent);
        }
        this.build(factor, operator, subTerm);
    }

    public Term(Factor factor, TermOperator operator, Component subTermAsComponent) {
        Term subTerm = null;
        if (subTermAsComponent != null) {
            subTerm = Term.getTerm(subTermAsComponent);
        }
        this.build(factor, operator, subTerm);
    }

    public Term(Component factor, TermOperator operator, Term subTerm) {
        this.build(Factor.getFactor(factor), operator, Term.getTerm(subTerm));
    }

    public Term(Factor factor, TermOperator operator, Term subTerm) {
        this.build(factor, operator, Term.getTerm(subTerm));
    }

    public Term(Component component) {
        Term t = getTerm(component);
        this.build(t.getFactor(), t.getOperator(), t.getSubTerm());
    }

    public Term(Factor factor) {
        this.build(Factor.getFactor(factor), NONE, null);
    }

    public void setSubTerm(Term subTerm) {
        build(this.factor, this.operator, subTerm);
    }

    public void setFactor(Factor factor) {
        build(factor, this.operator, this.subTerm);
    }

    private void build(Factor factor, TermOperator operator, Term subTerm) {

        // Prevent input components to be modified
        factor = factor.getClone();
        if (subTerm != null) {
            subTerm = subTerm.getClone();
        }

        if (isZero(factor) || (operator == MULTIPLY && isZero(subTerm))) {
            this.factor = new Constant(0);
            this.operator = NONE;
            this.subTerm = null;
        } else if (isOne(factor) && operator == MULTIPLY && subTerm != null) {
            this.build(subTerm.getFactor(), subTerm.getOperator(), subTerm.getSubTerm());
        } else if (isOne(subTerm)) {
            this.factor = factor;
            this.operator = NONE;
            this.subTerm = null;
        } else {
            this.factor = factor;
            this.operator = operator;
            this.subTerm = subTerm;
        }
    }

    public static Term getTerm(Component component) {

        if (component == null) {
            return null;
        }

        if (component instanceof Expression expression) {
            if (expression.getOperator().equals(ExpressionOperator.NONE)) {
                return getTerm(expression.getTerm());
            } else {
                return new Term(new ParenthesizedExpression(expression));
            }
        }

        if (component instanceof ParenthesizedExpression parExpression) {
            if (parExpression.getOperator().equals(ExpressionOperator.NONE)) {
                Expression expression;
                if (parExpression.getSign() == MINUS) {
                    // Remove MINUS sign, changing all signs inside parenthesis
                    expression = ComponentUtils.applyConstantToExpression(parExpression.getExpression(), new Constant(-1), TermOperator.MULTIPLY);
                } else {
                    expression = parExpression.getExpression();
                }
                // Call this method again so that will be executed the "Expression" case (see above)
                return getTerm(expression);
            } else {
                return new Term(parExpression);
            }
        }

        if (component instanceof Exponential exponential) {
            final Factor exponent = exponential.getExponent();
            if (isOne(exponent)) {
                return getTerm(exponential.getBase());
            } else if (isZero(exponent)) {
                return new Term(new Constant(1));
            } else {
                return new Term(exponential);
            }
        }

        if (component instanceof ConstantFunction constantFunction) {
            return getTerm(constantFunction.getComponent());
        }

        if (component instanceof Factor factor) {
            return new Term(factor);
        }

        if (component instanceof Monomial) {
            throw new UnsupportedOperationException("Move to Monomial class");
        }

        if (component instanceof Term term) {
            return term;
        }

        throw new IllegalArgumentException("Unexpected type [" + component.getClass() + "]");
    }

    public static Term buildTerm(Iterator<? extends Component> iterator, TermOperator operator) {
        final Term exitValue = new Term(new Constant(1));
        if (iterator.hasNext()) {
            Term term = Term.getTerm(iterator.next());
            final Term subTerm = buildTerm(iterator, operator);
            if (subTerm.equals(exitValue)) {
                return term;
            }
            if (term.getOperator() == NONE) {
                term.setOperator(operator);
                term.setSubTerm(subTerm);
                return term;
            }
            return new Term(term, operator, subTerm);
        } else {
            return exitValue;
        }
    }

    public static Term buildTerm(Set<? extends Factor> leftFactors, TermOperator operator, Set<? extends Factor> rightFactors) {

        Term simplifiedComponent = Term.buildTerm(leftFactors.iterator(), MULTIPLY);

        if (!rightFactors.isEmpty()) {
            final Term subTerm = Term.buildTerm(rightFactors.iterator(), MULTIPLY);
            if (simplifiedComponent.getOperator() == NONE) {
                simplifiedComponent.setOperator(operator);
                simplifiedComponent.setSubTerm(subTerm);
                return simplifiedComponent;
            } else {
                return new Term(simplifiedComponent, operator, subTerm);
            }
        } else {
            return simplifiedComponent;
        }
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
    public Component getDerivative(Variable var) {

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
                td = Term.getTerm(subTermDerivative);
                return new Term(
                        new ParenthesizedExpression(
                                new Term(fd, MULTIPLY, subTerm),
                                SUBTRACT,
                                new Term(factor, MULTIPLY, td)
                        ),
                        DIVIDE,
                        new Exponential(new ParenthesizedExpression(subTerm), new Constant(2))
                );
            case MULTIPLY:
                subTermDerivative = this.subTerm.getDerivative(var);
                fd = Factor.getFactor(factorDerivative);
                td = Term.getTerm(subTermDerivative);
                return new Expression(
                        new Term(fd, MULTIPLY, subTerm),
                        SUM,
                        new Term(factor, MULTIPLY, td)
                );
            default:
                throw new RuntimeException("Unexpected operator [" + operator + "]");
        }

    }

    @Override
    public Component rewrite(Rule rule) {
        this.setFactor(Factor.getFactor(this.getFactor().rewrite(rule)));
        if (this.getSubTerm() != null) {
            this.setSubTerm(Term.getTerm(this.getSubTerm().rewrite(rule)));
        }
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.factor.isScalar() && (this.operator == NONE || this.subTerm.isScalar());
    }

    @Override
    public Constant getValueAsConstant() {

        if (!this.isScalar()) {
            throw new NoValueException("This component is not a scalar");
        }

        BigDecimal value = this.getValue();

        if (MathUtils.isIntegerValue(value)) {
            return new Constant(value);
        } else if (isRational(this) && MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            Constant numerator = this.getFactor().getValueAsConstant();
            if (this.getSubTerm() != null) {
                Constant denominator = this.getSubTerm().getValueAsConstant();
                return new Fraction(numerator, denominator);
            } else {
                return numerator;
            }
        } else if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            if (this.getOperator() == NONE) {
                return this.getFactor().getValueAsConstant();
            }
        }
        return new ConstantFunction(this);
    }

    private static boolean isRational(Term term) {
        if (term.getFactor() instanceof Constant && term.getOperator() == DIVIDE) {
            return isRational(term.getSubTerm());
        } else {
            return term.getFactor().getClass().isAssignableFrom(Constant.class) && term.getOperator() == NONE;
        }
    }

    @Override
    public int compareTo(Component o) {

        if (o instanceof Infinity i) {
            return i.getSign() == PLUS ? -1 : 1;
        }

        Comparator<Term> comparatorByFactor = Comparator.comparing(Term::getFactor);
        if (this.subTerm != null) {
            Comparator<Term> comparatorByFactorAndSubTerm = comparatorByFactor.thenComparing(Term::getSubTerm);
            Comparator<Term> comparator = comparatorByFactorAndSubTerm.thenComparing(Term::getOperator);
            return comparator.compare(this, (Term) o);
        } else {
            return comparatorByFactor.compare(this, (Term) o);
        }
    }

    @Override
    public Term getClone() {
        return new Term(factor.getClone(), operator, subTerm != null ? subTerm.getClone() : null);
    }

    @Override
    public Domain getDomain(Variable variable) {
        Domain domain = new Domain();
        if (factor.contains(variable)) {
            domain.addIntervals(factor.getDomain(variable).getIntervals());
        }
        if (subTerm != null && subTerm.contains(variable)) {
            domain.addIntervals(subTerm.getDomain(variable).getIntervals());
            if (operator == DIVIDE) {
                Set<GenericInterval> thisDefinitionSets = ExpressionUtils.resolve(this.subTerm, RelationalOperator.NEQ, new Constant(0), variable);
                domain.addIntervals(thisDefinitionSets);
            }
        }
        return domain;
    }

    @Override
    public Set<Variable> getVariables() {
        TreeSet<Variable> variables = new TreeSet<>(factor.getVariables());
        if (subTerm != null) {
            variables.addAll(subTerm.getVariables());
        }
        return variables;
    }

    @Override
    public String toString() {
        if (subTerm == null) {
            return "" + factor;
        } else {
            String factorAsString = factor.toString();
            String termAsString = subTerm.toString();
            if (isParenthesized(factor)) {
                factorAsString = "(" + factorAsString + ")";
            }
            if (isParenthesized(subTerm) || (operator == DIVIDE && subTerm.getOperator() == MULTIPLY)) {
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

    @Override
    public String toLatex() {
        if (subTerm == null) {
            return "" + factor.toLatex();
        } else {
            String factorAsLatex = factor.toLatex();
            String termAsLatex = subTerm.toLatex();
            if (isParenthesized(factor)) {
                factorAsLatex = "(" + factorAsLatex + ")";
            }
            if (isParenthesized(subTerm) || (operator == DIVIDE && subTerm.getOperator() == MULTIPLY)) {
                termAsLatex = "(" + termAsLatex + ")";
            }
            if (operator.equals(DIVIDE)) {
                return LatexBuilder.division(factorAsLatex, termAsLatex);
            } else if (operator.equals(MULTIPLY)) {
                return LatexBuilder.product(factorAsLatex, termAsLatex);
            }
        }
        throw new RuntimeException("Unexpected operator [" + operator + "]");
    }

    @Override
    public boolean contains(TermOperator termOperator) {
        return Objects.equals(this.getOperator(), termOperator) || (this.subTerm != null && subTerm.contains(termOperator));
    }

    @Override
    public boolean contains(Variable variable) {
        return factor.contains(variable) || (subTerm != null && subTerm.contains(variable));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return Objects.equals(factor, term.factor) &&
                operator == term.operator &&
                Objects.equals(subTerm, term.subTerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factor, operator, subTerm);
    }
}
