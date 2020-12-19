package com.nemesis.mathcore.expressionsolver.models.delimiters;

import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.Getter;

@Getter
public abstract class GenericDelimiter {

    protected final Component component;

    protected GenericDelimiter(Component value) {
        this.component = value;
    }

    public abstract GenericType getType();

    public interface GenericType {
    }
}
