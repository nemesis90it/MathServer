package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.models.interval.NoDelimiterInterval;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;

@Data
public class Variable extends Base {

    private char name;

    public Variable(char name) {
        this.name = name;
    }

    public Variable(Sign sign, char name) {
        super.sign = sign;
        this.name = name;
    }

    @Override
    public BigDecimal getValue() {
        throw new NoValueException("Variables have no value");
    }

    public char getName() {
        return name;
    }

    @Override
    public Component getDerivative(char var) {
        return this.name == var ? new Constant("1") : new Constant("0");
    }

    @Override
    public Component rewrite(Rule rule) {
        return this;
    }

    @Override
    public Boolean isScalar() {
        return false;
    }

    @Override
    public Constant getValueAsConstant() {
        throw new NoValueException("Variables have no value");
    }

    @Override
    public boolean contains(Variable variable) {
        return this.name == variable.getName();
    }

    @Override
    public Variable getClone() {
        return new Variable(sign, name);
    }

    @Override
    public Domain getDomain(Variable variable) {
        return new Domain(new NoDelimiterInterval(variable.getName(), NoDelimiterInterval.Type.FOR_EACH));
    }

    @Override
    public String toString() {
        if (sign.equals(PLUS)) {
            return String.valueOf(name);
        } else {
            return "(-" + name + ")";
        }
    }

    @Override
    public int compareTo(Component c) {
        if (c instanceof Variable) {
            return String.valueOf(this.name).compareTo(String.valueOf(c));
        } else if (c instanceof Base b) {
            return Base.compare(this, b);
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
        Variable variable = (Variable) o;
        return name == variable.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
