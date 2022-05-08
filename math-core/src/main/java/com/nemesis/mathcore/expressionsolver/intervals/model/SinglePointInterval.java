package com.nemesis.mathcore.expressionsolver.intervals.model;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.models.Stringable;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
import lombok.Data;

@Data
public class SinglePointInterval implements GenericInterval {

    private final String variable;
    private final Point point;
    private Type type;

    public SinglePointInterval(String variable, Point point, Type type) {

        this.variable = variable;
        this.point = point;
        this.type = type;
    }

    public Point getPoint() {
        return new Point(point.getComponent());
    }

    @Override
    public String toString() {
        return String.format(type.toString(), variable, point.getComponent().toString());
    }

    @Override
    public String toLatex() {
        return String.format(type.toLatex(), variable, point.getComponent().toLatex());
    }

    @Override
    public int compareTo(GenericInterval o) {

        return switch (this.type) {
            case EQUALS -> switch (o) {
                case DoublePointInterval other -> this.getPoint().getComponent().compareTo(other.getLeftDelimiter().getComponent());
                case SinglePointInterval other -> switch (other.getType()) {
                    case EQUALS -> this.getPoint().getComponent().compareTo(other.getPoint().getComponent());
                    case NOT_EQUALS -> -1;
                };
                case NoPointInterval ignored -> 1;
                case null, default -> throw new UnexpectedComponentTypeException("Unexpected type [" + (o != null ? o.getClass() : null) + "]");
            };
            case NOT_EQUALS -> switch (o) {
                case DoublePointInterval ignored -> 1;
                case SinglePointInterval other -> switch (other.getType()) {
                    case EQUALS -> 1;
                    case NOT_EQUALS -> this.getPoint().getComponent().compareTo(other.getPoint().getComponent());
                };
                case NoPointInterval ignored -> 1;
                case null, default -> throw new UnexpectedComponentTypeException("Unexpected type [" + (o != null ? o.getClass() : null) + "]");
            };
        };

    }

    @Override
    public boolean contains(Component c) {
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

    @Override
    public GenericInterval getClone() {
        return new SinglePointInterval(variable, this.getPoint(), type);
    }

    public enum Type implements Stringable {
        EQUALS("%s = %s", "%s = %s"),
        NOT_EQUALS("%s ≠ %s", "%s \\neq %s");

        private final String stringPattern;
        private final String latexPattern;

        Type(String stringPattern, String latexPattern) {

            this.stringPattern = stringPattern;
            this.latexPattern = latexPattern;
        }

        @Override
        public String toLatex() {
            return latexPattern;
        }

        @Override
        public String toString() {
            return stringPattern;
        }
    }
}
