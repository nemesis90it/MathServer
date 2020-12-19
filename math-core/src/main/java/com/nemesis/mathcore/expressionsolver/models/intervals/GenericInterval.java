package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.models.Stringable;

public interface GenericInterval extends Stringable, Comparable<GenericInterval> {

    String getVariable();

    interface GenericIntervalType extends Stringable {

    }
}
