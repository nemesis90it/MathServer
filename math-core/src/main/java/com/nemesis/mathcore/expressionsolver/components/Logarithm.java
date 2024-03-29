package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.RelationalOperator;
import com.nemesis.mathcore.expressionsolver.intervals.model.GenericInterval;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.utils.MathUtils;
import com.numericalmethod.suanshu.number.big.BigDecimalUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

@Data
public class Logarithm extends MathFunction {

    private BigDecimal base;
    private WrappedExpression argument;

    public Logarithm(Sign sign, BigDecimal base, WrappedExpression argument) {
        super();
        super.sign = sign;
        this.base = base;
        this.argument = argument;
    }

    public Logarithm(BigDecimal base, WrappedExpression argument) {
        super();
        this.base = base;
        this.argument = argument;
    }

    @Override
    public BigDecimal getValue() {
        BigDecimal absValue;
        if (base.equals(NEP_NUMBER)) {
            absValue = BigDecimalUtils.log(argument.getValue());
        } else if (base.equals(BigDecimal.TEN)) {
            absValue = BigDecimal.valueOf(Math.log10(argument.getValue().doubleValue()));
        } else {
            throw new UnsupportedOperationException("Logarithm base [" + base.toPlainString() + "] is not supported yet");
        }
        this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);

        return value;
    }

    @Override
    public Component getDerivative(Variable var) {
        //  D[log(base,arg)] =  1/(arg*ln(base)) * D[arg]

        Factor lnBase = new Logarithm(NEP_NUMBER, new ParenthesizedExpression(new Term(new Constant(base))));

        BigDecimal lnBaseValue = lnBase.getValue();
        if (MathUtils.isIntegerValue(lnBaseValue)) {
            lnBase = new Constant(lnBaseValue);
        }

        return new Term(
                new ParenthesizedExpression(
                        new Term(
                                new Constant("1"),
                                DIVIDE,
                                new Term(
                                        Factor.getFactor(argument),
                                        MULTIPLY,
                                        lnBase
                                )
                        )
                ),
                MULTIPLY,
                Term.getTerm(argument.getDerivative(var))
        );
    }

    @Override
    public Component rewrite(Rule rule) {
        this.setArgument(new ParenthesizedExpression(ComponentUtils.getExpression(this.getArgument().rewrite(rule))));
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.argument.isScalar();
    }

    @Override
    public boolean contains(Variable variable) {
        return argument.contains(variable);
    }

    @Override
    public Logarithm getClone() {
        return new Logarithm(this.sign, new BigDecimal(this.base.toPlainString()), argument.getClone());
    }

    @Override
    public Domain getDomain(Variable variable) {
        Domain domain = new Domain();
        if (argument.contains(variable)) {
            domain.intersectWith(argument.getDomain(variable).getIntervals());
            Set<GenericInterval> thisDefinitionSets = ExpressionUtils.resolve(this.argument, RelationalOperator.GTE, new Constant(0), variable);
            domain.intersectWith(thisDefinitionSets);
        }
        return domain;
    }

    @Override
    public Set<Variable> getVariables() {
        return argument.getVariables();
    }

    @Override
    public String toString() {
        String logarithm;
        if (base.equals(NEP_NUMBER)) {
            logarithm = ExpressionBuilder.naturalLogarithm(sign, argument.toString());
        } else {
            logarithm = ExpressionBuilder.logarithm(sign, base, argument.toString());
        }
        return logarithm;
    }

    @Override
    public String toLatex() {
        String logarithm;
        if (base.equals(NEP_NUMBER)) {
            logarithm = ExpressionBuilder.naturalLogarithm(sign, argument.toLatex());
        } else {
            logarithm = ExpressionBuilder.logarithm(sign, base, argument.toLatex());
        }
        return logarithm;
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Infinity i) {
            return i.getSign() == PLUS ? -1 : 1;
        } else if (c instanceof Logarithm) {
            Comparator<Logarithm> baseComparator = Comparator.comparing(Logarithm::getBase);
            Comparator<Logarithm> logComparator = baseComparator.thenComparing(Logarithm::getArgument);
            return logComparator.compare(this, (Logarithm) c);
        } else if (c instanceof Base b) {
            return Base.compare(this, b);
        } else if (c instanceof Exponential e) {
            return new Exponential(this, new Constant(1)).compareTo(e);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }

    @Override
    public Classifier classifier() {
        return new LogarithmClassifier(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Logarithm logarithm = (Logarithm) o;
        return Objects.equals(base, logarithm.base) &&
                Objects.equals(argument, logarithm.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), base, argument);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class LogarithmClassifier extends Classifier {

        private Logarithm logarithm;

        public LogarithmClassifier(Logarithm logarithm) {
            super(Logarithm.class);
            this.logarithm = logarithm;
        }
    }
}
