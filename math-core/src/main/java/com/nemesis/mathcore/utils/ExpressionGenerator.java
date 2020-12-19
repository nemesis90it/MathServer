package com.nemesis.mathcore.utils;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Point;
import com.nemesis.mathcore.expressionsolver.models.intervals.*;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.IntervalsUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.nemesis.mathcore.expressionsolver.models.delimiters.Point.Type.EQUALS;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUM;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.DIVIDE;
import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.MULTIPLY;

public class ExpressionGenerator {

    private static final Random r = new Random();
    public static final int MAX_RAND_VALUE = 1000;

    public static void main(String[] args) throws IOException {
        PrintWriter writer = new PrintWriter("C:\\development\\tests.txt", StandardCharsets.UTF_8);
        int i = 0;
        final int tests = 100;
        while (i < tests) {
            try {
                Expression expression = generateExpression(0);
                if (!expression.toString().contains("null")) {
                    writer.println("Generated test (" + ++i + "/" + tests + "): " + expression);
                    System.out.println("Generated test (" + ++i + "/" + tests + "): " + expression);
                }
            } catch (Exception e) {
            }
        }
        writer.close();
    }


    public static Expression generateExpression(int depth) {

        Term t = generateTerm(depth, Domain.NOT_ZERO.getInterval());

        depth++;

        switch (r.nextInt(3 * depth)) {
            case 0 -> {
                return new Expression(t, SUBTRACT, generateExpression(depth));
            }
            case 1 -> {
                return new Expression(t, SUM, generateExpression(depth));
            }
            default -> {
                return new Expression(t);
            }
        }
    }

    private static Term generateTerm(int depth, GenericInterval domain) {

        Factor f = null;
        do {
            try {
                f = generateFactor(depth, Domain.R.getInterval());
            } catch (ArithmeticException e) {
            }
        } while (f == null);

        depth++;

        switch (r.nextInt(3 * depth)) {
            case 0 -> { // DIVIDE (divisor cannot be zero)
                int finalDepth = depth;
                Supplier<Term> termGenerator = () -> {
                    final GenericInterval intersectionDomain = IntervalsUtils.intersect(domain, Domain.NOT_ZERO.getInterval());
                    return generateTerm(finalDepth, intersectionDomain);
                };
//                Predicate<Term> loopCondition = tern -> tern.isScalar() && tern.getValue().compareTo(BigDecimal.ZERO) == 0;
                Term subTerm = generateParallel(termGenerator, null);
                return new Term(f, DIVIDE, subTerm);
            }
            case 1 -> { // MULTIPLY
                return new Term(f, MULTIPLY, generateTerm(depth, domain));
            }
            default -> { // No operator, this term will be a leaf
                return new Term(f);
            }
        }
    }

    private static Factor generateFactor(int depth, GenericInterval domain) {

        depth++;
        Sign sign = generateSign();

        switch (r.nextInt(4 * depth)) {
            case 0, 1 -> {
                return generateExponential(depth, sign, domain);
            }
            default -> {
                return generateBase(depth, sign, domain);
            }
        }
    }

    private static Exponential generateExponential(int depth, Sign sign, GenericInterval domain) {

        depth++;
        Sign baseSign = generateSign();
        int finalDepth = depth;

        Supplier<Base> baseGenerator = () -> {
            final GenericInterval intersectionDomain = IntervalsUtils.intersect(domain, Domain.STRICTLY_POSITIVE.getInterval());
            return generateBase(finalDepth, baseSign, intersectionDomain);
        };
//        Predicate<Base> baseLoopCondition = base -> base.isScalar() && base.getValue().compareTo(BigDecimal.ZERO) <= 0;
        Base base = generateParallel(baseGenerator, null);

        Supplier<Factor> exponentGenerator = () -> {
            final GenericInterval intersectionDomain = IntervalsUtils.intersect(domain, Domain.Z.getInterval());
            return generateFactor(finalDepth, intersectionDomain);
        };
        Predicate<Factor> loopCondition = exponent -> exponent.isScalar() && exponent.getValue().compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) >= 0;
        Factor exponent = generateParallel(exponentGenerator, loopCondition);

        return new Exponential(sign, base, exponent); // TODO: check domain, given as parameter
    }


    private static Base generateBase(int depth, Sign sign, GenericInterval domain) {

        switch (r.nextInt(10 * depth)) {
            case 0 -> {
                return generateWrappedExpression(depth, sign, domain);
            }
            case 1, 2 -> {
                return generateMathFunction(depth, sign, domain);
            }
//            case 3, 4, 5, 6, 7 -> {
//                return generateVariable(depth, sign);
//            }
            case 8, 9 -> {
                return generateFactorial(depth, sign, domain);
            }
            default -> {
                return generateConstant(depth, sign, domain);
            }
        }
    }

    private static Constant generateConstant(int depth, Sign sign, GenericInterval domain) {

        if (domain instanceof IntegerNumbersInterval) {
            return new Constant(r.nextInt(2 * MAX_RAND_VALUE) - MAX_RAND_VALUE);
        } else if (domain instanceof NaturalNumbersInterval) {
            return new Constant(r.nextInt(MAX_RAND_VALUE));
        } else if (domain instanceof SinglePointInterval s) {
            final BigDecimal pointValue = s.getPoint().getComponent().getValue();
            switch (s.getPoint().getType()) {
                case EQUALS -> {
                    return new Constant(pointValue);
                }
                case NOT_EQUALS -> {
                    int randomInt;
                    BigDecimal randomBigDecimal;
                    do {
                        randomInt = r.nextInt(2 * MAX_RAND_VALUE) - MAX_RAND_VALUE;
                        randomBigDecimal = new BigDecimal(randomInt);
                    } while (randomBigDecimal.compareTo(pointValue) == 0);
                    return new Constant(randomBigDecimal);
                }
                default -> throw new IllegalStateException("Unexpected value: " + s.getPoint().getType());
            }
        } else if (domain instanceof DoublePointInterval d) {
            BigDecimal value;
            switch (r.nextInt(3)) {
                case 0 -> {
                    value = generateRandomRealConstant(d);
                }
                case 1, 2 -> {
                    value = generateRandomIntegerConstant(d);
                }
                default -> throw new RuntimeException("Unexpected random value");
            }
            return new Constant(sign, value);
        } else {
            throw new RuntimeException("Unexpected domain " + domain); // TODO: manage null domain
        }
    }

    /*
    if (MathUtils.isIntegerValue(pointValue)) {
                    return new Constant(pointValue);
                } else {
                    throw new RuntimeException("No value can be generated for specified domain");
                }
     */

    private static BigDecimal generateRandomIntegerConstant(DoublePointInterval domain) {

        final Constant minRandValue = new Constant(MINUS, MAX_RAND_VALUE);
        final Constant maxRandValue = new Constant(MAX_RAND_VALUE);

        final Component leftValue = domain.getLeftDelimiter().getComponent();
        final Component rightValue = domain.getRightDelimiter().getComponent();

        /*
            Add 1 to min value to avoid to generate left delimiters values (calling "r.nextInt", min value is inclusive
            and this is wrong for OPEN intervals)
        */
        int min = leftValue.compareTo(minRandValue) < 0 ?
                minRandValue.getValue().intValueExact() : leftValue.getValue().intValueExact() + 1;

        int max = rightValue.compareTo(maxRandValue) < 0 ?
                rightValue.getValue().intValueExact() : maxRandValue.getValue().intValueExact();

        final int randomInt = r.nextInt(max - min) + min;
        return BigDecimal.valueOf(randomInt);
    }

    private static BigDecimal generateRandomRealConstant(DoublePointInterval domain) {

        final Constant minRandValue = new Constant(MINUS, MAX_RAND_VALUE);
        final Constant maxRandValue = new Constant(MAX_RAND_VALUE);

        final Component leftComponent = domain.getLeftDelimiter().getComponent();
        final Component rightComponent = domain.getRightDelimiter().getComponent();

        final Component leftValue = leftComponent.equals(new Infinity(MINUS)) ? new Constant(Integer.MIN_VALUE) : leftComponent;
        final Component rightValue = rightComponent.equals(new Infinity(PLUS)) ? new Constant(Integer.MAX_VALUE) : rightComponent;

        /*
            Add 1 to min value to avoid to generate left delimiters values (calling "Math.random", min value is inclusive
            and this is wrong for OPEN intervals)
        */
        int min = leftValue.compareTo(minRandValue) < 0 ?
                minRandValue.getValue().intValueExact() : leftValue.getValue().intValueExact() + 1;

        int max = rightValue.compareTo(maxRandValue) < 0 ?
                rightValue.getValue().intValueExact() : maxRandValue.getValue().intValueExact();

        final double randomRealNumber = (Math.random() * (max - min)) + min;
        return BigDecimal.valueOf(randomRealNumber);
    }

    private static Variable generateVariable(int depth, Sign sign) {
//        return new Variable(sign, (char) (r.nextInt(26) + 97)); // a-z
//        return new Variable(sign, (char) (r.nextInt(3) + 120)); // x,y,z
        return new Variable(sign, 'x');
    }

    private static Factorial generateFactorial(int depth, Sign sign, GenericInterval domain) {
        depth++;

        int finalDepth = depth;
        Supplier<Factor> generator = () -> {
            final GenericInterval intersectionDomain = IntervalsUtils.intersect(domain, Domain.N.getInterval());
            return generateFactor(finalDepth, intersectionDomain);
        };

//        Predicate<Factor> loopCondition = argument -> argument.isScalar() &&
//                (!MathUtils.isIntegerValue(argument.getValue()) || argument.getValue().compareTo(BigDecimal.ZERO) < 0);

        Factor argument = generateParallel(generator, null);

        return new Factorial(sign, argument);
    }

    private static MathFunction generateMathFunction(int depth, Sign sign, GenericInterval domain) {

        depth++;

        switch (r.nextInt(3)) {
            case 0 -> {
                return generateMathUnaryFunction(depth, sign);
            }
            case 1, 2 -> {
                return generateLogarithm(depth, sign);
            }
//            case 2 -> { // TODO
//                return generateRootFunction(depth, sign);
//            }
            default -> {
                throw new RuntimeException("Unexpected random value");
            }
        }
    }

    private static RootFunction generateRootFunction(int depth, Sign sign) {

        depth++;
        int index = r.nextInt(2) + 1; // TODO test greater index

        int finalDepth = depth;
        Supplier<Factor> generator = () -> generateFactor(finalDepth, Domain.POSITIVE.getInterval());

        Predicate<Factor> loopCondition;
        if (index % 2 == 0) {
            loopCondition = ComponentUtils::isNegative;
        } else {
            loopCondition = null;
        }

        Factor argument = generateParallel(generator, loopCondition);
        return new RootFunction(sign, index, argument);
    }

    private static MathFunction generateMathUnaryFunction(int depth, Sign sign) {

        BigDecimal argument = BigDecimal.valueOf(Math.random() * 10 - 5);

//        List<Method> methods = new ArrayList<>();
//        Collections.addAll(methods, TrigonometricFunctions.class.getDeclaredMethods());
//        Method method = methods.get(r.nextInt(methods.size()));

        List<String> methods = new ArrayList<>();
        methods.add("sin");
        methods.add("cos");
        methods.add("sec");
        methods.add("tan");
        methods.add("tg");
        methods.add("cotan");
        methods.add("cot");
        methods.add("cotg");
        methods.add("ctg");
        methods.add("cosec");
        methods.add("csc");

        Method method;
        try {
            final String methodName = methods.get(r.nextInt(methods.size()));
            method = TrigonometricFunctions.class.getDeclaredMethod(methodName, BigDecimal.class);
        } catch (NoSuchMethodException e) {
            System.out.println("Error occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }

        final UnaryOperator<BigDecimal> mathUnaryFunction = n -> {
            try {
                return (BigDecimal) method.invoke(null, n);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println("Error occurred: " + e.getMessage());
                throw new RuntimeException(e);
            }
        };

        return new MathUnaryFunction(sign, mathUnaryFunction, method.getName(), new Constant(argument)); // TODO use factor as argument
    }

    private static Logarithm generateLogarithm(int depth, Sign sign) {
        BigDecimal base;

        switch (r.nextInt(2)) {
            case 0 -> {
                base = Constants.NEP_NUMBER;
//                base = BigDecimal.TEN; // TODO
            }
            case 1 -> {
                base = Constants.NEP_NUMBER;
            }
            default -> {
                throw new RuntimeException("Unexpected random value");
            }
        }

        Sign argumentSign = generateSign();

        Supplier<WrappedExpression> generator = () -> generateWrappedExpression(depth, argumentSign, Domain.STRICTLY_POSITIVE.getInterval());
        Predicate<WrappedExpression> loopCondition = argument -> argument.isScalar() &&
                (argument.getValue().compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0
                        || argument.getValue().compareTo(BigDecimal.ZERO) <= 0);

        WrappedExpression argument = generateParallel(generator, loopCondition);

        return new Logarithm(sign, base, argument);
    }

    private static WrappedExpression generateWrappedExpression(int depth, Sign sign, GenericInterval domain) {

        depth++;
        Expression expr = generateExpression(depth);

        switch (r.nextInt(4)) {
            case 0, 1, 2 -> {
                return new ParenthesizedExpression(sign, expr);
            }
            case 3 -> {
                return new AbsExpression(sign, expr);
            }
            default -> {
                throw new RuntimeException("Unexpected random value");
            }
        }
    }

    private static Sign generateSign() {
        Sign sign;
        if (r.nextInt(2) == 0) {
            sign = MINUS;
        } else {
            sign = PLUS;
        }
        return sign;
    }


    private static <T extends Component> T generateParallel(final Supplier<T> generator, final Predicate<T> loopCondition) {

        AtomicReference<T> componentRef = new AtomicReference<>();
        final long end = System.currentTimeMillis() + 10000;

        Runnable r = () -> {
            T localComponent;
            boolean checkTimeout, notGeneratedYet, isNotAcceptable;
            do {
                localComponent = generator.get();
                checkTimeout = System.currentTimeMillis() < end;
                notGeneratedYet = componentRef.get() == null;
                isNotAcceptable = loopCondition != null && loopCondition.test(localComponent);
            } while (checkTimeout && notGeneratedYet && isNotAcceptable);
            if (System.currentTimeMillis() > end) {
                System.out.println(Thread.currentThread().getName() + " - Timeout");
                return;
            }
            final T component = componentRef.get();
            if (component == null) {
                componentRef.set(localComponent);
            }

        };

        int cores = Runtime.getRuntime().availableProcessors();

        ThreadGroup tg = new ThreadGroup("generator");
        Set<Thread> threads = new HashSet<>();
        for (int i = 0; i < cores; i++) {
            threads.add(new Thread(tg, r));
        }
        threads.forEach(Thread::start);

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

//        List<Runnable> runners = new ArrayList<>();
//        for (int i = 0; i < cores; i++) {
//            runners.add(r);
//        }
//        runners.stream().forEach(Runnable::run);

        final T component = componentRef.get();
        return component;
    }

    private enum Domain {
        POSITIVE(new DoublePointInterval("x", Delimiter.CLOSED_ZERO, Delimiter.PLUS_INFINITY)),
        STRICTLY_POSITIVE(new DoublePointInterval("x", Delimiter.OPEN_ZERO, Delimiter.PLUS_INFINITY)),
        NEGATIVE(new DoublePointInterval("x", Delimiter.MINUS_INFINITY, Delimiter.CLOSED_ZERO)),
        STRICTLY_NEGATIVE(new DoublePointInterval("x", Delimiter.MINUS_INFINITY, Delimiter.OPEN_ZERO)),
        R(new DoublePointInterval("x", Delimiter.MINUS_INFINITY, Delimiter.PLUS_INFINITY)),
        Z(new IntegerNumbersInterval("x")),
        N(new NaturalNumbersInterval("x")),
        ZERO(new SinglePointInterval("x", new Point(new Constant(0), EQUALS))),
        NOT_ZERO(new SinglePointInterval("x", new Point(new Constant(0), Point.Type.NOT_EQUALS))),
        VOID(new NoPointInterval("x"));

        private GenericInterval interval;

        Domain(GenericInterval interval) {
            this.interval = interval;
        }

        public GenericInterval getInterval() {
            return interval;
        }
    }
}
