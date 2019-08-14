package com.nemesis.mathcore.expressionsolver.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Constants {

    public static final BigDecimal MINUS_ONE = new BigDecimal("-1");
    public static final int SCALE = 20;
    public static final MathContext MATH_CONTEXT = new MathContext(SCALE, RoundingMode.HALF_EVEN);


    public static final String IS_GENERIC_NUM_REGEX = "^(-?[0-9]+(\\.[0-9]+)?).*";
    public static final String START_WITH_EXPRESSION_REGEX = "^-?\\((.+)\\).*";
    public static final String IS_FACTORIAL_REGEX = "^([0-9]+)!.*";

    /*
         CASE 1: Exponential ::= (+/-) Number^Number
         CASE 2: Exponential ::= (+/-) Number^(Expression)
         CASE 3: Exponential ::= (+/-) (Expression)^Number
         CASE 4: Exponential ::= (+/-) (Expression)^(Expression)
         CASE 5: Exponential ::= (+/-) Number^Exponential
    */

    public static final String IS_EXPONENTIAL_CASE_1_REGEX = "^(-?[0-9]+(\\.[0-9]+)?)\\^(-?[0-9]+).*";
    public static final String IS_EXPONENTIAL_CASE_2_REGEX = "^(-?[0-9]+(\\.[0-9]+)?)\\^(-?\\([^\\)]*\\)).*";
    public static final String IS_EXPONENTIAL_CASE_3_REGEX = "^(-?\\(.+\\))\\^(-?[0-9]+).*";
    public static final String IS_EXPONENTIAL_CASE_4_REGEX = "^(-?\\(.+\\))\\^(-?\\(.+\\)).*";
    public static final String IS_EXPONENTIAL_CASE_5_REGEX = "^(-?[0-9]+(\\.[0-9]+)?)\\^(.+\\^.+)";
}
