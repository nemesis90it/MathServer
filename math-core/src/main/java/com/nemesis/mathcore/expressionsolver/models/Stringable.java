package com.nemesis.mathcore.expressionsolver.models;

public interface Stringable {

    default String toLatex() {
        return this.toString();
    }

    String toString();
}
