package com.nemesis.mathcore.expressionsolver.expression.components;

import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;

@Data
@EqualsAndHashCode(callSuper = false)
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
    public Component getDerivative() {
        return new Constant("1");
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
}
