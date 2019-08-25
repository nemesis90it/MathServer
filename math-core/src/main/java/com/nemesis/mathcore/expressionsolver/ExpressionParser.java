package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.models.Number;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nemesis.mathcore.expressionsolver.models.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.NEP_NUMBER;

/*
         Expression ::= Term+Expression
         Expression ::= Term-Expression
         Expression ::= Term
         Term ::= Factor*Term
         Term ::= Factor/Term
         Term ::= Factor
         Factor ::= Exponential
         Factor ::= (Expression)
         Factor ::= Logarithm
         Factor ::= Number
         Factor ::= Factorial
         Exponential ::= Number^Exponential
         Exponential ::= Number^Number
         Exponential ::= Number^(Expression)
         Exponential ::= (Expression)^Number
         Exponential ::= (Expression)^(Expression)
         Factorial ::= (Expression)!
         Factorial ::= Logarithm!
         Factorial ::= Number!
         Factorial ::= Factorial!
         Logarithm ::= log(Expresson)
         Logarithm ::= ln(Expresson)
         Number ::= ∀ n ∊ ℝ | ⅇ | π

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
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
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

        // Expression ::= Term
        if (currentIndex == expression.length()) {
            return new Expression(term, ExpressionOperator.NONE, null);
        }

        ExpressionOperator expressionOperator;

        // Expression ::= Term + Expression
        // Expression ::= Term - Expression
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

        // Term ::= Factor
        if (currentIndex == expression.length()) {
            return new Term(factor, TermOperator.NONE, null);
        }

        TermOperator termOperator;

        // Term ::= Factor * Term
        // Term ::= Factor / Term
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


    // Factor ::= Exponential
    // Factor ::= (Expression)
    // Factor ::= Logarithm
    // Factor ::= Number
    // Factor ::= Factorial
    private Factor getFactor() {

        String toParse = expression.substring(currentIndex);

        Factor factor = null;

        List<Supplier<Factor>> parsers = new LinkedList<>();
        parsers.add(() -> this.getExponential());
        parsers.add(() -> this.getExpressionAsFactor(toParse));
        parsers.add(() -> this.getLogarithm(toParse));
        parsers.add(() -> this.getNumber(toParse));

        int i = 0;
        while (factor == null && i < parsers.size()) {
            factor = parsers.get(i).get();
            i++;
        }

        if (factor == null) {
            throw new UnsupportedOperationException("Expression [" + this.expression + "] is not supported");
        }

        Factor factorial = null;
        if (!(factor instanceof Exponential)) {
            factorial = this.getFactorial(factor);
        }

        return factorial == null ? factor : factorial;

    }


    private Exponential getExponential() {

        String toParse = expression.substring(currentIndex);

        if (!toParse.contains("^")) {
            return null;
        }

        Exponential exponential;

        Sign sign;
        if (toParse.startsWith("-")) {
            sign = MINUS;
            toParse = toParse.substring(1);
        } else {
            sign = PLUS;
        }

        // Exponential ::= Number^Exponential
        Matcher expCase1Matcher = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_1_REGEX).matcher(toParse);
        if (expCase1Matcher.matches()) {
            Number base = new Number(expCase1Matcher.group(1));
            ExpressionParser expressionParser = new ExpressionParser(expCase1Matcher.group(3));
            Exponential exponent = expressionParser.getExponential();
            exponential = new Exponential(sign, base, exponent);

            int baseChars = expCase1Matcher.end(1);
            int operatorChars = 1;
            int exponentChars = expressionParser.currentIndex;
            this.currentIndex += (baseChars + operatorChars + exponentChars);

            return exponential;
        }

        // Exponential ::= Number^Number
        Matcher expCase2Matcher = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_2_REGEX).matcher(toParse);
        if (expCase2Matcher.matches()) {
            Number base = new Number(expCase2Matcher.group(1));
            Number exponent = new Number(expCase2Matcher.group(3));
            exponential = new Exponential(sign, base, exponent);
            currentIndex += expCase2Matcher.end(3);
            return exponential;
        }

        // Exponential ::= Number^(Expression)
        Matcher expCase3Matcher = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_3_REGEX).matcher(toParse);
        if (expCase3Matcher.matches()) {
            Number base = new Number(expCase3Matcher.group(1));
            currentIndex += (1 + expCase3Matcher.end(1));
            Factor exponent = this.getExpressionAsFactor(expCase3Matcher.group(3));
            exponential = new Exponential(sign, base, exponent);
            return exponential;
        }

        // Exponential ::= (Expression)^Number
        if (Pattern.compile(Constants.START_WITH_EXPRESSION_REGEX).matcher(toParse).matches()) {
            int closedParIndex = SyntaxUtils.getClosedParenthesisIndex(toParse, null);
            int nextIndex = closedParIndex + 1;
            if (nextIndex < toParse.length() && toParse.charAt(nextIndex) == '^') {
                Pattern expCase4Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_4_REGEX);
                Matcher expCase4Matcher = expCase4Pattern.matcher(toParse);
                if (expCase4Matcher.matches()) {
                    Factor base = this.getExpressionAsFactor(toParse);
                    String exponentAsString = expCase4Matcher.group(1);
                    Number exponent = new Number(exponentAsString);
                    exponential = new Exponential(sign, base, exponent);
                    currentIndex += (1 + exponentAsString.length());
                    return exponential;
                }
            }
        }

        if (Pattern.compile(Constants.START_WITH_EXPRESSION_REGEX).matcher(toParse).matches()) {
            int closedParIndex = SyntaxUtils.getClosedParenthesisIndex(toParse, null);
            int nextIndex = closedParIndex + 1;
            if (nextIndex < toParse.length() && toParse.charAt(nextIndex) == '^') {
                Pattern expCase5Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_5_REGEX);
                Matcher expCase5Matcher = expCase5Pattern.matcher(toParse);
                if (expCase5Matcher.matches()) {
                    Factor base = this.getExpressionAsFactor(toParse);
                    currentIndex += 1;
                    Factor exponent = this.getExpressionAsFactor(expCase5Matcher.group(1));
                    exponential = new Exponential(sign, base, exponent);
                    return exponential;
                }
            }
        }

        return null;
    }

    // Logarithm ::= log(Expresson)
    // Logarithm ::= ln(Expresson)
    private Factor getLogarithm(String toParse) {

        if (toParse.startsWith("-log")) {
            currentIndex += 4;
            Factor argument = this.getExpressionAsFactor(toParse.substring(4));
            return new Logarithm(MINUS, BigDecimal.TEN, argument);
        }
        if (toParse.startsWith("log")) {
            currentIndex += 3;
            Factor argument = this.getExpressionAsFactor(toParse.substring(3));
            return new Logarithm(PLUS, BigDecimal.TEN, argument);
        }
        if (toParse.startsWith("-ln")) {
            currentIndex += 3;
            Factor argument = this.getExpressionAsFactor(toParse.substring(3));
            return new Logarithm(MINUS, NEP_NUMBER, argument);
        }
        if (toParse.startsWith("ln")) {
            currentIndex += 2;
            Factor argument = this.getExpressionAsFactor(toParse.substring(2));
            return new Logarithm(PLUS, NEP_NUMBER, argument);
        }
        return null;
    }

    private Factor getExpressionAsFactor(String toParse) {

        Pattern isExpressionPattern = Pattern.compile(Constants.START_WITH_EXPRESSION_REGEX);
        Matcher isExpressionMatcher = isExpressionPattern.matcher(toParse);
        if (isExpressionMatcher.matches()) {
            int indexOfOpenParenthesis = toParse.indexOf('(');
            int indexOfClosedParenthesis = SyntaxUtils.getClosedParenthesisIndex(toParse, indexOfOpenParenthesis);
            String parenthesisContent = toParse.substring(indexOfOpenParenthesis + 1, indexOfClosedParenthesis);
            Expression absExpression = expressionParser.parse(parenthesisContent);
            currentIndex += indexOfClosedParenthesis + 1;
            Sign sign = toParse.startsWith("-") ? MINUS : PLUS;
            return new Expression(sign, absExpression);
        }
        return null;
    }

    private Factor getNumber(String toParse) {

        Sign sign = toParse.startsWith("-") ? MINUS : PLUS;

        if (sign.equals(MINUS)) {
            toParse = toParse.substring(1);
            currentIndex++;
        }

        Pattern startWithNumberPattern = Pattern.compile(Constants.IS_GENERIC_NUM_REGEX);
        Matcher startWithNumberMatcher = startWithNumberPattern.matcher(toParse);
        if (startWithNumberMatcher.matches()) {
            currentIndex += startWithNumberMatcher.end(1);
            return new Number(sign, new BigDecimal(startWithNumberMatcher.group(1)));
        }
        return null;
    }

    // Factorial ::= (Expression)!
    // Factorial ::= Logarithm!
    // Factorial ::= Number!
    // Factorial ::= Factorial!
    private Factor getFactorial(Factor factor) {

        if (currentIndex < expression.length() && expression.charAt(currentIndex) == '!') {

            Sign sign = PLUS;
            if (factor.getSign().equals(MINUS)) {
                sign = MINUS;
                factor.setSign(PLUS);
            }

            ++currentIndex;
            Factorial factorial = new Factorial(factor);
            while (currentIndex < expression.length() && expression.charAt(currentIndex) == '!') {
                ++currentIndex;
                factorial = new Factorial(factorial);
            }
            if (sign.equals(MINUS)) {
                factorial.setSign(MINUS);
            }
            return factorial;
        }

        return null;
    }
}

