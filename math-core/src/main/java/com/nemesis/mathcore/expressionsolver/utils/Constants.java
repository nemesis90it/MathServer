package com.nemesis.mathcore.expressionsolver.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Constants {

    public static final BigDecimal MINUS_ONE_DECIMAL = new BigDecimal("-1");
    public static final BigInteger MINUS_ONE_INTEGER = new BigInteger("-1");
    public static final int SCALE = 10;
    public static final MathContext MATH_CONTEXT = new MathContext(SCALE, RoundingMode.HALF_EVEN);

    public static final BigDecimal NEP_NUMBER = new BigDecimal(Math.E);
    public static final BigDecimal PI = new BigDecimal(Math.PI);

    public static final char E_CHAR = 'e';
    public static final char PI_CHAR = 'π';
    public static final char FOURTH_ROOT_CHAR = '∜';
    public static final char CUBE_ROOT_CHAR = '∛';
    public static final char SQUARE_ROOT_CHAR = '√';

    public static final String INFINITY = "∞";
    public static final String NEG_INFINITY = "-" + INFINITY;
    public static final String INDETERMINATE = "indeterminate";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String MINUS = "-";
    public static final String PLUS = "+";

    public static final String START_WITH_GENERIC_NUM_REGEX = "^([0-9]+(\\.[0-9]+)?).*";
    public static final String IS_GENERIC_NUM_REGEX = "^([0-9]+(\\.[0-9]+)?)";
    public static final String START_WITH_PARENTHESIS_REGEX = "^\\(.*";

    public static final String IS_ZERO_REGEXP = "^\\(*0+(\\.0+)?\\)*$";
    public static final String IS_ONE_REGEXP = "^\\(*1+(\\.0+)?\\)*$";
    public static final String IS_MINUS_ONE_REGEXP = "^\\(*-1+(\\.0+)?\\)*$";
    public static final String DERIVATIVE_INPUT_REGEX = "^D\\[(.+),([a-z])\\]$";
}
