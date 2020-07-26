package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import lombok.EqualsAndHashCode;

import static com.nemesis.mathcore.expressionsolver.models.intervals.Delimiter.Type.OPEN;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;

@EqualsAndHashCode(callSuper = true)
public class Delimiter extends GenericDelimiter {

    public static final Delimiter PLUS_INFINITY = new Delimiter(OPEN, new Infinity(PLUS));
    public static final Delimiter MINUS_INFINITY = new Delimiter(OPEN, new Infinity(MINUS));

    private final Type type;

    public Delimiter(Type type, Component value) {
        super(value);
        this.type = type;
    }

    public boolean isOpen() {
        return type == Type.OPEN;
    }

    public boolean isClosed() {
        return type == Type.CLOSED;
    }

    public enum Type implements GenericType {
        OPEN,
        CLOSED
    }

    @Override
    public GenericType getType() {
        return type;
    }
}
