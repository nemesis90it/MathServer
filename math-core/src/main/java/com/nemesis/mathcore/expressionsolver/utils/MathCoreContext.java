package com.nemesis.mathcore.expressionsolver.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("\nSetting mode to [" + numericMode.name() + "]");
        MathCoreContext.numericMode = numericMode;
    }
}
