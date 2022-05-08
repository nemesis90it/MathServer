package com.nemesis.mathcore.expressionsolver.exception;

public class VariablesMismatchException extends RuntimeException {
    public VariablesMismatchException(String a, String b) {
        super(String.format("Illegal operation with different variables [%s] and [%s]", a, b));
    }

    public VariablesMismatchException(String msg) {
        super(msg);
    }
}
