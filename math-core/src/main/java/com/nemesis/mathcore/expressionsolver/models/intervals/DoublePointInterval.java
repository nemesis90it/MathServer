package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.models.Stringable;
import lombok.Data;

import java.util.function.Function;

import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.*;
import static com.nemesis.mathcore.expressionsolver.models.intervals.Delimiter.Type.CLOSED;
import static com.nemesis.mathcore.expressionsolver.models.intervals.Delimiter.Type.OPEN;

@Data
public class DoublePointInterval implements GenericInterval {

    private final String variable;
    private final Delimiter leftDelimiter;
    private final Delimiter rightDelimiter;
    private final Type type;

    public DoublePointInterval(String variable, Delimiter leftDelimiter, Delimiter rightDelimiter) {
        this.type = resolveType(leftDelimiter, rightDelimiter);
        this.variable = variable;
        this.leftDelimiter = leftDelimiter;
        this.rightDelimiter = rightDelimiter;
    }

    public DoublePointInterval(String variable, Type type, Component leftValue, Component rightValue) {
        this.type = type;
        this.variable = variable;
        this.leftDelimiter = type.getLeftDelimiter(leftValue);
        this.rightDelimiter = type.getRightDelimiter(rightValue);
    }

    private Type resolveType(Delimiter leftDelimiter, Delimiter rightDelimiter) {

        if (rightDelimiter.getValue() instanceof Infinity && leftDelimiter.getValue() instanceof Infinity) {
            return Type.FOR_EACH;
        }

        if (rightDelimiter.getValue() instanceof Infinity) {
            if (leftDelimiter.getType() == OPEN) {
                return Type.GREATER_THAN;
            }
            if (leftDelimiter.getType() == Delimiter.Type.CLOSED) {
                return Type.GREATER_THAN_OR_EQUALS;
            }
        }

        if (leftDelimiter.getValue() instanceof Infinity) {
            if (rightDelimiter.getType() == OPEN) {
                return Type.LESS_THAN;
            }
            if (rightDelimiter.getType() == Delimiter.Type.CLOSED) {
                return Type.LESS_THAN_OR_EQUALS;
            }
        }

        if (leftDelimiter.getType().equals(OPEN) && rightDelimiter.getType() == OPEN) {
            return Type.STRICTLY_BETWEEN;
        }

        if (leftDelimiter.getType().equals(OPEN) && rightDelimiter.getType() == Delimiter.Type.CLOSED) {
            return Type.LEFT_STRICTLY_BETWEEN;
        }

        if (leftDelimiter.getType().equals(Delimiter.Type.CLOSED) && rightDelimiter.getType() == OPEN) {
            return Type.RIGHT_STRICTLY_BETWEEN;
        }

        if (leftDelimiter.getType().equals(Delimiter.Type.CLOSED) && rightDelimiter.getType() == Delimiter.Type.CLOSED) {
            return Type.BETWEEN;
        }

        throw new IllegalStateException("Unexpected interval :" + "{" +
                "variable='" + variable + '\'' +
                ", leftDelimiter=" + leftDelimiter +
                ", rightDelimiter=" + rightDelimiter +
                '}');
    }

    @Override
    public String toString() {
        return this.toStringRepresentation(Stringable::toString);
    }

    @Override
    public String toLatex() {
        return this.toStringRepresentation(Stringable::toLatex);
    }

    private String toStringRepresentation(Function<Stringable, String> stringifyFunction) {
        return switch (type.getDelimiters()) {
            case 0 -> getNoDelimiterIntervalAsString(stringifyFunction);
            case 1 -> getSingleDelimiterIntervalAsString(leftDelimiter.getValue() instanceof Infinity ? rightDelimiter : leftDelimiter, stringifyFunction);
            case 2 -> getDoubleDelimiterIntervalAsString(stringifyFunction);
            default -> throw new IllegalStateException("Unexpected delimiters: " + type.getDelimiters());
        };
    }

    private String getNoDelimiterIntervalAsString(Function<Stringable, String> f) {
        return String.format(f.apply(type), variable);
    }

    private String getSingleDelimiterIntervalAsString(Delimiter delimiter, Function<Stringable, String> f) {
        return String.format(f.apply(type), variable, f.apply(delimiter.getValue()));
    }

    private String getDoubleDelimiterIntervalAsString(Function<Stringable, String> f) {
        return String.format(f.apply(type), f.apply(leftDelimiter.getValue()), variable, f.apply(rightDelimiter.getValue()));
    }

    @Override
    public int compareTo(GenericInterval o) {
        if (o instanceof DoublePointInterval dpi) {
            return leftDelimiter.getValue().compareTo(dpi.getLeftDelimiter().getValue());
        } else if (o instanceof SinglePointInterval spi) {
            return leftDelimiter.getValue().compareTo(spi.getPoint().getValue());
        } else if (o instanceof NoPointInterval) {
            return 1;
        } else {
            throw new IllegalArgumentException("Unexpected type [" + o.getClass() + "]");
        }
    }


    public enum Type implements GenericIntervalType {

        STRICTLY_BETWEEN(
                2,
                OPEN,
                OPEN,
                "%s " + LT.toString() + " %s " + LT.toString() + " %s",
                "%s " + LT.toLatex() + " %s " + LT.toLatex() + " %s"
        ),
        LEFT_STRICTLY_BETWEEN(
                2,
                OPEN,
                CLOSED,
                "%s " + LT.toString() + " %s " + LTE.toString() + " %s",
                "%s " + LT.toLatex() + " %s " + LTE.toLatex() + " %s"
        ),
        RIGHT_STRICTLY_BETWEEN(
                2,
                CLOSED,
                OPEN,
                "%s " + LTE.toString() + " %s " + LT.toString() + " %s",
                "%s " + LTE.toLatex() + " %s " + LT.toLatex() + " %s"
        ),
        BETWEEN(
                2,
                CLOSED,
                CLOSED,
                "%s " + LTE.toString() + " %s " + LTE.toString() + " %s",
                "%s " + LTE.toLatex() + " %s " + LTE.toLatex() + " %s"
        ),

        GREATER_THAN(
                1,
                OPEN,
                OPEN,
                "%s " + GT.toString() + " %s",
                "%s " + GT.toLatex() + " %s"
        ),
        GREATER_THAN_OR_EQUALS(
                1,
                CLOSED,
                OPEN,
                "%s " + GTE.toString() + " %s",
                "%s " + GTE.toLatex() + " %s"
        ),
        LESS_THAN(
                1,
                OPEN,
                OPEN,
                "%s " + LT.toString() + " %s",
                "%s " + LT.toLatex() + " %s"
        ),
        LESS_THAN_OR_EQUALS(
                1,
                OPEN,
                CLOSED,
                "%s " + LTE.toString() + " %s",
                "%s " + LTE.toLatex() + " %s"
        ),
        FOR_EACH(
                0,
                OPEN,
                OPEN,
                "for each %s",
                "\\forall %s"
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

        protected int getDelimiters() {
            return delimiters;
        }

        public Delimiter getLeftDelimiter(Component component) {
            return new Delimiter(this.leftDelimiterType, component);
        }

        public Delimiter getRightDelimiter(Component component) {
            return new Delimiter(this.rightDelimiterType, component);
        }
    }
}
