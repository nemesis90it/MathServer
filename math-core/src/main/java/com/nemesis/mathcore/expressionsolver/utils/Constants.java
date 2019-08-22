package com.nemesis.mathcore.expressionsolver.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Constants {

    public static final BigDecimal MINUS_ONE = new BigDecimal("-1");
    public static final int SCALE = 20;
    public static final MathContext MATH_CONTEXT = new MathContext(SCALE, RoundingMode.HALF_EVEN);
    public static final BigDecimal NEP_NUMBER = new BigDecimal(Math.E);


    public static final String IS_GENERIC_NUM_REGEX = "^([0-9]+(\\.[0-9]+)?).*";
    public static final String START_WITH_EXPRESSION_REGEX = "^-?\\((.+)\\).*";

    /*
         CASE 1: Exponential ::= Number^Exponential
         CASE 2: Exponential ::= Number^Number
         CASE 3: Exponential ::= Number^(Expression)
         CASE 4: Exponential ::= (Expression)^Number
         CASE 5: Exponential ::= (Expression)^(Expression)
    */

    public static final String IS_EXPONENTIAL_CASE_1_REGEX = "^(-?[0-9]+(\\.[0-9]+)?)\\^(-?[0-9]+\\^.+)";
    public static final String IS_EXPONENTIAL_CASE_2_REGEX = "^(-?[0-9]+(\\.[0-9]+)?)\\^(-?[0-9]+).*";
    public static final String IS_EXPONENTIAL_CASE_3_REGEX = "^(-?[0-9]+(\\.[0-9]+)?)\\^(-?\\([^)]*\\)).*";
    public static final String IS_EXPONENTIAL_CASE_4_REGEX = "^(-?\\([^)]*\\))\\^(-?[0-9]+).*";
    public static final String IS_EXPONENTIAL_CASE_5_REGEX = "^(-?\\(.+\\))\\^(-?\\(.+\\)).*";

    // TODO: manage sub-case for cases 3,4,5:      ((...) OPERATOR (...))
    // string ") OPERATOR (" must not be match
}
