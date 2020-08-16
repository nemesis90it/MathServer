package com.nemesis.mathcore.expressionsolver.models.delimiters;

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
    public Type getType() {
        return type;
    }

    public enum Type implements GenericType {
        EQUALS, NOT_EQUALS
    }
}
