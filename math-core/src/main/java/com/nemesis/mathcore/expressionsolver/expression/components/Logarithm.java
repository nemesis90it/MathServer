package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.MULTIPLY;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

@Data
@EqualsAndHashCode(callSuper = false)
public class Logarithm extends MathFunction {

    private BigDecimal base;
    private Expression argument;

    public Logarithm(Sign sign, BigDecimal base, Expression argument) {
        super();
        super.sign = sign;
        this.base = base;
        this.argument = argument;
    }

    public Logarithm(BigDecimal base, Expression argument) {
        super();
        this.base = base;
        this.argument = argument;
    }

    @Override
    public BigDecimal getValue() {

        if (value == null) {
            BigDecimal absValue;
            if (base.equals(NEP_NUMBER)) {
                absValue = BigDecimal.valueOf(Math.log(argument.getValue().doubleValue()));
            } else if (base.equals(BigDecimal.TEN)) {
                absValue = BigDecimal.valueOf(Math.log10(argument.getValue().doubleValue()));
            } else {
                throw new UnsupportedOperationException("Logarithm base [" + base.toPlainString() + "] not supported");
            }
            this.value = sign.equals(PLUS) ? absValue : absValue.multiply(MINUS_ONE_DECIMAL);
        }
        return value;
    }

    @Override
    public Component getDerivative(char var) {
        //  D[log(base,arg)] =  1/(arg*ln(base)) * D[arg]

        Factor lnBase = new Constant(new Logarithm(NEP_NUMBER, new Expression(new Term(new Constant(base)))).getValue());

        return new Term(
                new ParenthesizedExpression(
                        new Term(
                                new Constant("1"),
                                TermOperator.DIVIDE,
                                new Term(ComponentUtils.getFactor(argument), MULTIPLY, lnBase)
                        )
                ),
                MULTIPLY,
                ComponentUtils.getTerm(argument.getDerivative(var))
        );
    }

    @Override
    public Component rewrite(Rule rule) {
        this.setArgument(ComponentUtils.getExpression(this.getArgument().rewrite(rule)));
        return rule.applyTo(this);
    }

    @Override
    public Boolean isScalar() {
        return this.argument.isScalar();
    }

    @Override
    public Constant getValueAsConstant() {
        return new Constant(this.getValue());
    }

    @Override
    public String toString() {
        String log = base.equals(NEP_NUMBER) ? "ln" : (base.equals(BigDecimal.TEN) ? "log" : null);
        if (log == null) {
            return "Not Supported";
        } else {
            if (sign.equals(PLUS)) {
                return log + "(" + argument + ")";
            } else {
                return sign + log + "(" + argument + ")";
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Logarithm) {
            Comparator<Logarithm> baseComparator = Comparator.comparing(Logarithm::getBase);
            Comparator<Logarithm> logComparator = baseComparator.thenComparing(Logarithm::getArgument);
            return logComparator.compare(this, (Logarithm) o);
        } else {
            return Base.compare(this, o);
        }
    }
}
