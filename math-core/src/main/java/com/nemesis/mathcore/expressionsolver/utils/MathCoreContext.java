package com.nemesis.mathcore.expressionsolver.utils;

public class MathCoreContext {

    private static Mode numericMode = Mode.DECIMAL;

    public enum Mode {
        FRACTIONAL,
        DECIMAL
    }

    public static Mode getNumericMode() {
        return numericMode;
    }

    public static void setNumericMode(Mode numericMode) {
        MathCoreContext.numericMode = numericMode;
    }
}
