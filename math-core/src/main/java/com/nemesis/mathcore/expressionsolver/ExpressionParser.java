package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.models.ParsingResult;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;
import com.nemesis.mathcore.utils.TrigonometricFunctions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.*;

/*
        NOTES:
             • [string] -> "string" is optional, applied to all its following options
             • { string1 | string2 } -> group of optional strings
             • string1|string2 -> "string1" or "string2"
             • ε -> void char
             • <pipe> -> |
             • spaces in rules definition are ignored

         RULES:
             Expression         ::=  Term + Expression | Term - Expression | Term
             Term               ::=  Factor * Term | Factor / Term | Factor
             Factor             ::=  Exponential | Parenthesized | MathFunction | Constant | Variable | Factorial
             Exponential        ::=  Base ^ Factor
             Base               ::=  Parenthesized | MathFunction | Constant | Variable | Factorial
             MathFunction       ::=  MathUnaryFunction | Logarithm | Root
             MathUnaryFunction  ::=  Trigonometric | InvTrigonometric | Hyperbolic | InvHyperbolic
             Parenthesized      ::=  [-] (Expression) | <pipe>Expression<pipe>
             Factorial          ::=  [-] { Parenthesized | MathFunction | Constant | Variable | Factorial } !
             Root               ::=  [-] RootSymbol Factor
             RootSymbol         ::=  √ | ∛ | ∜
             Trigonometric      ::=  [-] { sin | cos | sec | tan | tg | cotan | cot | cotg | ctg | csc | cosec } Parenthesized
             InvTrigonometric   ::=  arc Trigonometric
             Hyperbolic         ::=  Trigonometric h
             InvHyperbolic      ::=  ar Hyperbolic
             Logarithm          ::=  [-] log Parenthesized | ln Parenthesized
             Variable           ::=  x
             Constant           ::=  [-] Number | ⅇ | π
             Number             ::=  NumberDigit[.NumberDigit]
             Digit              ::=  1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | ε

*/

public class ExpressionParser {

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = getExpression(expression).getComponent().getValue();
        return SyntaxUtils.removeNonSignificantZeros(rawResult);
    }

    public static String getDerivative(String expression) {
        String derivative = getExpression(expression).getComponent().getDerivative().toString();
        return derivative;
//        throw new UnsupportedOperationException();
    }

    /*
     Expression ::= Term+Expression
     Expression ::= Term-Expression
     Expression ::= Term
    */
    private static ParsingResult<Expression> getExpression(String expression) {

        if (expression.isEmpty()) {
            return new ParsingResult<>(new Expression(new Term(new Constant("0"))), -1);
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
        Factor ::=  Exponential | Parenthesized | MathFunction | Constant | Factorial
     */
    private static ParsingResult<Factor> getFactor(String expression) {

        ParsingResult<? extends Factor> parsedFactor = null;

        List<FactorParser> parsers = new LinkedList<>();
        parsers.add(ExpressionParser::getExponential);
        parsers.add(ExpressionParser::getBase);

        for (FactorParser parser : parsers) {
            parsedFactor = parser.parse(expression);
            if (parsedFactor != null) {
                break;
            }
        }

        if (parsedFactor == null) {
            throw new UnsupportedOperationException("Expression [" + expression + "] is not supported");
        }

        Factor factor = parsedFactor.getComponent();
        Integer parsedChars = parsedFactor.getParsedChars();

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

        ParsingResult<? extends Base> parsedBase = getBase(toParse);
        parsedChars += parsedBase.getParsedChars();

        if (moreCharsToParse(parsedChars, expression) && expression.charAt(parsedChars) == '^') {
            parsedChars++;
            ParsingResult<? extends Factor> parsedExponent = getFactor(expression.substring(parsedChars));
            Factor exponent = parsedExponent.getComponent();
            Exponential exponential = new Exponential(sign, parsedBase.getComponent(), exponent);
            return new ParsingResult<>(exponential, parsedChars + parsedExponent.getParsedChars());
        }

        return null;
    }


    //  Base    ::=  Parenthesized | MathFunction | Constant | Variable | Factorial

    private static ParsingResult<? extends Base> getBase(String toParse) {

        ParsingResult<? extends Base> parsedBase = null;

        List<BaseParser> parsers = new LinkedList<>();
        parsers.add(ExpressionParser::getParenthesizedExpr);
        parsers.add(ExpressionParser::getMathFunction);
        parsers.add(ExpressionParser::getConstant);
        parsers.add(ExpressionParser::getVariable);

        for (BaseParser parser : parsers) {
            parsedBase = parser.parse(toParse);
            if (parsedBase != null) {
                break;
            }
        }

        if (parsedBase != null) {
            Integer parsedChars = parsedBase.getParsedChars();
            if (moreCharsToParse(parsedChars, toParse)) {
                ParsingResult<Factorial> parsedFactorial = getFactorial(parsedBase.getComponent(), toParse.substring(parsedChars));
                if (parsedFactorial != null) {
                    Base factorial = parsedFactorial.getComponent();
                    parsedChars += parsedFactorial.getParsedChars();
                    parsedBase = new ParsingResult<>(factorial, parsedChars);
                }
            }
        }

        return parsedBase;
    }

    /*
        MathFunction  ::=  Root | Logarithm | Trigonometric | InvTrigonometric | Hyperbolic | InvHyperbolic
     */
    private static ParsingResult<? extends MathFunction> getMathFunction(String expression) {

        ParsingResult<? extends MathFunction> parsedFunction;

        List<MathFunctionParser> parsers = new LinkedList<>();
        parsers.add(ExpressionParser::getRoot);
        parsers.add(ExpressionParser::getLogarithm);
        parsers.add(ExpressionParser::getTrigonometricFunction);

        for (MathFunctionParser parser : parsers) {
            parsedFunction = parser.parse(expression);
            if (parsedFunction != null) {
                return parsedFunction;
            }
        }

        return null;
    }

    /*
        Logarithm ::= [-]log(Expression)
        Logarithm ::= [-]ln(Expression)
    */
    private static ParsingResult<Logarithm> getLogarithm(String expression) {

        // TODO: log base x

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
            ParsingResult<ParenthesizedExpression> parsedArgument = getParenthesizedExpr(expression.substring(parsedChars));
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
         Trigonometric     ::=  [-] { sin | cos | sec | tan | tg | cotan | cot | cotg | ctg | csc | cosec } Parenthesized
         InvTrigonometric  ::=  arc Trigonometric
         Hyperbolic        ::=  Trigonometric h
         InvHyperbolic     ::=  ar Hyperbolic
     */
    private static ParsingResult<MathUnaryFunction> getTrigonometricFunction(String expression) {

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

        int argumentParIndex;
        if ((argumentParIndex = toParse.indexOf('(')) == -1) {
            return null;
        }

        String functionName = toParse.substring(0, argumentParIndex);
        parsedChars += functionName.length();

        Method m;
        try {
            m = TrigonometricFunctions.class.getMethod(functionName, BigDecimal.class);
        } catch (NoSuchMethodException e) {
            return null;
        }

        UnaryOperator<BigDecimal> unaryFunction = arg -> {
            try {
                return (BigDecimal) m.invoke(arg);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ArithmeticException(e.getMessage());
            }
        };

        ParsingResult<ParenthesizedExpression> parsedArgument = getParenthesizedExpr(toParse.substring(parsedChars));
        if (parsedArgument != null) {
            parsedChars += parsedArgument.getParsedChars();
            return new ParsingResult<>(new MathUnaryFunction(sign, unaryFunction, functionName, parsedArgument.getComponent()), parsedChars);
        }

        return null;
    }

    /*
        Root       ::=  [-] RootSymbol Factor
        RootSymbol ::=  √ | ∛ | ∜
    */
    private static ParsingResult<RootFunction> getRoot(String expression) {

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

        Integer rootIndex = null;

        switch (toParse.charAt(0)) {
            case SQUARE_ROOT_CHAR:
                parsedChars++;
                rootIndex = 2;
                break;
            case CUBE_ROOT_CHAR:
                parsedChars++;
                rootIndex = 3;
                break;
            case FOURTH_ROOT_CHAR:
                parsedChars++;
                rootIndex = 4;
                break;
        }

        if (rootIndex != null) {
            ParsingResult<Factor> parsedArgument = getFactor(expression.substring(parsedChars));
            Factor argument = parsedArgument.getComponent();
            parsedChars += parsedArgument.getParsedChars();
            return new ParsingResult<>(new RootFunction(sign, rootIndex, argument), parsedChars);
        }

        return null;
    }

    /*
        Parenthesized ::= [-](Expression) | <pipe>Expression<pipe>
    */
    private static ParsingResult<ParenthesizedExpression> getParenthesizedExpr(String expression) {

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

        Matcher isParenthesizedExprMatcher = Pattern.compile(Constants.START_WITH_PARENTHESIS_REGEX).matcher(toParse);

        if (isParenthesizedExprMatcher.matches()) {
            int indexOfClosedPar = SyntaxUtils.getClosedParenthesisIndex(toParse, 0);
            String content = toParse.substring(1, indexOfClosedPar);
            ParsingResult<Expression> absExpression = getExpression(content);
            parsedChars += absExpression.getParsedChars() + 2;
            return new ParsingResult<>(new ParenthesizedExpression(sign, absExpression.getComponent()), parsedChars);
        }


//        if (toParse.charAt(0) == '|') {
//            toParse = toParse.substring(1);
//            int indexOfClosedPipe = SyntaxUtils.getClosedPipeIndex(toParse, 0);
//            if (indexOfClosedPipe == -1) {
//                throw new IllegalArgumentException("Pipe must be pairs");
//            }
//
//            String content = toParse.substring(0, indexOfClosedPipe);
//            ParsingResult<Expression> absExpression = getExpression(content);
//            parsedChars += absExpression.getParsedChars() + 2;
//
//            Expression subAbsExpression = absExpression.getComponent();
//            subAbsExpression.setSign(PLUS);
//
//            return new ParsingResult<>(new ParenthesizedExpression(sign, subAbsExpression), parsedChars);
//        }

        return null;
    }

    /*
        Constant  ::=  [-] Number | ⅇ | π
        Number    ::=  Number Digit [.Number Digit]
        Digit     ::=  1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | ε
    */
    private static ParsingResult<Constant> getConstant(String expression) {

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
            return new ParsingResult<>(new Constant(sign, new BigDecimal(startWithNumberMatcher.group(1))), parsedChars);
        }

        switch (expression.charAt(parsedChars)) {
            case E_CHAR:
                return new ParsingResult<>(new Constant(sign, NEP_NUMBER), ++parsedChars);
            case PI_CHAR:
                return new ParsingResult<>(new Constant(sign, PI), ++parsedChars);
        }

        return null;
    }

    /*
        Variable  ::=  x
    */
    private static ParsingResult<Variable> getVariable(String expression) {

        int parsedChars = 0;

        Sign sign;
        if (expression.startsWith("-")) {
            sign = MINUS;
            parsedChars++;
        } else {
            sign = PLUS;
        }

        if (expression.charAt(parsedChars) == 'x') {
            return new ParsingResult<>(new Variable(sign), ++parsedChars);
        }

        return null;
    }

    /*
        Factorial  ::=  [-] { Parenthesized | MathFunction | Constant | Factorial } !
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

    private interface FactorParser extends Function<String, ParsingResult<? extends Factor>> {
        default ParsingResult<? extends Factor> parse(String expression) {
            return apply(expression);
        }
    }

    private interface BaseParser extends Function<String, ParsingResult<? extends Base>> {
        default ParsingResult<? extends Base> parse(String expression) {
            return apply(expression);
        }
    }

    private interface MathFunctionParser extends Function<String, ParsingResult<? extends MathFunction>> {
        default ParsingResult<? extends MathFunction> parse(String expression) {
            return apply(expression);
        }
    }
}

