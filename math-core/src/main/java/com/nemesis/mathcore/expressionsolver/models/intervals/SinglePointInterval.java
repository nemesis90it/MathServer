package com.nemesis.mathcore.expressionsolver.models.intervals;

import lombok.Data;

import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.EQ;
import static com.nemesis.mathcore.expressionsolver.models.RelationalOperator.NEQ;

@Data
public class SinglePointInterval implements GenericInterval {

    private final String variable;
    private final Point point;
    private final Type type;

    public SinglePointInterval(String variable, Point point) {
        this.type = resolveType(point);
        this.variable = variable;
        this.point = point;
    }

    private Type resolveType(Point point) {
        return point.getType() == Point.Type.EQUALS ? Type.EQUALS : Type.NOT_EQUALS;
    }

    @Override
    public String toString() {
        return String.format(type.toString(), variable);
    }

    @Override
    public String toLatex() {
        return String.format(type.toLatex(), variable);
    }

    public enum Type implements GenericIntervalType {

        EQUALS(
                "%s " + EQ.toString() + " %s",
                "%s " + EQ.toLatex() + " %s"
        ),
        NOT_EQUALS(
                "%s " + NEQ.toString() + " %s",
                "%s " + NEQ.toLatex() + " %s"
        );

        private final String stringPattern;
        private final String latexPattern;

        Type(String stringPattern, String latexPattern) {
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
    }
}
