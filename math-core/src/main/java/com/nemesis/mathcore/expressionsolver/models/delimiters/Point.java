package com.nemesis.mathcore.expressionsolver.models.delimiters;

import com.nemesis.mathcore.expressionsolver.components.Component;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
public class Point extends GenericDelimiter {

    public Point(Component value) {
        super(value);
    }

}
