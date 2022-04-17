package com.nemesis.mathcore.expressionsolver.intervals.utils;

import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Constant;
import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.intervals.model.*;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.Type.CLOSED;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isInteger;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isNegative;

public class IntervalsIntersectionUtils {

    private static final Map<IntervalInputTypes<GenericInterval, GenericInterval>, BiFunction<GenericInterval, GenericInterval, GenericInterval>> intersectors = new LinkedHashMap<>();

    static {

        List<Class<? extends GenericInterval>> intervalTypes = Arrays.asList(
                N.class, Z.class, SubSetZ.class, DoublePointInterval.class, SinglePointInterval.class
        );

        List<BiFunction<GenericInterval, GenericInterval, GenericInterval>> intersectorsList = Arrays.asList(

                (n, n2) -> N.of(n.getVariable()),
                (n, z) -> N.of(n.getVariable()),
                (n, i) -> i,
                (n, d) -> intersect(n.getVariable(), (N) n, (DoublePointInterval) d),
                (n, s) -> intersect(n.getVariable(), (N) n, (SinglePointInterval) s),

                (z, n) -> N.of(z.getVariable()),
                (z, z2) -> Z.of(z.getVariable()),
                (z, i) -> i,
                (z, d) -> intersect(z.getVariable(), (Z) z, (DoublePointInterval) d),
                (z, d) -> intersect(z.getVariable(), (Z) z, (DoublePointInterval) d),
                (z, s) -> intersect(z.getVariable(), (Z) z, (SinglePointInterval) s),

                (i, n) -> i,
                (i, z) -> i,
                (i, i2) -> intersect(i.getVariable(), (SubSetZ) i, (SubSetZ) i2),
                (i, d) -> intersect(i.getVariable(), (Z) i, (DoublePointInterval) d),
                (i, s) -> intersect(i.getVariable(), (Z) i, (SinglePointInterval) s),

                (d, n) -> intersect(d.getVariable(), (N) n, (DoublePointInterval) d),
                (d, z) -> intersect(d.getVariable(), (Z) z, (DoublePointInterval) d),
                (d, i) -> intersect(d.getVariable(), (SubSetZ) i, (DoublePointInterval) d),
                (d, d2) -> intersect(d.getVariable(), (DoublePointInterval) d, (DoublePointInterval) d2),
                (d, s) -> intersect(d.getVariable(), (DoublePointInterval) d, (SinglePointInterval) s),

                (s, n) -> intersect(s.getVariable(), (N) n, (SinglePointInterval) s),
                (s, z) -> intersect(s.getVariable(), (Z) z, (SinglePointInterval) s),
                (s, i) -> intersect(s.getVariable(), (SubSetZ) i, (SinglePointInterval) s),
                (s, d) -> intersect(s.getVariable(), (DoublePointInterval) d, (SinglePointInterval) s),
                (s, s2) -> intersect(s.getVariable(), (SinglePointInterval) s, (SinglePointInterval) s2)
        );

        final Iterator<BiFunction<GenericInterval, GenericInterval, GenericInterval>> intersectorsIterator = intersectorsList.iterator();

        for (Class<? extends GenericInterval> type1 : intervalTypes) {
            for (Class<? extends GenericInterval> type2 : intervalTypes) {
                intersectors.put(new IntervalInputTypes<>(type1, type2), intersectorsIterator.next());
            }
        }
    }

    public static GenericInterval intersect(GenericInterval a, GenericInterval b) {
        return intersectors.get(new IntervalInputTypes<>(a.getClass(), b.getClass())).apply(a, b);
    }

    @Data
    private static class IntervalInputTypes<T extends GenericInterval, U extends GenericInterval> {
        private Class<T> a;
        private Class<U> b;

        public IntervalInputTypes(Class<? extends GenericInterval> a, Class<? extends GenericInterval> b) {
            this.a = (Class<T>) a;
            this.b = (Class<U>) b;
        }
    }

    private static GenericInterval intersect(String variable, N a, DoublePointInterval b) {

        /*
            CASE (1):  right delimiter of 'b' is negative: disjoint intervals:
                  |-------|        ||||||||
                  bl     br       0     +∞

            CASE (2): only right delimiter of 'b' is positive
                  |-----||||||||||||||
                  bl    0     br     +∞

            CASE (3): left (then also the right) delimiter of 'b' is positive (or zero):
                  ||||||||||||||
                  0   bl  br   +∞
         */

        // CASE (1)
        if (b.getRightDelimiter().getComponent().compareTo(Constant.ZERO) < 0) {
            return new NoPointInterval(variable);
        }

        Delimiter rightDelimiter = getFloorOfRightDelimiter(b.getRightDelimiter());

        // CASE (2)
        if (b.getLeftDelimiter().getComponent().compareTo(Constant.ZERO) < 0) {
            return new SubSetN(a.getVariable(), Delimiter.CLOSED_ZERO, rightDelimiter);
        }

        Delimiter leftDelimiter = getCeilOfLeftDelimiter(b.getLeftDelimiter());

        // CASE (3)
        if (b.getLeftDelimiter().getComponent().compareTo(Constant.ZERO) >= 0) {
            return new SubSetN(a.getVariable(), leftDelimiter, rightDelimiter);
        }

        throw new RuntimeException("Unexpected case intersecting N with DoublePointInterval");

    }


    private static GenericInterval intersect(String variable, N n, SinglePointInterval i) {
         /*
           CASE (1): single point is negative and type is "equals": disjoint intervals
                        |        ||||||||
                        i        0     +∞

           CASE (2): single point is negative and type is "not equals": intersection is N
                        -----O-----||||||||
                       -∞    i     0     +∞

           CASE (3): single point is zero or positive and type is "equals":
              - if i is integer: intersection is the single point
              - else they are disjoint intervals
                       ||||||||||||||
                       0     i     +∞

           CASE (4): single point is zero or positive and type is "not equals":
               - if i is integer: intersection is a single point interval of positive integer, of "not equals" type
               - else, intersection is N
                            ||||||||||||||
                            0           +∞       =>   ||||||O|||||||
                      ---------O---------             0     i     +∞
                     -∞        i        +∞

        */

        if (isNegative(i.getPoint().getComponent())) {
            return switch (i.getType()) {
                case EQUALS -> new NoPointInterval(variable);   // CASE (1)
                case NOT_EQUALS -> n;                           // CASE (2)
            };
        } else {
            return switch (i.getType()) {
                case EQUALS -> isInteger(i.getPoint().getComponent()) ? i : new NoPointInterval(variable);      // CASE (3)
                case NOT_EQUALS -> isInteger(i.getPoint().getComponent()) ? new GenericIntersection(n, i) : n;  // CASE (4)
            };
        }

    }

    private static GenericInterval intersect(String variable, Z a, DoublePointInterval b) {

        /*
                  ||||||||||||||||||||||||||
                 -∞       bl     br        +∞
         */

        Delimiter leftDelimiter = ObjectUtils.max(a.getLeftDelimiter(), b.getLeftDelimiter());
        leftDelimiter = getCeilOfLeftDelimiter(leftDelimiter);

        Delimiter rightDelimiter = ObjectUtils.min(a.getRightDelimiter(), b.getRightDelimiter());
        rightDelimiter = getFloorOfRightDelimiter(rightDelimiter);

        return new SubSetZ(variable, leftDelimiter, rightDelimiter);

    }

    private static GenericInterval intersect(String variable, Z a, SinglePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, SubSetN a, DoublePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, SubSetN a, SinglePointInterval b) {
        throw new UnsupportedOperationException("Not supported"); // TODO
    }

    private static GenericInterval intersect(String variable, SinglePointInterval a, SinglePointInterval b) {

        final Component aComponent = a.getPoint().getComponent();
        final Component bComponent = b.getPoint().getComponent();

        if (Objects.equals(aComponent.getValue(), bComponent.getValue()) && Objects.equals(a.getType(), b.getType())) {
            return new SinglePointInterval(variable, new Point(aComponent.getClone()), a.getType());
        } else {
            return new NoPointInterval(variable);
        }
    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, DoublePointInterval b) {

        if (IntervalsUtils.areAdjacent(a, b)) {
            Delimiter al = a.getLeftDelimiter();
            Delimiter ar = a.getRightDelimiter();
            Delimiter bl = b.getLeftDelimiter();
            Delimiter br = b.getRightDelimiter();

            if (ar.getComponent().compareTo(bl.getComponent()) == 0 && ar.isClosed() && bl.isClosed()) {
                return new SinglePointInterval(variable, new Point(ar.getComponent()), SinglePointInterval.Type.EQUALS);
            } else if (br.getComponent().compareTo(al.getComponent()) == 0 && br.isClosed() && al.isClosed()) {
                return new SinglePointInterval(variable, new Point(al.getComponent()), SinglePointInterval.Type.EQUALS);
            } else {
                throw new RuntimeException("Unexpected error: possible bug");
            }
        }

        final int leftDistance = b.getLeftDelimiter().getComponent().compareTo(a.getLeftDelimiter().getComponent());
        Delimiter leftDelimiter = leftDistance >= 0 ? b.getLeftDelimiter() : a.getLeftDelimiter();

        final int rightDistance = a.getRightDelimiter().getComponent().compareTo(b.getRightDelimiter().getComponent());
        Delimiter rightDelimiter = rightDistance < 0 ? a.getRightDelimiter() : b.getRightDelimiter();

        if (a instanceof SubSetZ || b instanceof SubSetZ) {

            leftDelimiter = getCeilOfLeftDelimiter(leftDelimiter);

            rightDelimiter = getFloorOfRightDelimiter(rightDelimiter);

            if (a instanceof SubSetN || b instanceof SubSetN) {
                return new SubSetN(variable, leftDelimiter, rightDelimiter);
            } else {
                return new SubSetZ(variable, leftDelimiter, rightDelimiter);
            }
        } else {
            return new DoublePointInterval(variable, leftDelimiter, rightDelimiter);
        }

    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, SinglePointInterval b) {

        if (IntervalsUtils.areAdjacent(a, b)) {
            return a;
        }

        throw new UnsupportedOperationException("Not implemented yet"); // TODO

    }


    private static Delimiter getFloorOfRightDelimiter(Delimiter rightDelimiter) {
        Component rightComponent = rightDelimiter.getComponent();
        if (!Objects.equals(rightComponent, new Infinity(Sign.PLUS)) && !isInteger(rightComponent)) {
            rightDelimiter = new Delimiter(CLOSED, rightComponent.getValue().setScale(0, RoundingMode.FLOOR));
        }
        return rightDelimiter;
    }

    private static Delimiter getCeilOfLeftDelimiter(Delimiter leftDelimiter) {
        Component leftComponent = leftDelimiter.getComponent();
        if (!Objects.equals(leftComponent, new Infinity(Sign.MINUS)) && !isInteger(leftComponent)) {
            leftDelimiter = new Delimiter(CLOSED, leftComponent.getValue().setScale(0, RoundingMode.CEILING));
        }
        return leftDelimiter;
    }
}
