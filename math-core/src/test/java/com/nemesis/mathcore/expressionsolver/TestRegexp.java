/***
 * Rules:
 *
 * For negative number: '-a' must be written as '(0-a)';
 * Operators equitable priority are associative to the right.
 */

package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.utils.Constants;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegexp {

    public static void main(String[] args) throws IOException {


        Pattern isExpressionPattern = Pattern.compile(Constants.START_WITH_PARENTHESIS_REGEX);
        Matcher isExpressionMatcher = isExpressionPattern.matcher("(2+1)/3");
        if (isExpressionMatcher.matches()) {
            int currentIndex = isExpressionMatcher.end(1);
            System.out.println(currentIndex);
        } else System.out.println("Not match");

//        String decimal = "0.01";
//        System.out.println(decimal.matches("^-?[0-9]+\\.[0-9]*0+"));
//        decimal = "0.001";
//        System.out.println(decimal.matches("^-?[0-9]+\\.[0-9]*0+"));
//        decimal = "0.0010";
//        System.out.println(decimal.matches("^-?[0-9]+\\.[0-9]*0+"));
//        decimal = "0.00100";
//        System.out.println(decimal.matches("^-?[0-9]+\\.[0-9]*0+"));

//        String decimalexp_1 = "2.0^3.0";
//        String decimalexp_2 = "2^3.0";
//        String decimalexp_3 = "2^-3.0";
//        String decimalexp_4 = "10^3";
//        String decimalexp_5 = "2^-10";
//
//        String regex = "^[0-9]+(\\.[0-9]+)?\\^-?[0-9]+(\\.[0-9]+)?";
//
//        System.out.println(decimalexp_1.matches(regex));
//        System.out.println(decimalexp_2.matches(regex));
//        System.out.println(decimalexp_3.matches(regex));
//        System.out.println(decimalexp_4.matches(regex));
//        System.out.println(decimalexp_5.matches(regex));
//
//
//        String decimalexp_6 = "2^2^2)";
//        String decimalexp_8 = "2^(-10)";
//        String decimalexp_9 = "2^(2^2)";
//
//        String regex_2 = "^[0-9]+(\\.[0-9]+)?\\^.*";
//
//        System.out.println(decimalexp_6.matches(regex_2));
//        System.out.println(decimalexp_8.matches(regex_2));
//        System.out.println(decimalexp_9.matches(regex_2));
//
//        String regex_3 ="^[0-9]+(\\.[0-9]+)?\\^\\(.*\\)";
//
//        System.out.println(decimalexp_8.matches(regex_3));
//        System.out.println(decimalexp_9.matches(regex_3));

    }

}
