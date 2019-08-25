package com.nemesis.mathcore.expressionsolver.models;

public enum Sign {

    PLUS, MINUS;

    @Override
    public String toString() {
        return this.equals(PLUS) ? "+" : "-";
    }
}
