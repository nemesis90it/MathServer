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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static com.nemesis.mathcore.expressionsolver.intervals.model.SinglePointInterval.Type.EQUALS;
import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.Type.CLOSED;
import static com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter.Type.OPEN;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isInteger;
import static com.nemesis.mathcore.expressionsolver.utils.ComponentUtils.isNegative;

public class IntervalsIntersectionUtils {

    private static final Map<IntervalInputTypes<GenericInterval, GenericInterval>, BiFunction<GenericInterval, GenericInterval, GenericInterval>> intersectors = new LinkedHashMap<>();

    static {

        intersectors.put(new IntervalInputTypes<>(N.class, N.class), (n, n2) -> N.of(n.getVariable()));
        intersectors.put(new IntervalInputTypes<>(N.class, Z.class), (n, z) -> N.of(n.getVariable()));
        intersectors.put(new IntervalInputTypes<>(N.class, SubSetN.class), (n, d) -> intersect(n.getVariable(), (N) n, (SubSetN) d));
        intersectors.put(new IntervalInputTypes<>(N.class, SubSetZ.class), (n, d) -> intersect(n.getVariable(), (N) n, (SubSetZ) d));
        intersectors.put(new IntervalInputTypes<>(N.class, DoublePointInterval.class), (n, d) -> intersect(n.getVariable(), (N) n, (DoublePointInterval) d));
        intersectors.put(new IntervalInputTypes<>(N.class, SinglePointInterval.class), (n, s) -> intersect(n.getVariable(), (N) n, (SinglePointInterval) s));

        intersectors.put(new IntervalInputTypes<>(Z.class, N.class), (z, n) -> N.of(z.getVariable()));
        intersectors.put(new IntervalInputTypes<>(Z.class, Z.class), (z, z2) -> Z.of(z.getVariable()));
        intersectors.put(new IntervalInputTypes<>(Z.class, SubSetN.class), (z, d) -> intersect(z.getVariable(), (Z) z, (SubSetN) d));
        intersectors.put(new IntervalInputTypes<>(Z.class, SubSetZ.class), (z, d) -> intersect(z.getVariable(), (Z) z, (SubSetZ) d));
        intersectors.put(new IntervalInputTypes<>(Z.class, DoublePointInterval.class), (z, d) -> intersect(z.getVariable(), (Z) z, (DoublePointInterval) d));
        intersectors.put(new IntervalInputTypes<>(Z.class, SinglePointInterval.class), (z, s) -> intersect(z.getVariable(), (Z) z, (SinglePointInterval) s));

        intersectors.put(new IntervalInputTypes<>(SubSetN.class, N.class), (d, n) -> intersect(d.getVariable(), (N) n, (SubSetN) d));
        intersectors.put(new IntervalInputTypes<>(SubSetN.class, Z.class), (d, z) -> intersect(d.getVariable(), (Z) z, (SubSetN) d));
        intersectors.put(new IntervalInputTypes<>(SubSetN.class, SubSetN.class), (d, d2) -> intersect(d.getVariable(), (SubSetN) d, (SubSetN) d2));
        intersectors.put(new IntervalInputTypes<>(SubSetN.class, SubSetZ.class), (d, d2) -> intersect(d.getVariable(), (SubSetN) d, (SubSetZ) d2));
        intersectors.put(new IntervalInputTypes<>(SubSetN.class, DoublePointInterval.class), (d, d2) -> intersect(d.getVariable(), (SubSetN) d, (DoublePointInterval) d2));
        intersectors.put(new IntervalInputTypes<>(SubSetN.class, SinglePointInterval.class), (d, s) -> intersect(d.getVariable(), (SubSetN) d, (SinglePointInterval) s));

        intersectors.put(new IntervalInputTypes<>(SubSetZ.class, N.class), (d, n) -> intersect(d.getVariable(), (N) n, (SubSetZ) d));
        intersectors.put(new IntervalInputTypes<>(SubSetZ.class, Z.class), (d, z) -> intersect(d.getVariable(), (Z) z, (SubSetZ) d));
        intersectors.put(new IntervalInputTypes<>(SubSetZ.class, SubSetN.class), (d, d2) -> intersect(d.getVariable(), (SubSetZ) d, (SubSetN) d2));
        intersectors.put(new IntervalInputTypes<>(SubSetZ.class, SubSetZ.class), (d, d2) -> intersect(d.getVariable(), (SubSetZ) d, (SubSetZ) d2));
        intersectors.put(new IntervalInputTypes<>(SubSetZ.class, DoublePointInterval.class), (d, d2) -> intersect(d.getVariable(), (SubSetZ) d, (DoublePointInterval) d2));
        intersectors.put(new IntervalInputTypes<>(SubSetZ.class, SinglePointInterval.class), (d, s) -> intersect(d.getVariable(), (SubSetZ) d, (SinglePointInterval) s));

        intersectors.put(new IntervalInputTypes<>(DoublePointInterval.class, N.class), (d, n) -> intersect(d.getVariable(), (N) n, (DoublePointInterval) d));
        intersectors.put(new IntervalInputTypes<>(DoublePointInterval.class, Z.class), (d, z) -> intersect(d.getVariable(), (Z) z, (DoublePointInterval) d));
        intersectors.put(new IntervalInputTypes<>(DoublePointInterval.class, SubSetN.class), (d, d2) -> intersect(d.getVariable(), (DoublePointInterval) d, (SubSetN) d2));
        intersectors.put(new IntervalInputTypes<>(DoublePointInterval.class, SubSetZ.class), (d, d2) -> intersect(d.getVariable(), (DoublePointInterval) d, (SubSetZ) d2));
        intersectors.put(new IntervalInputTypes<>(DoublePointInterval.class, DoublePointInterval.class), (d, d2) -> intersect(d.getVariable(), (DoublePointInterval) d, (DoublePointInterval) d2));
        intersectors.put(new IntervalInputTypes<>(DoublePointInterval.class, SinglePointInterval.class), (d, s) -> intersect(d.getVariable(), (DoublePointInterval) d, (SinglePointInterval) s));

        intersectors.put(new IntervalInputTypes<>(SinglePointInterval.class, N.class), (s, n) -> intersect(s.getVariable(), (N) n, (SinglePointInterval) s));
        intersectors.put(new IntervalInputTypes<>(SinglePointInterval.class, Z.class), (s, z) -> intersect(s.getVariable(), (Z) z, (SinglePointInterval) s));
        intersectors.put(new IntervalInputTypes<>(SinglePointInterval.class, SubSetN.class), (s, d) -> intersect(s.getVariable(), (SubSetN) d, (SinglePointInterval) s));
        intersectors.put(new IntervalInputTypes<>(SinglePointInterval.class, SubSetZ.class), (s, d) -> intersect(s.getVariable(), (SubSetZ) d, (SinglePointInterval) s));
        intersectors.put(new IntervalInputTypes<>(SinglePointInterval.class, DoublePointInterval.class), (s, d) -> intersect(s.getVariable(), (DoublePointInterval) d, (SinglePointInterval) s));
        intersectors.put(new IntervalInputTypes<>(SinglePointInterval.class, SinglePointInterval.class), (s, s2) -> intersect(s.getVariable(), (SinglePointInterval) s, (SinglePointInterval) s2));

    }

    public static GenericInterval intersect(GenericInterval a, GenericInterval b) {
        IntervalInputTypes<GenericInterval, GenericInterval> key = new IntervalInputTypes<>(a.getClass(), b.getClass());
        return intersectors.get(key).apply(a, b);
    }

    @Data
    private static class IntervalInputTypes<T extends GenericInterval, U extends GenericInterval> {
        private Class<T> a;
        private Class<U> b;

        public IntervalInputTypes(Class<? extends GenericInterval> a, Class<? extends GenericInterval> b) {
            this.a = (Class<T>) a;
            this.b = (Class<U>) b;
        }

        @Override
        public String toString() {
            return "[" + a.getSimpleName() + "," + b.getSimpleName() + "]";
        }
    }

    private interface Intersector extends BiFunction<GenericInterval, GenericInterval, GenericInterval> {
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

                 - CASE (2d): 's' is not integer and type is "equals":
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
                    case NOT_EQUALS -> new Intersection(n, s);   // CASE (2a):  x ∈ ℕ, x ≠ s
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
                case NOT_EQUALS -> new Intersection(z, s); // x ∈ ℤ, x ≠ s
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

        boolean componentsAreEquals = Objects.equals(aComponent.getValue(), bComponent.getValue());
        boolean typesAreEquals = Objects.equals(a.getType(), b.getType());

        if (componentsAreEquals) {
            if (typesAreEquals) {
                return a.getClone();
            } else {
                return new NoPointInterval(variable);
            }
        } else {
            return switch (a.getType()) {
                case EQUALS -> switch (b.getType()) {
                    case EQUALS -> new NoPointInterval(variable);
                    case NOT_EQUALS -> a.getClone();
                };
                case NOT_EQUALS -> switch (b.getType()) {
                    case EQUALS -> b.getClone();
                    case NOT_EQUALS -> {
                        Delimiter delimiter1;
                        Delimiter delimiter2;
                        if (aComponent.compareTo(bComponent) < 0) {
                            delimiter1 = new Delimiter(OPEN, aComponent);
                            delimiter2 = new Delimiter(OPEN, bComponent);
                        } else {
                            delimiter1 = new Delimiter(OPEN, bComponent);
                            delimiter2 = new Delimiter(OPEN, aComponent);
                        }
                        DoublePointInterval i1 = new DoublePointInterval(variable, Delimiter.MINUS_INFINITY, delimiter1);
                        DoublePointInterval i2 = new DoublePointInterval(variable, delimiter1, delimiter2);
                        DoublePointInterval i3 = new DoublePointInterval(variable, delimiter2, Delimiter.PLUS_INFINITY);
                        yield new Union(i1, i2, i3);
                    }
                };
            };
        }
    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, DoublePointInterval b) {
        /*
            CASE (1a): x ≤ 1 ∩ x ≥ 1               -->     x = 1
            CASE (1b): x ≥ 1 ∩ x ≤ 1               -->     x = 1
            CASE (2a): x ≤ 10, x ∈ ℕ  ∩  x ≥ 2.2   -->     3 ≤ x ≤ 10, x ∈ ℕ
            CASE (2b): x ≤ 10, x ∈ ℤ  ∩  x ≥ -2.2   -->    -2 ≤ x ≤ 10, x ∈ ℤ
         */

        if (IntervalsUtils.areDisjoint(a, b)) {
            return new NoPointInterval(variable);
        }


        if (IntervalsUtils.areAdjacent(a, b)) {
            Delimiter al = a.getLeftDelimiter();
            Delimiter ar = a.getRightDelimiter();
            Delimiter bl = b.getLeftDelimiter();
            Delimiter br = b.getRightDelimiter();

            if (ar.getComponent().compareTo(bl.getComponent()) == 0 && ar.isClosed() && bl.isClosed()) {
                return new SinglePointInterval(variable, new Point(ar.getComponent()), EQUALS);     // CASE (1a)
            } else if (br.getComponent().compareTo(al.getComponent()) == 0 && br.isClosed() && al.isClosed()) {
                return new SinglePointInterval(variable, new Point(al.getComponent()), EQUALS);     // CASE (1b)
            } else {
                throw new RuntimeException("Unexpected error intersecting adjacent intervals: possible bug");
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
                return new SubSetN(variable, leftDelimiter, rightDelimiter); // CASE (2a)
            } else {
                return new SubSetZ(variable, leftDelimiter, rightDelimiter); // CASE (2b)
            }
        } else {
            return new DoublePointInterval(variable, leftDelimiter, rightDelimiter); // CASE (3)
        }

    }

    private static GenericInterval intersect(String variable, DoublePointInterval a, SinglePointInterval b) {

        /*
            - CASE (1): 'a' contains the point of 'b' and 'a' is a SubSetZ (or SubSetN)

                 - CASE (1a): 'b' is integer and is of type NOT_EQUALS:  al < x < b  ∪  b < x < ar , x ∈ ℤ  (or x ∈ ℕ)

                             ||||||||||||||
                            al           ar          =>   ||||||O||||||      =>     |||||O   U   O||||||
                          ---------O---------             al    b     ar           al    b       b   ar
                         -∞        b        +∞

                 - CASE (1b): 'b' is integer and is of type EQUALS:  intersection is 'b'

                 - CASE (1c): 'b' is not integer and is of type NOT_EQUALS: intersection is 'a'

                 - CASE (1d): 'b' is not integer and is of type EQUALS: intersection is void

            - CASE (2): 'a' contains the point of 'b' and 'a' is a continuous DoublePointInterval

                 - CASE (2a): 'b' is of type NOT_EQUALS: see CASE (1a), but x ∈ ℝ

                 - CASE (2b): 'b' is of type EQUALS:  intersection is 'b'

            - CASE (3):'a' not contains the point of 'b'

                 - CASE (3a): 'b' is of type NOT_EQUALS: intersection is 'a'

                 - CASE (3b): 'b' is of type EQUALS:  intersection is void

         */

        if (IntervalsUtils.areAdjacent(a, b)) {
            return a;
        }

        if (a.contains(b.getPoint().getComponent())) {
            if (a instanceof SubSetZ) {
                if (isInteger(b.getPoint().getComponent())) {
                    return switch (b.getType()) {
                        case NOT_EQUALS -> {    // CASE (1a):  al < x < bl  ∪  br < x < ar , x ∈ ℤ (or x ∈ ℕ)
                            Delimiter bl = new Delimiter(OPEN, b.getPoint().getComponent());
                            Delimiter br = new Delimiter(OPEN, b.getPoint().getComponent());
                            DoublePointInterval leftInterval;
                            DoublePointInterval rightInterval;
                            switch (a) {
                                case SubSetN subSetN -> {
                                    leftInterval = new SubSetN(variable, subSetN.getLeftDelimiter(), bl);
                                    rightInterval = new SubSetN(variable, br, subSetN.getRightDelimiter());
                                }
                                case SubSetZ subSetZ -> {
                                    leftInterval = new SubSetZ(variable, subSetZ.getLeftDelimiter(), bl);
                                    rightInterval = new SubSetZ(variable, br, subSetZ.getRightDelimiter());
                                }
                                default -> throw new IllegalStateException("Unexpected interval type: " + a.getClass().getSimpleName());
                            }
                            yield new Union(leftInterval, rightInterval);
                        }
                        case EQUALS -> b;   // CASE (1b)
                    };
                } else { // b is not integer
                    return switch (b.getType()) {
                        case NOT_EQUALS -> a;   // CASE (1c)
                        case EQUALS -> new NoPointInterval(variable);   // CASE 1d
                    };
                }
            } else { // 'a' is a continuous DoublePointInterval
                return switch (b.getType()) {
                    case NOT_EQUALS -> {    // CASE (2a):  al < x < bl  ∪  br < x < ar
                        Delimiter bl = new Delimiter(OPEN, b.getPoint().getComponent());
                        Delimiter br = new Delimiter(OPEN, b.getPoint().getComponent());
                        Delimiter al = a.getLeftDelimiter();
                        Delimiter ar = a.getRightDelimiter();

                        GenericInterval leftInterval;
                        GenericInterval rightInterval;

                        if (al.getComponent().compareTo(bl.getComponent()) == 0) {
                            leftInterval = new NoPointInterval(variable);
                        } else {
                            leftInterval = new DoublePointInterval(variable, al, bl);
                        }

                        if (ar.getComponent().compareTo(br.getComponent()) == 0) {
                            rightInterval = new NoPointInterval(variable);
                        } else {
                            rightInterval = new DoublePointInterval(variable, br, ar);
                        }

                        yield new Union(leftInterval, rightInterval);
                    }
                    case EQUALS -> b;   // CASE (2b)
                };
            }
        } else { // 'a' does not contains 'b'
            return switch (b.getType()) {
                case NOT_EQUALS -> a;   // CASE (3a)
                case EQUALS -> new NoPointInterval(variable);   // CASE (3b)
            };
        }

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
