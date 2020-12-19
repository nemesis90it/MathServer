package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.components.Factor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class RationalFunction {

    private final SortedSet<Factor> numerator;
    private final SortedSet<Factor> denominator;

    public <T extends Factor> RationalFunction(Set<T> numerator, Set<T> denominator) {
        this.numerator = new TreeSet<>(numerator);
        this.denominator = new TreeSet<>(denominator);
    }

}
