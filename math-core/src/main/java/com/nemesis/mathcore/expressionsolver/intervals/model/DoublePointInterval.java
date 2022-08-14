package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.intervals.utils.DoublePointIntervalStringifier;
import com.nemesis.mathcore.expressionsolver.models.Stringable;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

import static com.nemesis.mathcore.expressionsolver.intervals.model.DoublePointInterval.Type.*;
import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.*;
import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.Type.CLOSED;
import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.Type.OPEN;


public class DoublePointInterval implements GenericInterval {

    private static final Map<Pair<Delimiter.Type, Delimiter.Type>, Type> intervalTypeMapping = new HashMap<>();

    static {
        intervalTypeMapping.put(Pair.of(OPEN, OPEN), STRICTLY_BETWEEN);
        intervalTypeMapping.put(Pair.of(OPEN, CLOSED), LEFT_STRICTLY_BETWEEN);
        intervalTypeMapping.put(Pair.of(CLOSED, OPEN), RIGHT_STRICTLY_BETWEEN);
        intervalTypeMapping.put(Pair.of(CLOSED, CLOSED), BETWEEN);
    }

    protected final String variable;
    private final Delimiter leftDelimiter;
    private final Delimiter rightDelimiter;
    private final Type type;

    public DoublePointInterval(String variable, Delimiter leftDelimiter, Delimiter rightDelimiter) {

        if (leftDelimiter.getComponent().compareTo(rightDelimiter.getComponent()) >= 0) {
            throw new IllegalArgumentException("Left delimiter must be less than right delimiter");
        }

        this.type = resolveType(leftDelimiter, rightDelimiter);
        this.variable = variable;
        this.leftDelimiter = leftDelimiter;
        this.rightDelimiter = rightDelimiter;
    }

    public DoublePointInterval(String variable, Type type, Component leftValue, Component rightValue) {

        if (leftValue.compareTo(rightValue) >= 0) {
            throw new IllegalArgumentException("Left delimiter must be less than right delimiter");
        }

        this.type = type;
        this.variable = variable;
        this.leftDelimiter = type.getLeftDelimiter(leftValue);
        this.rightDelimiter = type.getRightDelimiter(rightValue);
    }

    @Override
    public String getVariable() {
        return variable;
    }

    public Type getType() {
        return type;
    }

    public Delimiter getLeftDelimiter() {
        return new Delimiter(leftDelimiter.getType(), leftDelimiter.getComponent());
    }

    public Delimiter getRightDelimiter() {
        return new Delimiter(rightDelimiter.getType(), rightDelimiter.getComponent());
    }

    public NumericDomain getDomain() {
        return NumericDomain.R;
    }

    @Override
    public boolean contains(Component c) {

        final boolean leftCheck;
        if (leftDelimiter.getType() == OPEN) {
            leftCheck = c.compareTo(leftDelimiter.getComponent()) > 0;
        } else {
            leftCheck = c.compareTo(leftDelimiter.getComponent()) >= 0;
        }

        final boolean rightCheck;
        if (rightDelimiter.getType() == OPEN) {
            rightCheck = c.compareTo(rightDelimiter.getComponent()) < 0;
        } else {
            rightCheck = c.compareTo(rightDelimiter.getComponent()) <= 0;
        }

        return leftCheck && rightCheck;
    }

    @Override
    public GenericInterval getClone() {
        return new DoublePointInterval(variable, this.getLeftDelimiter(), this.getRightDelimiter());
    }

    private Type resolveType(Delimiter leftDelimiter, Delimiter rightDelimiter) {

        if (rightDelimiter.getComponent() instanceof Infinity && leftDelimiter.getComponent() instanceof Infinity) {
            return Type.FOR_EACH;
        }

        if (rightDelimiter.getComponent() instanceof Infinity) {
            if (leftDelimiter.getType() == OPEN) {
                return Type.GREATER_THAN;
            }
            if (leftDelimiter.getType() == Delimiter.Type.CLOSED) {
                return Type.GREATER_THAN_OR_EQUALS;
            }
        }

        if (leftDelimiter.getComponent() instanceof Infinity) {
            if (rightDelimiter.getType() == OPEN) {
                return Type.LESS_THAN;
            }
            if (rightDelimiter.getType() == CLOSED) {
                return Type.LESS_THAN_OR_EQUALS;
            }
        }

        return intervalTypeMapping.get(Pair.of(leftDelimiter.getType(), rightDelimiter.getType()));
    }

    @Override
    public String toString() {
        return DoublePointIntervalStringifier.stringhify(this, Stringable::toString);
    }

    @Override
    public String toLatex() {
        return DoublePointIntervalStringifier.stringhify(this, Stringable::toLatex);
    }

    @Override
    public int compareTo(GenericInterval o) {
        if (o instanceof DoublePointInterval dpi) {
            return leftDelimiter.getComponent().compareTo(dpi.getLeftDelimiter().getComponent());
        } else if (o instanceof SinglePointInterval spi) {
            return leftDelimiter.getComponent().compareTo(spi.getPoint().getComponent());
        } else if (o instanceof NoPointInterval) {
            return 1;
        } else {
            throw new UnexpectedComponentTypeException("Unexpected type [" + o.getClass() + "]");
        }
    }


    public enum Type implements GenericIntervalType {

        STRICTLY_BETWEEN(
                2,
                OPEN,
                OPEN,
                "%s " + LT.toString() + " %s " + LT.toString() + " %s , %2$s ∈ %4$s",
                "%s " + LT.toLatex() + " %s " + LT.toLatex() + " %s , %2$s \\in %4$s"
        ),
        LEFT_STRICTLY_BETWEEN(
                2,
                OPEN,
                CLOSED,
                "%s " + LT.toString() + " %s " + LTE.toString() + " %s , %2$s ∈ %4$s",
                "%s " + LT.toLatex() + " %s " + LTE.toLatex() + " %s , %2$s \\in %4$s"
        ),
        RIGHT_STRICTLY_BETWEEN(
                2,
                CLOSED,
                OPEN,
                "%s " + LTE.toString() + " %s " + LT.toString() + " %s , %2$s ∈ %4$s",
                "%s " + LTE.toLatex() + " %s " + LT.toLatex() + " %s , %2$s \\in %4$s"
        ),
        BETWEEN(
                2,
                CLOSED,
                CLOSED,
                "%s " + LTE.toString() + " %s " + LTE.toString() + " %s , %2$s ∈ %4$s",
                "%s " + LTE.toLatex() + " %s " + LTE.toLatex() + " %s , %2$s \\in %4$s"
        ),

        GREATER_THAN(
                1,
                OPEN,
                OPEN,
                "%s " + GT.toString() + " %s , %1$s ∈ %3$s",
                "%s " + GT.toLatex() + " %s , %1$s \\in %3$s"
        ),
        GREATER_THAN_OR_EQUALS(
                1,
                CLOSED,
                OPEN,
                "%s " + GTE.toString() + " %s , %1$s ∈ %3$s",
                "%s " + GTE.toLatex() + " %s , %1$s \\in %3$s"
        ),
        LESS_THAN(
                1,
                OPEN,
                OPEN,
                "%s " + LT.toString() + " %s , %1$s ∈ %3$s",
                "%s " + LT.toLatex() + " %s , %1$s \\in %3$s"
        ),
        LESS_THAN_OR_EQUALS(
                1,
                OPEN,
                CLOSED,
                "%s " + LTE.toString() + " %s , %1$s ∈ %3$s",
                "%s " + LTE.toLatex() + " %s , %1$s \\in %3$s"
        ),
        FOR_EACH(
                0,
                OPEN,
                OPEN,
                "∀ %s ∈ %s",
                "\\forall %s \\in %s"
        );

        private final int delimiters;
        private final String stringPattern;
        private final String latexPattern;
        private final Delimiter.Type leftDelimiterType;
        private final Delimiter.Type rightDelimiterType;

        Type(int delimiters, Delimiter.Type leftDelimiterType, Delimiter.Type rightDelimiterType, String stringPattern, String latexPattern) {
            this.delimiters = delimiters;
            this.leftDelimiterType = leftDelimiterType;
            this.rightDelimiterType = rightDelimiterType;
            this.stringPattern = stringPattern;
            this.latexPattern = latexPattern;
        }

        @Override
        public String toString() {
            return stringPattern;
        }

        @Override
        public String toLatex() {
            return latexPattern;
        }

        public int getDelimiters() {
            return delimiters;
        }

        public Delimiter getLeftDelimiter(Component component) {
            return new Delimiter(this.leftDelimiterType, component);
        }

        public Delimiter getRightDelimiter(Component component) {
            return new Delimiter(this.rightDelimiterType, component);
        }
    }

    public enum NumericDomain implements Stringable {
        N("ℕ", "\\mathbb{N}"),
        Z("ℤ", "\\mathbb{Z}"),
        R("ℝ", "\\mathbb{R}");

        private final String stringSymbol;
        private final String latexSymbol;

        NumericDomain(String stringSymbol, String latexSymbol) {
            this.stringSymbol = stringSymbol;
            this.latexSymbol = latexSymbol;
        }

        @Override
        public String toString() {
            return stringSymbol;
        }

        @Override
        public String toLatex() {
            return latexSymbol;
        }

    }
}
