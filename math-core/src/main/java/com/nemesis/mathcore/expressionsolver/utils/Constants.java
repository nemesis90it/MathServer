package com.nemesis.mathcore.expressionsolver.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Constants {

    public static final BigDecimal MINUS_ONE_DECIMAL = BigDecimal.valueOf(-1L);
    public static final BigInteger MINUS_ONE_INTEGER = BigInteger.valueOf(-1L);
    public static final int SCALE = 16;
    public static final MathContext MATH_CONTEXT = new MathContext(SCALE, RoundingMode.HALF_EVEN);

    public static final BigDecimal NEP_NUMBER = BigDecimal.valueOf(Math.E);
    public static final BigDecimal PI = BigDecimal.valueOf(Math.PI);

    public static final char E_CHAR = 'e';
    public static final char PI_CHAR = 'π';
    public static final char FOURTH_ROOT_CHAR = '∜';
    public static final char CUBE_ROOT_CHAR = '∛';
    public static final char SQUARE_ROOT_CHAR = '√';
    public static final char INFINITY = '∞';

    public static final String NEG_INFINITY = "-" + INFINITY;
    public static final String INDETERMINATE = "indeterminate";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String MINUS = "-";
    public static final String PLUS = "+";


    public static final String START_WITH_GENERIC_NUM_REGEX = "^([0-9]+(\\.[0-9]+)?).*";
    public static final String START_WITH_PARENTHESIS_REGEX = "^\\(.*";
    public static final String START_WITH_ROOT_FUNCTION_REGEX = "(^root\\(([0-9]+)-th,.+\\)).*";
    public static final String START_WITH_LOG_ARGUMENT_REGEX = "^(.+?),(.+).*";

    public static final String IS_GENERIC_NUM_REGEX = "^([0-9]+(\\.[0-9]+)?)";
    public static final String IS_ZERO_REGEXP = "^\\(*0(\\.0+)?\\)*$";
    public static final String IS_ONE_REGEXP = "^\\(*1(\\.0+)?\\)*$";
    public static final String IS_MINUS_ONE_REGEXP = "^\\(*-1(\\.0+)?\\)*$";
    public static final String DERIVATIVE_INPUT_REGEX = "^D\\[(.+),([a-z])\\]$";
}
