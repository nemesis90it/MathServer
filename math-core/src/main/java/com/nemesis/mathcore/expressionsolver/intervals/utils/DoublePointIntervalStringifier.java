package com.nemesis.mathcore.expressionsolver.intervals.utils;

import com.nemesis.mathcore.expressionsolver.components.Infinity;
import com.nemesis.mathcore.expressionsolver.intervals.model.DoublePointInterval;
import com.nemesis.mathcore.expressionsolver.models.Stringable;
import com.nemesis.mathcore.expressionsolver.models.delimiters.Delimiter;

import java.util.function.Function;

public class DoublePointIntervalStringifier {

    public static String stringhify(DoublePointInterval interval, Function<Stringable, String> stringifyFunction) {
        return switch (interval.getType().getDelimiters()) {
            case 0 -> getNoDelimiterIntervalAsString(interval, stringifyFunction);
            case 1 -> {
                Delimiter delimiter = interval.getLeftDelimiter().getComponent() instanceof Infinity ? interval.getRightDelimiter() : interval.getLeftDelimiter();
                yield getSingleDelimiterIntervalAsString(interval, delimiter, stringifyFunction);
            }
            case 2 -> getDoubleDelimiterIntervalAsString(interval, stringifyFunction);
            default -> throw new IllegalStateException("Unexpected delimiters: " + interval.getType().getDelimiters());
        };
    }

    private static String getNoDelimiterIntervalAsString(DoublePointInterval interval, Function<Stringable, String> stringifier) {
        String pattern = stringifier.apply(interval.getType());
        String domainSymbol = stringifier.apply(interval.getDomain());
        return String.format(pattern, interval.getVariable(), domainSymbol);
    }

    private static String getSingleDelimiterIntervalAsString(DoublePointInterval interval, Delimiter delimiter, Function<Stringable, String> stringifier) {
        String pattern = stringifier.apply(interval.getType());
        String delimiterAsString = stringifier.apply(delimiter.getComponent());
        String domainSymbol = stringifier.apply(interval.getDomain());
        return String.format(pattern, interval.getVariable(), delimiterAsString, domainSymbol);
    }

    private static String getDoubleDelimiterIntervalAsString(DoublePointInterval interval, Function<Stringable, String> stringifier) {
        String pattern = stringifier.apply(interval.getType());
        String leftDelimiter = stringifier.apply(interval.getLeftDelimiter().getComponent());
        String rightDelimiter = stringifier.apply(interval.getRightDelimiter().getComponent());
        String domainSymbol = stringifier.apply(interval.getDomain());
        return String.format(pattern, leftDelimiter, interval.getVariable(), rightDelimiter, domainSymbol);
    }

}
