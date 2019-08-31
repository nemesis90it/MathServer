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
             • [string] -> "string" is optional, applied to all its following options
             • string1|string2 -> "string1" or "string2"
             • ε -> void char
             • spaces in rules definition are ignored

         RULES:
             Expression     ::=  Term+Expression | Term-Expression | Term
             Term           ::=  Factor*Term | Factor/Term | Factor
             Factor         ::=  Exponential | Parenthesized | Logarithm | Number | Factorial
             Exponential    ::=  Base^Factor
             Base           ::=  Parenthesized | Logarithm | Number
             Parenthesized  ::=  [-] (Expression)
             Factorial      ::=  [-] Parenthesized! | Logarithm! | Number! | Factorial!
             Logarithm      ::=  [-] logParenthesized | lnParenthesized
             Value          ::=  [-] Number | ⅇ | π
             Number         ::=  NumberDigit[.NumberDigit]
             Digit          ::=  1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | ε

*/

public class ExpressionParser {

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = getExpression(expression).getComponent().getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    /*
     Expression ::= Term+Expression
     Expression ::= Term-Expression
     Expression ::= Term
    */
    private static ParsingResult<Expression> getExpression(String expression) {

        if (expression.isEmpty()) {
            return new ParsingResult<>(new Expression(new Term(new Number("0"))), -1);
        }

        ParsingResult<Term> parsedTerm = getTerm(expression);

        Term term = parsedTerm.getComponent();
        Integer parsedChars = parsedTerm.getParsedChars();

        // Expression ::= Term
        if (!moreCharsToParse(parsedChars, expression)) {
            return new ParsingResult<>(new Expression(term, ExpressionOperator.NONE, null), parsedChars);
        }

        ExpressionOperator expressionOperator;

        // Expression ::= Term + Expression
        // Expression ::= Term - Expression
        char expressionOperatorChar = expression.charAt(parsedChars);
        switch (expressionOperatorChar) {
            case '+':
                expressionOperator = ExpressionOperator.SUM;
                break;
            case '-':
                expressionOperator = ExpressionOperator.SUBSTRACT;
                break;
            default:
                return new ParsingResult<>(new Expression(term, ExpressionOperator.NONE, null), parsedChars);
        }

        parsedChars++;

        ParsingResult<Expression> parsedSubExpression = getExpression(expression.substring(parsedChars));
        Expression subExpression = parsedSubExpression.getComponent();
        parsedChars += parsedSubExpression.getParsedChars();

        return new ParsingResult<>(new Expression(term, expressionOperator, subExpression), parsedChars);

    }


    /*
     Term ::= Factor*Term
     Term ::= Factor/Term
     Term ::= Factor
    */
    private static ParsingResult<Term> getTerm(String expression) {

        ParsingResult<Factor> parsedFactor = getFactor(expression);
        Factor factor = parsedFactor.getComponent();
        Integer parsedChars = parsedFactor.getParsedChars();

        // Term ::= Factor
        if (!moreCharsToParse(parsedChars, expression)) {
            return new ParsingResult<>(new Term(factor, TermOperator.NONE, null), parsedChars);
        }

        TermOperator termOperator;

        // Term ::= Factor * Term
        // Term ::= Factor / Term
        char termOperatorChar = expression.charAt(parsedChars);
        switch (termOperatorChar) {
            case '*':
                termOperator = TermOperator.MULTIPLY;
                break;
            case '/':
                termOperator = TermOperator.DIVIDE;
                break;
            default:
                return new ParsingResult<>(new Term(factor, TermOperator.NONE, null), parsedChars);
        }

        parsedChars++;

        ParsingResult<Term> parsedTerm = getTerm(expression.substring(parsedChars));
        Term subTerm = parsedTerm.getComponent();
        parsedChars += parsedTerm.getParsedChars();

        return new ParsingResult<>(new Term(factor, termOperator, subTerm), parsedChars);
    }

    /*
        Factor ::= Exponential
        Factor ::= Parenthesized
        Factor ::= Logarithm
        Factor ::= Number
        Factor ::= Factorial
     */
    private static ParsingResult<Factor> getFactor(String expression) {

        ParsingResult<? extends Factor> parsedFactor = null;

        List<Supplier<ParsingResult<? extends Factor>>> parsers = new LinkedList<>();
        parsers.add(() -> getExponential(expression));
        parsers.add(() -> getParenthesizedExpr(expression));
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
        Integer parsedChars = parsedFactor.getParsedChars();

        if (!(factor instanceof Exponential) && moreCharsToParse(parsedChars, expression)) {
            ParsingResult<Factorial> parsedFactorial = getFactorial(factor, expression.substring(parsedChars));
            if (parsedFactorial != null) {
                Factor factorial = parsedFactorial.getComponent();
                return new ParsingResult<>(factorial, parsedChars + parsedFactorial.getParsedChars());
            }
        }

        return new ParsingResult<>(factor, parsedChars);

    }

    /*
      Exponential ::=  Base^Factor
      Base        ::=  Parenthesized | Logarithm | Number
     */
    private static ParsingResult<Exponential> getExponential(String expression) {

        if (!expression.contains("^")) {
            return null;
        }

        Sign sign;
        int parsedChars = 0;
        String toParse;

        if (expression.startsWith("-")) {
            sign = MINUS;
            toParse = expression.substring(1);
            parsedChars++;
        } else {
            sign = PLUS;
            toParse = expression;
        }

        ParsingResult<? extends Factor> parsedBase = null;
        List<Supplier<ParsingResult<? extends Factor>>> parsers = new LinkedList<>();

        parsers.add(() -> getParenthesizedExpr(toParse));
        parsers.add(() -> getLogarithm(toParse));
        parsers.add(() -> getNumber(toParse));

        for (Supplier<ParsingResult<? extends Factor>> parser : parsers) {
            parsedBase = parser.get();
            if (parsedBase != null) {
                break;
            }
        }

        if (parsedBase == null) {
            return null;
        }

        parsedChars += parsedBase.getParsedChars();
        if (moreCharsToParse(parsedChars, expression)) {
            ParsingResult<Factorial> parsedFactorial = getFactorial(parsedBase.getComponent(), expression.substring(parsedChars));
            if (parsedFactorial != null) {
                Factor factorial = parsedFactorial.getComponent();
                parsedChars += parsedFactorial.getParsedChars();
                parsedBase = new ParsingResult<>(factorial, parsedChars);
            }
        }

        if (moreCharsToParse(parsedChars, expression) && expression.charAt(parsedChars) == '^') {
            parsedChars++;
            ParsingResult<? extends Factor> parsedExponent = getFactor(expression.substring(parsedChars));
            Factor exponent = parsedExponent.getComponent();
            Exponential exponential = new Exponential(sign, parsedBase.getComponent(), exponent);
            return new ParsingResult<>(exponential, parsedChars + parsedExponent.getParsedChars());
        }

        return null;
    }

    /*
     Logarithm ::= [-]log(Expression)
     Logarithm ::= [-]ln(Expression)
    */
    private static ParsingResult<Logarithm> getLogarithm(String expression) {


        Sign sign;
        int parsedChars = 0;
        String toParse;
        BigDecimal logBase = null;

        if (expression.startsWith("-")) {
            sign = MINUS;
            toParse = expression.substring(1);
            parsedChars++;
        } else {
            sign = PLUS;
            toParse = expression;
        }

        if (toParse.startsWith("log")) {
            parsedChars += 3;
            logBase = BigDecimal.TEN;
        }
        if (toParse.startsWith("ln")) {
            parsedChars += 2;
            logBase = NEP_NUMBER;
        }

        if (parsedChars > 1) {
            ParsingResult<Factor> parsedArgument = getParenthesizedExpr(expression.substring(parsedChars));
            if (parsedArgument != null) {
                parsedChars += parsedArgument.getParsedChars();
                return new ParsingResult<>(new Logarithm(sign, logBase, parsedArgument.getComponent()), parsedChars);
            } else {
                throw new IllegalArgumentException("Invalid expression [" + toParse + "]");
            }
        }
        return null;
    }


    /*
     Parenthesized ::= [-](Expression)
    */
    private static ParsingResult<Factor> getParenthesizedExpr(String expression) {

        int parsedChars = 0;

        Sign sign;
        String toParse;
        if (expression.startsWith("-")) {
            sign = MINUS;
            toParse = expression.substring(1);
            parsedChars++;
        } else {
            sign = PLUS;
            toParse = expression;
        }

        Pattern isExpressionPattern = Pattern.compile(Constants.START_WITH_EXPRESSION_REGEX);
        Matcher isExpressionMatcher = isExpressionPattern.matcher(toParse);

        if (isExpressionMatcher.matches()) {
            int indexOfClosedPar = SyntaxUtils.getClosedParenthesisIndex(toParse, 0);
            String content = toParse.substring(1, indexOfClosedPar);
            ParsingResult<Expression> absExpression = getExpression(content);
            parsedChars += absExpression.getParsedChars() + 2;
            return new ParsingResult<>(new Expression(sign, absExpression.getComponent()), parsedChars);
        }
        return null;
    }


    /*
     Number ::= [-]N|ⅇ|π
     N ::= NDigit.NDigit
     Digit ::= 1|2|3|4|5|6|7|8|9|0|ε
    */
    private static ParsingResult<Number> getNumber(String expression) {

        int parsedChars = 0;

        Sign sign;
        String toParse;
        if (expression.startsWith("-")) {
            sign = MINUS;
            toParse = expression.substring(1);
            parsedChars++;
        } else {
            sign = PLUS;
            toParse = expression;
        }

        Matcher startWithNumberMatcher = Pattern.compile(Constants.IS_GENERIC_NUM_REGEX).matcher(toParse);
        if (startWithNumberMatcher.matches()) {
            parsedChars += startWithNumberMatcher.end(1);
            return new ParsingResult<>(new Number(sign, new BigDecimal(startWithNumberMatcher.group(1))), parsedChars);
        }

        switch (expression.charAt(parsedChars)) {
            case 'ⅇ':
                return new ParsingResult<>(new Number(sign, NEP_NUMBER), ++parsedChars);
            case 'π':
                return new ParsingResult<>(new Number(sign, PI), ++parsedChars);
        }

        return null;
    }

    /*
     Factorial ::= Parenthesized!
     Factorial ::= Logarithm!
     Factorial ::= Number!
     Factorial ::= Factorial!
    */
    private static ParsingResult<Factorial> getFactorial(Factor factor, String toParse) {

        int parsedChars = 0;

        if (moreCharsToParse(parsedChars, toParse) && toParse.charAt(parsedChars) == '!') {

            Sign sign = PLUS;
            if (factor.getSign().equals(MINUS)) {
                sign = MINUS;
                factor.setSign(PLUS);
            }

            ++parsedChars;
            Factorial factorial = new Factorial(factor);
            while (moreCharsToParse(parsedChars, toParse) && toParse.charAt(parsedChars) == '!') {
                ++parsedChars;
                factorial = new Factorial(factorial);
            }
            factorial.setSign(sign);

            return new ParsingResult<>(factorial, parsedChars);
        }

        return null;
    }

    private static boolean moreCharsToParse(Integer parsedChars, String expression) {
        return parsedChars < expression.length();
    }
}

