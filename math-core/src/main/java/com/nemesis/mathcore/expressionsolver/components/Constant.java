package com.nemesis.mathcore.expressionsolver.components;


import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.intervals.DoublePointInterval;
import com.nemesis.mathcore.expressionsolver.models.intervals.Intervals;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.models.intervals.Delimiter.MINUS_INFINITY;
import static com.nemesis.mathcore.expressionsolver.models.intervals.Delimiter.PLUS_INFINITY;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.*;

public class Constant extends Base {

    protected BigDecimal value = null;

    public Constant() {
    }

    public Constant(String number) {
        value = new BigDecimal(number);
    }

    public Constant(BigDecimal number) {
        value = number;
    }

    public Constant(BigInteger number) {
        value = new BigDecimal(number);
    }

    public Constant(Integer number) {
        value = new BigDecimal(number);
    }

    public Constant(Sign sign, BigDecimal value) {
        if (sign == MINUS && value.compareTo(BigDecimal.ZERO) < 0) {
            sign = PLUS;
            value = value.abs();
        }
        super.sign = sign;
        this.value = value;
    }

    @Override
    public BigDecimal getValue() {
        return sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
    }

    @Override
    public Constant getDerivative(Variable var) {
        return new Constant("0");
    }

    @Override
    public Component rewrite(Rule rule) {
        // TODO: check mode (decimal/fraction)
        if (sign == MINUS && this.value.compareTo(BigDecimal.ZERO) < 0) {
            return new Constant(value.abs());
        }
        return this;
    }

    @Override
    public Boolean isScalar() {
        return true;
    }

    @Override
    public Constant getValueAsConstant() {
        return this;
    }

    @Override
    public boolean contains(Variable variable) {
        return false;
    }

    @Override
    public Constant getClone() {
        return new Constant(this.sign, new BigDecimal(value.toPlainString()));
    }

    @Override
    public Domain getDomain(Variable variable) {
        return new Domain(new Intervals(new DoublePointInterval(variable.toString(), MINUS_INFINITY, PLUS_INFINITY)));
    }

    @Override
    public Set<Variable> getVariables() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL) {
            if (this.value.toPlainString().length() > 8 && this.value.toPlainString().substring(0, 10).equals(NEP_NUMBER.toPlainString().substring(0, 10))) { // TODO
                return ExpressionBuilder.addSign(sign.toString(), String.valueOf(E_CHAR));
            }
            if (this.value.toPlainString().length() > 8 && this.value.toPlainString().substring(0, 10).equals(PI.toPlainString().substring(0, 10))) { // TODO
                return ExpressionBuilder.addSign(sign.toString(), String.valueOf(PI_CHAR));
            }
        }
        String valueAsString = SyntaxUtils.removeNonSignificantZeros(value).toString();
        return ExpressionBuilder.addSign(sign.toString(), valueAsString);
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Constant) {
            return this.getValue().compareTo(c.getValue());
        } else if (c instanceof Base b) {
            return compare(this, b);
        } else if (c instanceof Exponential e) {
            return new Exponential(this, new Constant(1)).compareTo(e);
        } else {
            throw new UnsupportedOperationException("Comparison between [" + this.getClass() + "] and [" + c.getClass() + "] is not supported yet");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Constant constant = (Constant) o;
        return Objects.equals(value, constant.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
