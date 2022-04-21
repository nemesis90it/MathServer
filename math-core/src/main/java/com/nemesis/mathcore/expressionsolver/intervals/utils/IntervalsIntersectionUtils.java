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
                N.class, Z.class, DoublePointInterval.class, SinglePointInterval.class
        );

        List<BiFunction<GenericInterval, GenericInterval, GenericInterval>> intersectorsList = Arrays.asList(

                (n, n2) -> N.of(n.getVariable()),
                (n, z) -> N.of(n.getVariable()),
                (n, d) -> intersect(n.getVariable(), (N) n, (DoublePointInterval) d),
                (n, s) -> intersect(n.getVariable(), (N) n, (SinglePointInterval) s),

                (z, n) -> N.of(z.getVariable()),
                (z, z2) -> Z.of(z.getVariable()),
                (z, d) -> intersect(z.getVariable(), (Z) z, (DoublePointInterval) d),
                (z, s) -> intersect(z.getVariable(), (Z) z, (SinglePointInterval) s),

                (d, n) -> intersect(d.getVariable(), (N) n, (DoublePointInterval) d),
                (d, z) -> intersect(d.getVariable(), (Z) z, (DoublePointInterval) d),
                (d, d2) -> intersect(d.getVariable(), (DoublePointInterval) d, (DoublePointInterval) d2),
                (d, s) -> intersect(d.getVariable(), (DoublePointInterval) d, (SinglePointInterval) s),

                (s, n) -> intersect(s.getVariable(), (N) n, (SinglePointInterval) s),
                (s, z) -> intersect(s.getVariable(), (Z) z, (SinglePointInterval) s),
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

    private static GenericInterval intersect(String variable, N n, DoublePointInterval b) {

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
            return new SubSetN(n.getVariable(), Delimiter.CLOSED_ZERO, rightDelimiter);
        }

        Delimiter leftDelimiter = getCeilOfLeftDelimiter(b.getLeftDelimiter());

        // CASE (3)
        if (b.getLeftDelimiter().getComponent().compareTo(Constant.ZERO) >= 0) {
            return new SubSetN(n.getVariable(), leftDelimiter, rightDelimiter);
        }

        throw new RuntimeException("Unexpected case intersecting N with DoublePointInterval");

    }


    private static GenericInterval intersect(String variable, N n, SinglePointInterval s) {
         /*

            CASE (1): single point is negative:

                 - CASE (1a): type is "equals": disjoint intervals
                             |        ||||||||
                             s        0     +∞

                 - CASE (1b): type is "not equals": intersection is N
                        -----O-----||||||||
                       -∞    s     0     +∞

            CASE (2): single point is zero or positive:

                 - CASE (2a): 's' is integer and type is "not equals":
                    intersection is a single point interval of positive integer, of "not equals" type

                              ||||||||||||||
                              0           +∞       =>   ||||||O|||||||
                        ---------O---------             0     s     +∞
                       -∞        s        +∞

                 - CASE (2b): 's' is integer and type is "equals":
                    intersection is 's'

                 - CASE (2c): 's' is not integer and type is "not equals":
                    intersection is N

                 - CASE (2c): 's' is not integer and type is "equals":
                    disjoint intervals

        */

        Component pointComponent = s.getPoint().getComponent();

        if (isNegative(pointComponent)) {
            return switch (s.getType()) {
                case EQUALS -> new NoPointInterval(variable);   // CASE (1a)
                case NOT_EQUALS -> n;                           // CASE (1b)
            };
        } else {
            if (isInteger(pointComponent)) {
                return switch (s.getType()) {
                    case NOT_EQUALS -> new GenericIntersection(n, s);   // CASE (2a):  x ∈ ℕ, x ≠ s
                    case EQUALS -> s;                                   // CASE (2b)
                };
            } else {
                return switch (s.getType()) {
                    case NOT_EQUALS -> n;                           // CASE (2c);
                    case EQUALS -> new NoPointInterval(variable);   // CASE (2d)
                };
            }
        }
    }

    private static GenericInterval intersect(String variable, Z z, DoublePointInterval d) {

        /*
                  ||||||||||||||||||||||||||
                 -∞       dl     dr        +∞
         */

        Delimiter leftDelimiter = ObjectUtils.max(z.getLeftDelimiter(), d.getLeftDelimiter());
        leftDelimiter = getCeilOfLeftDelimiter(leftDelimiter);

        Delimiter rightDelimiter = ObjectUtils.min(z.getRightDelimiter(), d.getRightDelimiter());
        rightDelimiter = getFloorOfRightDelimiter(rightDelimiter);

        return new SubSetZ(variable, leftDelimiter, rightDelimiter);

    }

    private static GenericInterval intersect(String variable, Z z, SinglePointInterval s) {

        Component pointComponent = s.getPoint().getComponent();

        if (isInteger(pointComponent)) {
            return switch (s.getType()) {
                case NOT_EQUALS -> new GenericIntersection(z, s); // x ∈ ℤ, x ≠ s
                case EQUALS -> s;
            };
        } else {
            return switch (s.getType()) {
                case NOT_EQUALS -> z;
                case EQUALS -> new NoPointInterval(variable);
            };
        }

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
