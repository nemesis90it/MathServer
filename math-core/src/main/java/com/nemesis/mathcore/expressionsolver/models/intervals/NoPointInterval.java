package com.nemesis.mathcore.expressionsolver.models.intervals;

import lombok.Data;

@Data
public class NoPointInterval implements GenericInterval {

    private final String variable;

    public NoPointInterval(String variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return String.format("for no value of %s", variable);
    }

    @Override
    public String toLatex() {
        return String.format("\\nexists %s", variable);
    }

    @Override
    public int compareTo(GenericInterval o) {
        return -1;
    }

}
