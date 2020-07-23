package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.Getter;

@Getter
public abstract class GenericDelimiter {

    protected final Component value;

    protected GenericDelimiter(Component value) {
        this.value = value;
    }

    public abstract GenericType getType();

    public interface GenericType {
    }
}
