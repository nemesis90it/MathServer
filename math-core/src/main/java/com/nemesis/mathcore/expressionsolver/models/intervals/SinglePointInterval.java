package com.nemesis.mathcore.expressionsolver.models.intervals;

import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
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
        return String.format(type.toString(), variable, point.getValue().toString());
    }

    @Override
    public String toLatex() {
        return String.format(type.toLatex(), variable, point.getValue().toLatex());
    }

    @Override
    public int compareTo(GenericInterval o) {
        if (o instanceof DoublePointInterval dpi) {
            return this.getPoint().getValue().compareTo(dpi.getLeftDelimiter().getValue());
        } else if (o instanceof SinglePointInterval spi) {
            return this.getPoint().getValue().compareTo(spi.getPoint().getValue());
        } else if (o instanceof NoPointInterval) {
            return 1;
        } else {
            throw new IllegalArgumentException("Unexpected type [" + o.getClass() + "]");
        }
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
