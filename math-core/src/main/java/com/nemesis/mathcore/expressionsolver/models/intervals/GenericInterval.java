package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Stringable;

public interface GenericInterval extends Stringable, Comparable<GenericInterval> {

    @Override
    default int compareTo(GenericInterval other) {
        return this.toString().compareTo(other.toString()); // TODO
    }

}
