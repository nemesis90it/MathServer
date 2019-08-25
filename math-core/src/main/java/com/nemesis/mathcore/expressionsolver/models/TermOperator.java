package com.nemesis.mathcore.expressionsolver.models;

public enum TermOperator {

    MULTIPLY, DIVIDE, NONE;

    @Override
    public String toString() {
        return this.equals(MULTIPLY) ? "*" : (this.equals(DIVIDE) ? "/" : "");
    }
}
