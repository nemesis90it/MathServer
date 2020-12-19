package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.operators.Sign;
import com.nemesis.mathcore.expressionsolver.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import com.nemesis.mathcore.expressionsolver.utils.ParsingResult;
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

import static com.nemesis.mathcore.expressionsolver.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.*;

/*
        NOTES:
             • [string] -> "string" is optional, applied to all its following options
             • { string1 | string2 } -> group of optional strings
             • { } -> group of strings or characters
             • string1|string2 -> "string1" or "string2"
             • ε -> void char
             • <pipe> -> |
             • spaces in rules definition are ignored

         DEFINITIONS:
             Expression         ::=  Term + Expression | Term - Expression | Term
             Term               ::=  Factor * Term | Factor / Term | Factor Term | Factor
             Factor             ::=  Exponential | Base
             Exponential        ::=  Base ^ Factor
             Base               ::=  WrappedExpression | MathFunction | Constant | Variable | Factorial
             MathFunction       ::=  MathUnaryFunction | Logarithm | Root
             MathUnaryFunction  ::=  Trigonometric | InvTrigonometric | Hyperbolic | InvHyperbolic
             WrappedExpression  ::=  [-] ParenthesizedExpr | AbsValueExpr
             ParenthesizedExpr  ::=  (Expression)
             AbsValueExpr       ::=  <pipe>Expression<pipe>
             Factorial          ::=  [-] Factor!
             Root               ::=  [-] RootSymbol Factor
             RootSymbol         ::=  √ | ∛ | ∜
             Trigonometric      ::=  [-] { sin | cos | sec | tan | tg | cotan | cot | cotg | ctg | csc | cosec } WrappedExpression
             InvTrigonometric   ::=  arc Trigonometric
             Hyperbolic         ::=  Trigonometric h
             InvHyperbolic      ::=  ar Hyperbolic
             Logarithm          ::=  [-] log WrappedExpression | ln WrappedExpression
             Variable           ::=  {a-z}
             Constant           ::=  [-] Number | ⅇ | π
             Number             ::=  Number Digit [.Number Digit]
             Digit              ::=  1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | ε

*/

public class ExpressionParser {

    public static Expression parse(String expression) {
        if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL && expression.contains(".")) {
            throw new IllegalArgumentException("Decimal numbers is not allowed in fractional mode");
        }
        ParsingResult<Expression> parsingResult = getExpression(expression);
        if (parsingResult == null) {
            return null;
        }
        return parsingResult.getComponent();
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

        if (parsedTerm == null) {
            return null;
        }

        Term term = parsedTerm.getComponent();
        Integer parsedChars = parsedTerm.getParsedChars();

        // Expression ::= Term
        if (!moreCharsToParse(parsedChars, expression)) {
            return new ParsingResult<>(new Expression(term), parsedChars);
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
                expressionOperator = ExpressionOperator.SUBTRACT;
                break;
            default:
                return new ParsingResult<>(new Expression(term), parsedChars);
        }

        parsedChars++;

        ParsingResult<Expression> parsedSubExpression = getExpression(expression.substring(parsedChars));
        if (parsedSubExpression == null) {
            return null;
        }
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

        if (parsedFactor == null) {
            return null;
        }

        Factor factor = parsedFactor.getComponent();
        Integer parsedChars = parsedFactor.getParsedChars();

        // Term ::= Factor
        if (!moreCharsToParse(parsedChars, expression)) {
            return new ParsingResult<>(new Term(factor), parsedChars);
        }

        TermOperator termOperator;

        // Term ::= Factor * Term
        // Term ::= Factor / Term
        // Term ::= Factor Term
        char termOperatorChar = expression.charAt(parsedChars);
        switch (termOperatorChar) {
            case '*' -> {
                termOperator = TermOperator.MULTIPLY;
                parsedChars++;
            }
            case '/' -> {
                termOperator = TermOperator.DIVIDE;
                parsedChars++;
            }
            default -> {
                if (termOperatorChar == '+' || termOperatorChar == '-') {
                    return new ParsingResult<>(new Term(factor), parsedChars);
                } else {
                    ParsingResult<Term> parsedTerm = getTerm(expression.substring(parsedChars));
                    if (parsedTerm == null) {
                        return new ParsingResult<>(new Term(factor), parsedChars);
                    }
                }
                termOperator = TermOperator.MULTIPLY;
            }
        }

        ParsingResult<Term> parsedTerm = getTerm(expression.substring(parsedChars));

        if (parsedTerm == null) {
            throw new UnsupportedOperationException("Expression [" + expression + "] is not supported");
        }

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
            return null;
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

        if (parsedBase == null) {
            return null;
        }

        parsedChars += parsedBase.getParsedChars();

        if (moreCharsToParse(parsedChars, expression) && expression.charAt(parsedChars) == '^') {
            parsedChars++;
            ParsingResult<? extends Factor> parsedExponent = getFactor(expression.substring(parsedChars));
            if (parsedExponent == null) {
                return null;
            }
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
        parsers.add(ExpressionParser::getWrappedExpr);
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
            ParsingResult<? extends WrappedExpression> parsedArgument = getWrappedExpr(expression.substring(parsedChars));
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

        ParsingResult<? extends WrappedExpression> parsedArgument = getWrappedExpr(toParse.substring(parsedChars));
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
            case SQUARE_ROOT_CHAR -> {
                parsedChars++;
                rootIndex = 2;
            }
            case CUBE_ROOT_CHAR -> {
                parsedChars++;
                rootIndex = 3;
            }
            case FOURTH_ROOT_CHAR -> {
                parsedChars++;
                rootIndex = 4;
            }
        }

        if (rootIndex != null) {
            ParsingResult<Factor> parsedArgument = getFactor(expression.substring(parsedChars));
            if (parsedArgument == null) {
                return null;
            }
            Factor argument = parsedArgument.getComponent();
            parsedChars += parsedArgument.getParsedChars();
            return new ParsingResult<>(new RootFunction(sign, rootIndex, argument), parsedChars);
        }

        // Root     ::=  [-] root(Digit-th,Factor)
        Matcher rootFunctionMatcher = Pattern.compile(ROOT_FUNCTION_REGEX).matcher(toParse);
        if (rootFunctionMatcher.matches()) {
            parsedChars += (rootFunctionMatcher.end(2) + "-th,".length());
            rootIndex = Integer.parseInt(rootFunctionMatcher.group(2));
            ParsingResult<Factor> parsedArgument = getFactor(expression.substring(parsedChars));
            if (parsedArgument == null) {
                return null;
            }
            Factor argument = parsedArgument.getComponent();
            parsedChars += parsedArgument.getParsedChars();
            parsedChars++; // closed root parenthesis
            return new ParsingResult<>(new RootFunction(sign, rootIndex, argument), parsedChars);
        }


        return null;
    }
    /*
        Parenthesized ::= [-](Expression) | <pipe>Expression<pipe>
    */

    private static ParsingResult<? extends WrappedExpression> getWrappedExpr(String expression) {

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

        // Parenthesized ::= [-](Expression)

        Matcher isParenthesizedExprMatcher = Pattern.compile(START_WITH_PARENTHESIS_REGEX).matcher(toParse);

        if (isParenthesizedExprMatcher.matches()) {
            int indexOfClosedPar = SyntaxUtils.getClosedParenthesisIndex(toParse, 0);
            String content = toParse.substring(1, indexOfClosedPar);
            ParsingResult<Expression> absExpression = getExpression(content);
            if (absExpression == null) {
                return null;
            }
            parsedChars += absExpression.getParsedChars() + 2;
            return new ParsingResult<>(new ParenthesizedExpression(sign, absExpression.getComponent()), parsedChars);
        }

        // Parenthesized ::= [-]<pipe>Expression<pipe>

        if (toParse.charAt(0) == '|') {
            toParse = toParse.substring(1);
            if (toParse.isEmpty()) {
                return null;
            }
            ParsingResult<Expression> absExpression = getExpression(toParse);

            if (absExpression == null) {
                return null;
            }

            Integer absContentParsedChars = absExpression.getParsedChars();
            if (absContentParsedChars >= toParse.length()) {
                return null;
            }
            if (toParse.charAt(absContentParsedChars) != '|') {
                throw new IllegalArgumentException("Expected closing pipe char at index [" + absContentParsedChars + "]");
            }
            parsedChars += absExpression.getParsedChars() + 2;
            return new ParsingResult<>(new AbsExpression(sign, absExpression.getComponent()), parsedChars);
        }

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

        Matcher startWithNumberMatcher = Pattern.compile(Constants.START_WITH_GENERIC_NUM_REGEX).matcher(toParse);
        if (startWithNumberMatcher.matches()) {
            parsedChars += startWithNumberMatcher.end(1);
            return new ParsingResult<>(new Constant(sign, new BigDecimal(startWithNumberMatcher.group(1))), parsedChars);
        }

        return switch (expression.charAt(parsedChars)) {
            case E_CHAR -> new ParsingResult<>(new Constant(sign, NEP_NUMBER), ++parsedChars);
            case PI_CHAR -> new ParsingResult<>(new Constant(sign, PI), ++parsedChars);
            default -> null;
        };

    }
    /*
        Variable  ::=  [a-z]
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

        char var = expression.charAt(parsedChars);
        if (String.valueOf(var).matches("^[a-z]")) {
            if (moreCharsToParse(parsedChars + 1, expression)) {
                char nextChar = expression.charAt(parsedChars + 1);
                if (String.valueOf(nextChar).matches("^[a-z]")) {
                    return null; // 'var' is not a variable, but potentially a function name or something else
                }
            }
            return new ParsingResult<>(new Variable(sign, var), ++parsedChars);
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

