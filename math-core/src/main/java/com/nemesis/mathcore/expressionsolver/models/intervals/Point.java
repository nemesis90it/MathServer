package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
public class Point extends GenericDelimiter {

    private final Type type;

    public Point(Component value, Type type) {
        super(value);
        this.type = type;
    }

    @Override
    public GenericDelimiter.GenericType getType() {
        return type;
    }

    public enum Type implements GenericType {
        EQUALS, NOT_EQUALS
    }
}
