package com.nemesis.mathcore.expressionsolver.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Constants {

    public static final BigDecimal MINUS_ONE = new BigDecimal("-1");
    public static final int SCALE = 20;
    public static final MathContext MATH_CONTEXT = new MathContext(SCALE, RoundingMode.HALF_EVEN);
    public static final BigDecimal NEP_NUMBER = new BigDecimal(Math.E);
    public static final BigDecimal PI = new BigDecimal(Math.PI);


    public static final String IS_GENERIC_NUM_REGEX = "^([0-9]+(\\.[0-9]+)?).*";
    public static final String START_WITH_EXPRESSION_REGEX = "^-?\\(.*";
    public static final String IS_RECURSIVE_EXPONENTIAL_REGEX = "^([0-9]+(\\.[0-9]+)?)\\^(-?[0-9]+\\^.*)";

}
