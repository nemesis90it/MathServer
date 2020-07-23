package com.nemesis.mathcore.expressionsolver.components;

public interface Stringable {

    default String toLatex() {
        return this.toString();
    }

    String toString();
}
