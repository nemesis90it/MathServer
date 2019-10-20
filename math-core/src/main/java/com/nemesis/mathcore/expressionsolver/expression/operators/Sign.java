package com.nemesis.mathcore.expressionsolver.expression.operators;

public enum Sign {

    PLUS, MINUS;

    @Override
    public String toString() {
        return this.equals(PLUS) ? "+" : "-";
    }
}
