package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
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
    public Component getDerivative() {
        //  D[log(base,arg)] =  1/(arg*ln(base)) * D[arg]

        Term ln_base = new Term(new Constant(new Logarithm(NEP_NUMBER, new Expression(new Term(new Constant(base)))).getValue()));

        Term logDerivative = new Term(
                new ParenthesizedExpression(
                        new Term(
                                new Constant("1"),
                                TermOperator.DIVIDE,
                                new Term(ComponentUtils.getFactor(argument), MULTIPLY, ln_base)
                        )
                ),
                MULTIPLY,
                ComponentUtils.getTerm(argument.getDerivative())
        );

        return logDerivative.simplify();
    }

    @Override
    public Component simplify() {

        // log(base,base) = 1
        if (ComponentUtils.isFactor(argument, Constant.class) && argument.getValue().equals(base)) {
            return new Constant("1");
        }

        if (ComponentUtils.isFactor(argument, Exponential.class)) {
            Exponential argument = (Exponential) this.argument.getTerm().getFactor();
            if (argument.getBase() instanceof Constant && argument.getBase().getValue().equals(this.base)) {
                // log(base, base^x) = x
                return argument.getExponent();
            } else {
                // log(x^y) = y*log(x)
                return new Term(argument.getExponent(), MULTIPLY, new Term(new Logarithm(base, ComponentUtils.getExpression(argument.getBase()))));
            }
        }

        return this;
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
