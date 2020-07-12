package com.nemesis.mathcore.expressionsolver.operators;

public enum Sign {

    PLUS, MINUS;

    @Override
    public String toString() {
        return this.equals(PLUS) ? "+" : "-";
    }
}
