package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.models.Stringable;

public interface GenericInterval extends Stringable, Comparable<GenericInterval> {

    String getVariable();

    boolean contains(Component c);

    interface GenericIntervalType extends Stringable {

    }
}
