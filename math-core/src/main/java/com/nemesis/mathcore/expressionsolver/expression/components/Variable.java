package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
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
    public String toString() {
        if (sign.equals(PLUS)) {
            return String.valueOf(name);
        } else {
            return "(-" + name + ")";
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Variable) {
            return String.valueOf(this.name).compareTo(String.valueOf(o));
        } else {
            return Base.compare(this, o);
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
