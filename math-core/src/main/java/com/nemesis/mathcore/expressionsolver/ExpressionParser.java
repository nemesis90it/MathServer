package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.models.Number;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

/*
         Expression ::= Term+Expression
         Expression ::= Term-Expression
         Expression ::= Term
         Term ::= Factor*Term
         Term ::= Factor/Term
         Term ::= Factor
         Factor ::= Exponential
         Factor ::= Logarithm
         Factor ::= (Expression)
         Factor ::= (Expression)!
         Factor ::= Number!
         Factor ::= Number
         Exponential ::= Number^Exponential
         Exponential ::= Number^Number
         Exponential ::= Number^(Expression)
         Exponential ::= (Expression)^Number
         Exponential ::= (Expression)^(Expression)
         Logarithm ::= log(Expresson)
         Logarithm ::= ln(Expresson)

*/

public class ExpressionParser {

    @FunctionalInterface
    interface InternalParser {
        Expression parse(String rawExpression);
    }

    private static InternalParser expressionParser = rawExpression -> (new ExpressionParser(rawExpression)).getExpression();

    private String expression;
    private int currentIndex = 0;

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = expressionParser.parse(expression).getValue();
        if (rawResult.toString().contains(".")) {
            char[] chars = rawResult.toString().toCharArray();
            int i = chars.length - 1;
            while (chars[i] == '0') {
                i--;
            }
            if (chars[i] == '.') {
                i--;
            }
            return new BigDecimal(new String(chars).substring(0, i + 1));
        } else {
            return rawResult;
        }
    }

    private ExpressionParser(String expression) {
        SyntaxUtils.checkParenthesis(expression);
        this.expression = expression;
    }

    private Expression getExpression() {

        if (expression.isEmpty()) {
            return new Expression(new Term(new Number("0")));
        }

        Term term = this.getTerm();

        // Expression = Term
        if (currentIndex == expression.length()) {
            return new Expression(term, ExpressionOperator.NONE, null);
        }

        ExpressionOperator expressionOperator;

        // Expression = Term + Expression
        // Expression = Term - Expression
        char expressionOperatorChar = expression.charAt(currentIndex);
        switch (expressionOperatorChar) {
            case '+':
                expressionOperator = ExpressionOperator.SUM;
                break;
            case '-':
                expressionOperator = ExpressionOperator.SUBSTRACT;
                break;
            default:
                return new Expression(term, ExpressionOperator.NONE, null);
        }

        ++currentIndex;
        Expression subExpression = this.getExpression();

        return new Expression(term, expressionOperator, subExpression);

    }

    private Term getTerm() {

        Factor factor = this.getFactor();

        // Term = Factor
        if (currentIndex == expression.length()) {
            return new Term(factor, TermOperator.NONE, null);
        }

        TermOperator termOperator;

        // Term = Factor * Term
        // Term = Factor / Term
        char termOperatorChar = expression.charAt(currentIndex);
        switch (termOperatorChar) {
            case '*':
                termOperator = TermOperator.MULTIPLY;
                break;
            case '/':
                termOperator = TermOperator.DIVIDE;
                break;
            default:
                return new Term(factor, TermOperator.NONE, null);
        }

        ++currentIndex;
        Term subTerm = this.getTerm();

        return new Term(factor, termOperator, subTerm);
    }


    private Factor getFactor() {

        String toParse = expression.substring(currentIndex);

        // Factor ::= Exponential
        Factor exponential = this.getExponential();
        if (exponential != null) {
            return exponential;
        }

        // Factor ::= Logarithm
        Factor logarithm = this.getLogarithm(toParse);
        if (logarithm != null) {
            return logarithm;
        }

        // Factor ::= (Expression)
        // Factor ::= (Expression)!
        Factor expressionAsFactor = this.getExpressionAsFactor(toParse);
        if (expressionAsFactor != null) {
            return expressionAsFactor;
        }

        // Factor ::= Number!
        Factor factorial = this.getFactorial(toParse);
        if (factorial != null) {
            return factorial;
        }

        // Factor ::= Number
        Factor number = this.getNumber(toParse);
        if (number != null) {
            return number;
        }

        throw new UnsupportedOperationException("Expression " + this.expression + " is not supported");
    }

    private Exponential getExponential() {

        String toParse = expression.substring(currentIndex);

        if (!toParse.contains("^")) {
            return null;
        }

        Exponential exponential;

        Sign sign = toParse.startsWith("-") ? Sign.MINUS : Sign.PLUS;

        // Exponential ::= Number^Exponential
        Pattern expCase1Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_1_REGEX);
        Matcher expCase1Matcher = expCase1Pattern.matcher(toParse);
        if (expCase1Matcher.matches()) {
            String baseAsString = expCase1Matcher.group(1);
            String exponentAsString = expCase1Matcher.group(3);
            ExpressionParser expressionParser = new ExpressionParser(exponentAsString);
            Exponential exponent = expressionParser.getExponential();
            exponential = new Exponential(sign, new Number(baseAsString), exponent);
            currentIndex += (expCase1Matcher.end(1) + 1 + expressionParser.currentIndex);
            return exponential;
        }

        // Exponential ::= Number^Number
        Pattern expCase2Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_2_REGEX);
        Matcher expCase2Matcher = expCase2Pattern.matcher(toParse);
        if (expCase2Matcher.matches()) {
            String baseAsString = expCase2Matcher.group(1);
            String exponentAsString = expCase2Matcher.group(3);
            exponential = new Exponential(sign, new Number(baseAsString), new Number(exponentAsString));
            currentIndex += expCase2Matcher.end(3);
            return exponential;
        }

        // Exponential ::= Number^(Expression)
        Pattern expCase3Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_3_REGEX);
        Matcher expCase3Matcher = expCase3Pattern.matcher(toParse);
        if (expCase3Matcher.matches()) {
            String baseAsString = expCase3Matcher.group(1);
            String exponentAsString = expCase3Matcher.group(3);
            exponential = new Exponential(sign, new Number(baseAsString), expressionParser.parse(exponentAsString));
            currentIndex += expCase3Matcher.end(3);
            return exponential;
        }

        // Exponential ::= (Expression)^Number
        Pattern expCase4Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_4_REGEX);
        Matcher expCase4Matcher = expCase4Pattern.matcher(toParse);
        if (expCase4Matcher.matches()) {
            String baseAsString = expCase4Matcher.group(1);
            String exponentAsString = expCase4Matcher.group(2);
            exponential = new Exponential(sign, expressionParser.parse(baseAsString), new Number(exponentAsString));
            currentIndex += expCase4Matcher.end(2);
            return exponential;
        }

        // Exponential ::= (Expression)^(Expression)
        Pattern expCase5Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_5_REGEX);
        Matcher expCase5Matcher = expCase5Pattern.matcher(toParse);
        if (expCase5Matcher.matches()) {
            String baseAsString = expCase5Matcher.group(1);
            String exponentAsString = expCase5Matcher.group(2);
            exponential = new Exponential(sign, expressionParser.parse(baseAsString), expressionParser.parse(exponentAsString));
            currentIndex += expCase5Matcher.end(2);
            return exponential;
        }

        return null;

    }

    private Factor getLogarithm(String toParse) {

        if (toParse.startsWith("-log")) {
            currentIndex += 4;
            Factor argument = this.getExpressionAsFactor(toParse.substring(4));
            return new Logarithm(Sign.MINUS, BigDecimal.TEN, argument);
        }
        if (toParse.startsWith("log")) {
            currentIndex += 3;
            Factor argument = this.getExpressionAsFactor(toParse.substring(3));
            return new Logarithm(Sign.PLUS, BigDecimal.TEN, argument);
        }
        if (toParse.startsWith("-ln")) {
            currentIndex += 3;
            Factor argument = this.getExpressionAsFactor(toParse.substring(3));
            return new Logarithm(Sign.MINUS, NEP_NUMBER, argument);
        }
        if (toParse.startsWith("ln")) {
            currentIndex += 2;
            Factor argument = this.getExpressionAsFactor(toParse.substring(2));
            return new Logarithm(Sign.PLUS, NEP_NUMBER, argument);
        }
        return null;
    }

    private Factor getExpressionAsFactor(String toParse) {

        Pattern isExpressionPattern = Pattern.compile(Constants.START_WITH_EXPRESSION_REGEX);
        Matcher isExpressionMatcher = isExpressionPattern.matcher(toParse);
        if (isExpressionMatcher.matches()) {
            int indexOfOpenParenthesis = toParse.indexOf('(');
            int indexOfClosedParenthesis = SyntaxUtils.getIndexOfClosedParenthesis(toParse, indexOfOpenParenthesis);
            String parenthesisContent = toParse.substring(indexOfOpenParenthesis + 1, indexOfClosedParenthesis);
            Expression absExpression = expressionParser.parse(parenthesisContent);
            currentIndex += indexOfClosedParenthesis + 1;

            Sign sign = toParse.startsWith("-") ? Sign.MINUS : Sign.PLUS;

            if (currentIndex < expression.length() && expression.charAt(currentIndex) == '!') {
                ++currentIndex;
                Factorial factorial = new Factorial(sign, absExpression);
                while (currentIndex < expression.length() && expression.charAt(currentIndex) == '!') {
                    ++currentIndex;
                    factorial = new Factorial(factorial);
                }
                return factorial;
            }

            return new Expression(sign, absExpression);
        }
        return null;
    }

    private Factor getFactorial(String toParse) {
        Pattern isFactorialPattern = Pattern.compile(Constants.IS_FACTORIAL_REGEX);
        Matcher isFactorialMatcher = isFactorialPattern.matcher(toParse);
        if (isFactorialMatcher.matches()) {
            currentIndex += (isFactorialMatcher.end(1) + 1);
            Sign sign = toParse.startsWith("-") ? Sign.MINUS : Sign.PLUS;
            Factorial factorial = new Factorial(sign, new Number(isFactorialMatcher.group(1)));
            while (currentIndex < expression.length() && expression.charAt(currentIndex) == '!') {
                ++currentIndex;
                factorial = new Factorial(factorial);
            }
            return factorial;
        }
        return null;
    }

    private Factor getNumber(String toParse) {
        Pattern startWithNumberPattern = Pattern.compile(Constants.IS_GENERIC_NUM_REGEX);
        Matcher startWithNumberMatcher = startWithNumberPattern.matcher(toParse);
        if (startWithNumberMatcher.matches()) {
            currentIndex += startWithNumberMatcher.end(1);
            return new Number(startWithNumberMatcher.group(1));
        }
        return null;
    }
}

