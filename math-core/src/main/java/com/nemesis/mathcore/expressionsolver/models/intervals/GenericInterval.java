package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Stringable;

public interface GenericInterval extends Stringable, Comparable<GenericInterval> {

    String getVariable();
}
