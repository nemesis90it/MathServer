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
import static com.nemesis.mathcore.expressionsolver.utils.Constants.PI;

/*
        NOTES:
            [string] -> "string" is optional
            string1|string2 -> "string1" or "string2"
            ε -> void char

         RULES:
             Expression ::= Term+Expression
             Expression ::= Term-Expression
             Expression ::= Term
             Term ::= Factor*Term
             Term ::= Factor/Term
             Term ::= Factor
             Factor ::= Exponential
             Factor ::= ParExpression
             Factor ::= Logarithm
             Factor ::= Number
             Factor ::= Factorial
             ParExpression :: [-](Expression)
             Exponential ::= [-]Number^Exponential
             Exponential ::= [-]Number^Number
             Exponential ::= [-]Number^ParExpression
             Exponential ::= [-]ParExpression^Number
             Exponential ::= [-]ParExpression^ParExpression
             Exponential ::= [-]Logarithm^Number        TODO
             Exponential ::= [-]Logarithm^ParExpression TODO
             Exponential ::= [-]Logarithm^Logarithm     TODO
             Exponential ::= [-]Logarithm^Exponential   TODO
             Factorial ::= [-]ParExpression!
             Factorial ::= [-]Logarithm!
             Factorial ::= [-]Number!
             Factorial ::= [-]Factorial!
             Logarithm ::= [-]logParExpression
             Logarithm ::= [-]lnParExpression
             Number ::= [-]N|ⅇ|π
             N ::= NDigit.NDigit
             Digit ::= 1|2|3|4|5|6|7|8|9|0|ε

*/

public class ExpressionParser {

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = getExpression(expression).getComponent().getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    // Expression ::= Term+Expression
    // Expression ::= Term-Expression
    // Expression ::= Term
    private static ParsingResult<Expression> getExpression(String expression) {

        if (expression.isEmpty()) {
            return new ParsingResult<>(new Expression(new Term(new Number("0"))), 0);
        }

        ParsingResult<Term> parsedTerm = getTerm(expression);

        Term term = parsedTerm.getComponent();
        Integer parsedIndex = parsedTerm.getParsedIndex();

        // Expression ::= Term
        if (parsedIndex == expression.length() - 1) {
            return new ParsingResult<>(new Expression(term, ExpressionOperator.NONE, null), parsedIndex);
        }

        ExpressionOperator expressionOperator;

        // Expression ::= Term + Expression
        // Expression ::= Term - Expression
        char expressionOperatorChar = expression.charAt(parsedIndex + 1);
        switch (expressionOperatorChar) {
            case '+':
                expressionOperator = ExpressionOperator.SUM;
                break;
            case '-':
                expressionOperator = ExpressionOperator.SUBSTRACT;
                break;
            default:
                return new ParsingResult<>(new Expression(term, ExpressionOperator.NONE, null), parsedIndex);
        }

        parsedIndex++;

        ParsingResult<Expression> parsedSubExpression = getExpression(expression.substring(++parsedIndex));
        Expression subExpression = parsedSubExpression.getComponent();
        parsedIndex += parsedSubExpression.getParsedIndex();

        return new ParsingResult<>(new Expression(term, expressionOperator, subExpression), parsedIndex);

    }


    // Term ::= Factor*Term
    // Term ::= Factor/Term
    // Term ::= Factor
    private static ParsingResult<Term> getTerm(String expression) {

        ParsingResult<Factor> parsedFactor = getFactor(expression);
        Factor factor = parsedFactor.getComponent();
        Integer parsedIndex = parsedFactor.getParsedIndex();

        // Term ::= Factor
        if (parsedIndex == expression.length() - 1) {
            return new ParsingResult<>(new Term(factor, TermOperator.NONE, null), parsedIndex);
        }

        TermOperator termOperator;

        // Term ::= Factor * Term
        // Term ::= Factor / Term
        char termOperatorChar = expression.charAt(parsedIndex + 1);
        switch (termOperatorChar) {
            case '*':
                termOperator = TermOperator.MULTIPLY;
                break;
            case '/':
                termOperator = TermOperator.DIVIDE;
                break;
            default:
                return new ParsingResult<>(new Term(factor, TermOperator.NONE, null), parsedIndex);
        }

        parsedIndex++;

        ParsingResult<Term> parsedTerm = getTerm(expression.substring(++parsedIndex));
        Term subTerm = parsedTerm.getComponent();
        parsedIndex += parsedTerm.getParsedIndex();

        return new ParsingResult<>(new Term(factor, termOperator, subTerm), parsedIndex);
    }


    // Factor ::= Exponential
    // Factor ::= ParExpression
    // Factor ::= Logarithm
    // Factor ::= Number
    // Factor ::= Factorial
    private static ParsingResult<Factor> getFactor(String expression) {

        ParsingResult<? extends Factor> parsedFactor = null;

        List<Supplier<ParsingResult<? extends Factor>>> parsers = new LinkedList<>();
        parsers.add(() -> getExponential(expression));
        parsers.add(() -> getExpressionAsFactor(expression));
        parsers.add(() -> getLogarithm(expression));
        parsers.add(() -> getNumber(expression));

        for (Supplier<ParsingResult<? extends Factor>> parser : parsers) {
            parsedFactor = parser.get();
            if (parsedFactor != null) {
                break;
            }
        }

        if (parsedFactor == null) {
            throw new UnsupportedOperationException("Expression [" + expression + "] is not supported");
        }

        Factor factor = parsedFactor.getComponent();
        Integer parsedIndex = parsedFactor.getParsedIndex();

        if (!(factor instanceof Exponential) && parsedIndex < expression.length() - 1) {
            ParsingResult<Factorial> parsedFactorial = getFactorial(factor, expression.substring(parsedIndex + 1));
            if (parsedFactorial != null) {
                Factor factorial = parsedFactorial.getComponent();
                return new ParsingResult<>(factorial, parsedIndex + 1 + parsedFactorial.getParsedIndex());
            }
        }

        return new ParsingResult<>(factor, parsedIndex);

    }


    // Exponential ::= [-]Number^Exponential
    // Exponential ::= [-]Number^ParExpression
    // Exponential ::= [-]Number^Number
    // Exponential ::= [-]ParExpression^ParExpression
    // Exponential ::= [-]ParExpression^Number
    private static ParsingResult<Exponential> getExponential(String expression) {

        if (!expression.contains("^")) {
            return null;
        }

        Sign sign;
        if (expression.startsWith("-")) {
            sign = MINUS;
            expression = expression.substring(1);
        } else {
            sign = PLUS;
        }

        // Exponential ::= Number^Exponential
        Matcher recursiveExpMatcher = Pattern.compile(Constants.IS_RECURSIVE_EXPONENTIAL_REGEX).matcher(expression);
        if (recursiveExpMatcher.matches()) {
            Number base = new Number(recursiveExpMatcher.group(1));
            ParsingResult<Exponential> parsedExponent = getExponential(recursiveExpMatcher.group(3));
            if (parsedExponent != null) {
                Exponential exponential = new Exponential(sign, base, parsedExponent.getComponent());

                int baseChars = recursiveExpMatcher.end(1);
                int operatorChars = 1;
                int exponentChars = parsedExponent.getParsedIndex();
                int parsedIndex = baseChars + operatorChars + exponentChars;

                return new ParsingResult<>(exponential, parsedIndex);
            }
        }


        ExponentialBuilder exponentialBuilder = (exponentialSign, base, parsedIndex, toParse) -> {
            ParsingResult<? extends Factor> parsedExponent = null;
            if (parsedIndex < toParse.length() - 1 && toParse.charAt(++parsedIndex) == '^') {
                parsedIndex++;
                parsedExponent = getExpressionAsFactor(toParse.substring(parsedIndex));
                if (parsedExponent != null) {
                    parsedIndex += parsedExponent.getParsedIndex();
                    Factor exponent = parsedExponent.getComponent();
                    parsedExponent = new ParsingResult<>(exponent, parsedIndex);
                } else {
                    parsedExponent = getNumber(toParse.substring(parsedIndex));
                    if (parsedExponent != null) {
                        Factor exponent = parsedExponent.getComponent();
                        parsedIndex += parsedExponent.getParsedIndex();
                        parsedExponent = new ParsingResult<>(exponent, parsedIndex);
                    }
                }
                if (parsedExponent != null) {
                    Factor exponent = parsedExponent.getComponent();
                    Exponential localExponential = new Exponential(exponentialSign, base, exponent);
                    return new ParsingResult<>(localExponential, parsedExponent.getParsedIndex());
                }
            }
            return null;
        };


        Integer parsedIndex;
        ParsingResult<? extends Factor> parsedBase;

        // Exponential ::= Number^ParExpression
        // Exponential ::= Number^Number
        parsedBase = getNumber(expression);
        if (parsedBase != null) {
            Factor base = parsedBase.getComponent();
            parsedIndex = parsedBase.getParsedIndex();
            if (base != null) {
                ParsingResult<Exponential> parsedExponential = exponentialBuilder.build(sign, base, parsedIndex, expression);
                if (parsedExponential != null) {
                    return parsedExponential;
                }
            }
        }

        // Exponential ::= ParExpression^ParExpression
        // Exponential ::= ParExpression^Number
        parsedBase = getExpressionAsFactor(expression);
        if (parsedBase != null) {
            Factor base = parsedBase.getComponent();
            parsedIndex = parsedBase.getParsedIndex();
            if (base != null) {
                ParsingResult<Exponential> parsedExponential = exponentialBuilder.build(sign, base, parsedIndex, expression);
                if (parsedExponential != null) {
                    return parsedExponential;
                }
            }
        }

        return null;
    }


    // Logarithm ::= [-]log(Expression)
    // Logarithm ::= [-]ln(Expression)
    private static ParsingResult<Logarithm> getLogarithm(String expression) {

        int parsedIndex = -1;
        Sign sign = null;
        BigDecimal logBase = null;

        if (expression.startsWith("-log")) {
            parsedIndex += 4;
            sign = MINUS;
            logBase = BigDecimal.TEN;
        }
        if (expression.startsWith("log")) {
            parsedIndex += 3;
            sign = PLUS;
            logBase = BigDecimal.TEN;
        }
        if (expression.startsWith("-ln")) {
            parsedIndex += 3;
            sign = MINUS;
            logBase = NEP_NUMBER;
        }
        if (expression.startsWith("ln")) {
            parsedIndex += 2;
            sign = PLUS;
            logBase = NEP_NUMBER;
        }

        if (parsedIndex != -1) {
            ParsingResult<Factor> parsedArgument = getExpressionAsFactor(expression.substring(parsedIndex + 1));
            if (parsedArgument != null) {
                parsedIndex += parsedArgument.getParsedIndex() + 1;
                return new ParsingResult<>(new Logarithm(sign, logBase, parsedArgument.getComponent()), parsedIndex);
            } else {
                throw new IllegalArgumentException("Invalid expression [" + expression + "]");
            }
        }
        return null;
    }

    // ParExpression ::= [-](Expression)
    private static ParsingResult<Factor> getExpressionAsFactor(String toParse) {

        int parsedIndex = -1;

        Pattern isExpressionPattern = Pattern.compile(Constants.START_WITH_EXPRESSION_REGEX);
        Matcher isExpressionMatcher = isExpressionPattern.matcher(toParse);
        if (isExpressionMatcher.matches()) {
            int indexOfOpenParenthesis = toParse.indexOf('(');
            int indexOfClosedParenthesis = SyntaxUtils.getClosedParenthesisIndex(toParse, indexOfOpenParenthesis);
            String parenthesisContent = toParse.substring(indexOfOpenParenthesis + 1, indexOfClosedParenthesis);
            ParsingResult<Expression> absExpression = getExpression(parenthesisContent);
            parsedIndex += indexOfClosedParenthesis;
            Sign sign = toParse.startsWith("-") ? MINUS : PLUS;
            parsedIndex++;
            return new ParsingResult<>(new Expression(sign, absExpression.getComponent()), parsedIndex);
        }
        return null;
    }


    // Number ::= [-]N|ⅇ|π
    // N ::= NDigit.NDigit
    // Digit ::= 1|2|3|4|5|6|7|8|9|0|ε
    private static ParsingResult<Number> getNumber(String toParse) {

        Sign sign = toParse.startsWith("-") ? MINUS : PLUS;

        int parsedIndex = -1;

        if (sign.equals(MINUS)) {
            toParse = toParse.substring(1);
            parsedIndex++;
        }

        Pattern startWithNumberPattern = Pattern.compile(Constants.IS_GENERIC_NUM_REGEX);
        Matcher startWithNumberMatcher = startWithNumberPattern.matcher(toParse);
        if (startWithNumberMatcher.matches()) {
            parsedIndex += startWithNumberMatcher.end(1);
            return new ParsingResult<>(new Number(sign, new BigDecimal(startWithNumberMatcher.group(1))), parsedIndex);
        }

        switch (toParse.charAt(parsedIndex + 1)) {
            case 'ⅇ':
                return new ParsingResult<>(new Number(sign, NEP_NUMBER), parsedIndex + 1);
            case 'π':
                return new ParsingResult<>(new Number(sign, PI), parsedIndex + 1);
        }

        return null;
    }

    // Factorial ::= ParExpression!
    // Factorial ::= Logarithm!
    // Factorial ::= Number!
    // Factorial ::= Factorial!
    private static ParsingResult<Factorial> getFactorial(Factor factor, String toParse) {

        int currentIndex = 0;

        if (currentIndex < toParse.length() && toParse.charAt(currentIndex) == '!') {

            Sign sign = PLUS;
            if (factor.getSign().equals(MINUS)) {
                sign = MINUS;
                factor.setSign(PLUS);
            }

            ++currentIndex;
            Factorial factorial = new Factorial(factor);
            while (currentIndex < toParse.length() && toParse.charAt(currentIndex) == '!') {
                ++currentIndex;
                factorial = new Factorial(factorial);
            }
            factorial.setSign(sign);

            return new ParsingResult<>(factorial, currentIndex - 1);
        }

        return null;
    }

    @FunctionalInterface
    private interface ExponentialBuilder {
        ParsingResult<Exponential> build(Sign sign, Factor base, Integer parsedIndex, String expression);
    }
}

