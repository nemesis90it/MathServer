package com.nemesis.mathcore.expressionsolver.components;

import com.nemesis.mathcore.expressionsolver.exception.NoValueException;
import com.nemesis.mathcore.expressionsolver.intervals.model.DoublePointInterval;
import com.nemesis.mathcore.expressionsolver.intervals.model.Union;
import com.nemesis.mathcore.expressionsolver.models.Domain;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.MINUS_INFINITY;
import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.PLUS_INFINITY;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;

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
    public Component getDerivative(Variable var) {
        return this.name == var.getName() ? new Constant("1") : new Constant("0");
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
        throw new RuntimeException("Variable is not a constant");
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
        return new Domain(new Union(new DoublePointInterval(variable.toString(), MINUS_INFINITY, PLUS_INFINITY)));
    }

    @Override
    public Set<Variable> getVariables() {
        return Collections.singleton(this.getClone());
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
        if (c instanceof Infinity i) {
            return i.getSign() == PLUS ? -1 : 1;
        } else if (c instanceof Variable) {
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
        if (!super.equals(o)) return false;
        Variable variable = (Variable) o;
        return name == variable.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public Classifier classifier() {
        return new VariableClassifier(this);

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class VariableClassifier extends Classifier {

        private Variable variable;

        public VariableClassifier(Variable variable) {
            super(Variable.class);
            this.variable = variable;
        }
    }
}
