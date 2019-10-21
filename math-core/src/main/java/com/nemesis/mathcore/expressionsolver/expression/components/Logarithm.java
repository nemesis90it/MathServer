package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

@Data
@EqualsAndHashCode(callSuper = false)
public class Logarithm extends MathFunction {

    private BigDecimal base;
    private Factor argument;

    public Logarithm(Sign sign, BigDecimal base, Factor argument) {
        super();
        super.sign = sign;
        this.base = base;
        this.argument = argument;
    }

    public Logarithm(BigDecimal base, Factor argument) {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Component simplify() {
        // TODO
        return this;
//        throw new UnsupportedOperationException();
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
    public boolean absEquals(Object obj) {
        return obj instanceof Logarithm &&
                Objects.equals(this.argument, ((Logarithm) obj).getArgument()) &&
                Objects.equals(this.base, ((Logarithm) obj).getBase());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Logarithm) {
            Comparator<Logarithm> baseComparator = Comparator.comparing(Logarithm::getBase);
            Comparator<Logarithm> logComparator = baseComparator.thenComparing(Logarithm::getArgument);
            return logComparator.compare(this, (Logarithm) o);
        } else {
            return Base.compareTo(this, o);
        }
    }
}
