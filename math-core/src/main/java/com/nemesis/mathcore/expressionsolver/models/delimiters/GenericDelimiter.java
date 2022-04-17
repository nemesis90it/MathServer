package com.nemesis.mathcore.expressionsolver.models.delimiters;

import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.Getter;

@Getter
public abstract class GenericDelimiter implements Comparable<Delimiter> {

    protected final Component component;

    protected GenericDelimiter(Component value) {
        this.component = value;
    }

    @Override
    public final int compareTo(Delimiter o) {
        return component.compareTo(o.getComponent());
    }
}
