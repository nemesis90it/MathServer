package com.nemesis.mathcore.expressionsolver.monomial;

import com.nemesis.mathcore.expressionsolver.components.Exponential;
import com.nemesis.mathcore.expressionsolver.components.Factor;
import com.nemesis.mathcore.expressionsolver.stringbuilder.ExpressionBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LiteralPart extends TreeSet<Exponential> implements Comparable<LiteralPart> {

    public LiteralPart(Set<Exponential> numeratorExponentialSet) {
        super(numeratorExponentialSet);
    }

    public LiteralPart getClone() {
        TreeSet<Exponential> clonedSet = this.stream().map(Exponential::getClone).collect(Collectors.toCollection(TreeSet::new));
        return new LiteralPart(clonedSet);
    }

    @Override
    public String toString() {
        return this.stream().map(Exponential::toString).reduce("", ExpressionBuilder::product);
    }

    @Override
    public int compareTo(LiteralPart other) {

        Iterator<Exponential> thisIterator = this.iterator();
        Iterator<Exponential> otherIterator = other.iterator();

        while (thisIterator.hasNext()) {
            Exponential thisExponential = thisIterator.next();
            if (otherIterator.hasNext()) {
                Exponential otherExponential = otherIterator.next();
                final int currentComparison = thisExponential.compareTo(otherExponential);
                if (currentComparison != 0) {
                    return currentComparison;
                }
            } else {
                return -1; // thisExponential > null, then this > other, then reverse
            }
        }

        if (otherIterator.hasNext()) {
            return 1; // null < otherExponential, then this < other, then reverse
        } else {
            return 0; // All elements are same, then this = other
        }
    }


    public Factor.Classifier getClassifier() {
        return new LiteralPartClassifier(this);
    }

    @Data
    private static class LiteralPartClassifier extends Factor.Classifier {

        private Set<Factor.Classifier> classifiers;

        public LiteralPartClassifier(LiteralPart literalPart) {
            super(null);
            this.classifiers = literalPart.stream().map(Exponential::classifier).collect(Collectors.toSet());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            LiteralPartClassifier other = (LiteralPartClassifier) o;
            return classifiers.isEmpty() || other.getClassifiers().isEmpty() || Objects.equals(classifiers, other.classifiers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), classifiers);
        }
    }


}
