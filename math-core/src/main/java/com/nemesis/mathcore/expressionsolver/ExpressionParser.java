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
import lombok.extern.slf4j.Slf4j;

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
             Factor             ::=  Sign { Exponential | Base }
             Sign               ::=  - | + | ε
             Exponential        ::=  Base ^ Factor
             Base               ::=  WrappedExpression | MathFunction | Constant | Variable | Factorial
             MathFunction       ::=  MathUnaryFunction | Logarithm | Root
             MathUnaryFunction  ::=  Trigonometric | InvTrigonometric | Hyperbolic | InvHyperbolic
             WrappedExpression  ::=  ParenthesizedExpr | AbsValueExpr
             ParenthesizedExpr  ::=  (Expression)
             AbsValueExpr       ::=  <pipe> Expression <pipe>
             Factorial          ::=  Factor!
             Root               ::=  RootSymbol Factor
             RootSymbol         ::=  √ | ∛ | ∜
             Trigonometric      ::=  { sin | cos | sec | tan | tg | cotan | cot | cotg | ctg | csc | cosec } WrappedExpression
             InvTrigonometric   ::=  arc Trigonometric
             Hyperbolic         ::=  Trigonometric h
             InvHyperbolic      ::=  ar Hyperbolic
             Logarithm          ::=  log WrappedExpression | ln WrappedExpression | log(Constant,Expression)
             Variable           ::=  {a-z}
             Constant           ::=  Number | ⅇ | π | ∞
             Number             ::=  IntegerNumber [.IntegerNumber]
             IntegerNumber      ::=  IntegerNumber Digit
             Digit              ::=  1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | ε

*/

@Slf4j
public class ExpressionParser {

    public static Expression parse(String input) {
        if (MathCoreContext.getNumericMode() == MathCoreContext.Mode.FRACTIONAL && input.contains(".")) {
            throw new IllegalArgumentException("Decimal numbers is not allowed in fractional mode");
        }
        ParsingResult<Expression> parsingResult = getExpression(input);
        if (parsingResult == null) {
            return null;
        }
        final Expression expression = parsingResult.getComponent();
        log.info("Parsed expression [{}] from string [{}]", expression, input);
        return expression;
    }


    /*
     Expression ::= Term+Expression
     Expression ::= Term-Expression
     Expression ::= Term
    */
    private static ParsingResult<Expression> getExpression(String input) {

        if (input.isEmpty()) {
            log.trace("Found void string, parsed as '0'");
            return new ParsingResult<>(new Expression(new Term(new Constant("0"))), -1);
        }

        ParsingResult<Term> parsedTerm = getTerm(input);

        if (parsedTerm == null) {
            log.trace("Unrecognized string [{}] as expression", input);
            return null;
        }

        Term term = parsedTerm.getComponent();
        Integer parsedChars = parsedTerm.getParsedChars();

        // Expression ::= Term
        if (!moreCharsToParse(parsedChars, input)) {
            log.debug("Recognized expression as term [{}] from string [{}]", term, input);
            return new ParsingResult<>(new Expression(term), parsedChars);
        }

        ExpressionOperator expressionOperator;

        // Expression ::= Term + Expression
        // Expression ::= Term - Expression
        char expressionOperatorChar = input.charAt(parsedChars);

        switch (expressionOperatorChar) {
            case '+' -> expressionOperator = ExpressionOperator.SUM;
            case '-' -> expressionOperator = ExpressionOperator.SUBTRACT;
            default -> {
                log.debug("Recognized expression as term [{}] from string [{}]", term, input);
                return new ParsingResult<>(new Expression(term), parsedChars);
            }
        }

        parsedChars++;

        ParsingResult<Expression> parsedSubExpression = getExpression(input.substring(parsedChars));
        if (parsedSubExpression == null) {
            log.trace("Unrecognized string [{}] as expression", input);
            return null;
        }
        Expression subExpression = parsedSubExpression.getComponent();
        parsedChars += parsedSubExpression.getParsedChars();

        final Expression parsedExpression = new Expression(term, expressionOperator, subExpression);
        log.debug("Recognized expression [{}] from string [{}]", parsedExpression, input);
        return new ParsingResult<>(parsedExpression, parsedChars);

    }


    /*
     Term ::= Factor*Term
     Term ::= Factor/Term
     Term ::= Factor Term
     Term ::= Factor
    */
    private static ParsingResult<Term> getTerm(String input) {

        ParsingResult<Factor> parsedFactor = getFactor(input);

        if (parsedFactor == null) {
            log.trace("Unrecognized string [{}] as term", input);
            return null;
        }

        Factor factor = parsedFactor.getComponent();
        Integer parsedChars = parsedFactor.getParsedChars();

        // Term ::= Factor
        if (!moreCharsToParse(parsedChars, input)) {
            log.debug("Recognized term as factor [{}] from string [{}]", factor, input);
            return new ParsingResult<>(new Term(factor), parsedChars);
        }

        ParsingResult<Term> parsedSubTerm = null;
        TermOperator termOperator;

        // Term ::= Factor * Term
        // Term ::= Factor / Term
        // Term ::= Factor Term
        char termOperatorChar = input.charAt(parsedChars);
        switch (termOperatorChar) {
            case '*' -> {
                termOperator = TermOperator.MULTIPLY;
                parsedChars++;
            }
            case '/' -> {
                termOperator = TermOperator.DIVIDE;
                parsedChars++;
            }
            case ',' -> {
                log.trace("Unrecognized string [{}] as term", input);
                return null;
            }
            default -> {
                if (termOperatorChar == '+' || termOperatorChar == '-') {
                    log.debug("Recognized term as factor [{}] from string [{}]", factor, input);
                    return new ParsingResult<>(new Term(factor), parsedChars);
                } else {
                    parsedSubTerm = getTerm(input.substring(parsedChars));
                    if (parsedSubTerm == null) {
                        log.debug("Recognized term as factor [{}] from string [{}]", factor, input);
                        return new ParsingResult<>(new Term(factor), parsedChars);
                    }
                }
                termOperator = TermOperator.MULTIPLY;
            }
        }

        if (parsedSubTerm == null) {
            parsedSubTerm = getTerm(input.substring(parsedChars));
        }

        if (parsedSubTerm == null) {
            throw new UnsupportedOperationException("Expression [" + input + "] is not supported");
        }

        Term subTerm = parsedSubTerm.getComponent();
        parsedChars += parsedSubTerm.getParsedChars();

        final Term parsedTerm = new Term(factor, termOperator, subTerm);
        log.debug("Recognized term [{}] from string [{}]", parsedTerm, input);
        return new ParsingResult<>(parsedTerm, parsedChars);
    }


    /*
        Factor ::=  Sign { Exponential | Parenthesized | MathFunction | Constant | Factorial }
    */
    private static ParsingResult<Factor> getFactor(String input) {

        Sign sign;
        int parsedChars = 0;
        String toParse;

        if (input.startsWith("-")) {
            sign = MINUS;
            toParse = input.substring(1);
            parsedChars++;
        } else {
            sign = PLUS;
            toParse = input;
        }

        ParsingResult<? extends Factor> parsedFactor = null;

        List<FactorParser> parsers = new LinkedList<>();
        parsers.add(ExpressionParser::getExponential);
        parsers.add(ExpressionParser::getBase);

        for (FactorParser parser : parsers) {
            parsedFactor = parser.parse(toParse);
            if (parsedFactor != null) {
                break;
            }
        }

        if (parsedFactor == null) {
            log.trace("Unrecognized string [{}] as factor", input);
            return null;
        }

        Factor factor = parsedFactor.getComponent();
        factor.setSign(sign);

        parsedChars += parsedFactor.getParsedChars();

        log.trace("Recognized factor [{}] from string [{}]", factor, input);
        return new ParsingResult<>(factor, parsedChars);

    }


    /*
      Exponential ::=  Base^Factor
     */

    private static ParsingResult<Exponential> getExponential(String input) {

        final String unrecognizedInputMessage = "Unrecognized string [{}] as exponential";

        if (!input.contains("^")) {
            log.trace(unrecognizedInputMessage, input);
            return null;
        }

        ParsingResult<? extends Base> parsedBase = getBase(input);

        if (parsedBase == null) {
            log.trace(unrecognizedInputMessage, input);
            return null;
        }

        Integer parsedChars = parsedBase.getParsedChars();

        if (moreCharsToParse(parsedChars, input) && input.charAt(parsedChars) == '^') {
            parsedChars++;
            ParsingResult<? extends Factor> parsedExponent = getFactor(input.substring(parsedChars));
            if (parsedExponent == null) {
                log.trace(unrecognizedInputMessage, input);
                return null;
            }
            Factor exponent = parsedExponent.getComponent();
            Exponential exponential = new Exponential(parsedBase.getComponent(), exponent);
            log.debug("Recognized exponential [{}] from string [{}]", exponential, input);
            return new ParsingResult<>(exponential, parsedChars + parsedExponent.getParsedChars());
        }

        log.trace(unrecognizedInputMessage, input);
        return null;
    }


    //  Base    ::=  Parenthesized | MathFunction | Constant | Variable | Factorial
    private static ParsingResult<? extends Base> getBase(String input) {

        ParsingResult<? extends Base> parsedBase = null;

        List<BaseParser> parsers = new LinkedList<>();
        parsers.add(ExpressionParser::getWrappedExpr);
        parsers.add(ExpressionParser::getMathFunction);
        parsers.add(ExpressionParser::getConstant);
        parsers.add(ExpressionParser::getVariable);

        for (BaseParser parser : parsers) {
            parsedBase = parser.parse(input);
            if (parsedBase != null) {
                break;
            }
        }

        if (parsedBase != null) {
            Integer parsedChars = parsedBase.getParsedChars();
            if (moreCharsToParse(parsedChars, input)) {
                ParsingResult<Factorial> parsedFactorial = getFactorial(parsedBase.getComponent(), input.substring(parsedChars));
                if (parsedFactorial != null) {
                    Base factorial = parsedFactorial.getComponent();
                    parsedChars += parsedFactorial.getParsedChars();
                    parsedBase = new ParsingResult<>(factorial, parsedChars);
                }
            }
        }

        if (parsedBase != null) {
            log.trace("Recognized base [{}] as [{}] from string [{}]", parsedBase.getComponent(), parsedBase.getComponent().getClass().getSimpleName(), input);
        } else {
            log.trace("Unrecognized string [{}] as base", input);
        }

        return parsedBase;
    }


    /*
        MathFunction  ::=  Root | Logarithm | Trigonometric | InvTrigonometric | Hyperbolic | InvHyperbolic
     */
    private static ParsingResult<? extends MathFunction> getMathFunction(String input) {

        ParsingResult<? extends MathFunction> parsedFunction;

        List<MathFunctionParser> parsers = new LinkedList<>();
        parsers.add(ExpressionParser::getRoot);
        parsers.add(ExpressionParser::getLogarithm);
        parsers.add(ExpressionParser::getTrigonometricFunction);

        for (MathFunctionParser parser : parsers) {
            parsedFunction = parser.parse(input);
            if (parsedFunction != null) {
                log.trace("Recognized math function [{}] as [{}] from string [{}]", parsedFunction.getComponent(), parsedFunction.getComponent().getClass().getSimpleName(), input);
                return parsedFunction;
            }
        }

        log.trace("Unrecognized string [{}] as math function", input);
        return null;
    }


    /*
        Logarithm  ::=  log WrappedExpression | ln WrappedExpression | log(Constant,Expression)
    */
    private static ParsingResult<Logarithm> getLogarithm(String input) {

        int parsedChars = 0;

        if (input.startsWith("log")) {
            parsedChars = 3;
        }
        if (input.startsWith("ln")) {
            parsedChars = 2;
        }

        if (parsedChars == 0) {
            log.trace("Unrecognized string [{}] as logarithm", input);
            return null;
        }

        String toParse = input.substring(parsedChars);

        ParsingResult<? extends WrappedExpression> parsedArgument = getWrappedExpr(toParse);
        if (parsedArgument != null) {
            parsedChars += parsedArgument.getParsedChars();
            final Logarithm logarithm = new Logarithm(NEP_NUMBER, parsedArgument.getComponent());
            log.debug("Recognized natural logarithm [{}] from string [{}]", logarithm, input);
            return new ParsingResult<>(logarithm, parsedChars);
        }

        Matcher isParenthesizedExprMatcher = Pattern.compile(START_WITH_PARENTHESIS_REGEX).matcher(toParse);

        if (isParenthesizedExprMatcher.matches()) {
            int indexOfClosedPar = SyntaxUtils.getClosedParenthesisIndex(toParse, 0);
            String content = toParse.substring(1, indexOfClosedPar);
            Matcher logArgumentMatcher = Pattern.compile(START_WITH_LOG_ARGUMENT_REGEX).matcher(content);
            if (logArgumentMatcher.matches()) {
                int baseGroup = 1, argumentGroup = 2;
                String base = logArgumentMatcher.group(baseGroup);
                String argument = logArgumentMatcher.group(argumentGroup);
                ParsingResult<Constant> constantParsingResult = getConstant(base);
                if (constantParsingResult != null) {
                    ParsingResult<Expression> expressionParsingResult = getExpression(argument);
                    if (expressionParsingResult != null) {
                        parsedChars += logArgumentMatcher.end(argumentGroup) + 2; // parsed function name, parenthesis and its content
                        BigDecimal logBase = constantParsingResult.getComponent().getValue();
                        final Logarithm logarithm = new Logarithm(logBase, new ParenthesizedExpression(expressionParsingResult.getComponent()));
                        log.debug("Recognized logarithm [{}] from string [{}]", logarithm, input);
                        return new ParsingResult<>(logarithm, parsedChars);
                    }
                } else {
                    throw new UnsupportedOperationException("Unexpected logarithm base [" + base + "]. Only constants are supported.");
                }
            }
        }

        throw new IllegalArgumentException("Invalid string [" + input + "]");
    }


    /*
         Trigonometric     ::=  { sin | cos | sec | tan | tg | cotan | cot | cotg | ctg | csc | cosec } Parenthesized
         InvTrigonometric  ::=  arc Trigonometric
         Hyperbolic        ::=  Trigonometric h
         InvHyperbolic     ::=  ar Hyperbolic
     */
    private static ParsingResult<MathUnaryFunction> getTrigonometricFunction(String input) {

        final String unrecognizedInputMessage = "Unrecognized string [{}] as trigonometric function";

        int parsedChars = 0;

        int argumentParIndex;
        if ((argumentParIndex = input.indexOf('(')) == -1) {
            log.trace(unrecognizedInputMessage, input);
            return null;
        }

        String functionName = input.substring(0, argumentParIndex);
        parsedChars += functionName.length();

        Method trigonometricMethod;
        try {
            trigonometricMethod = TrigonometricFunctions.class.getMethod(functionName, BigDecimal.class);
        } catch (NoSuchMethodException e) {
            log.trace(unrecognizedInputMessage, input);
            return null;
        }

        UnaryOperator<BigDecimal> unaryFunction = arg -> {
            try {
                return (BigDecimal) trigonometricMethod.invoke(arg);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ArithmeticException(e.getMessage());
            }
        };

        ParsingResult<? extends WrappedExpression> parsedArgument = getWrappedExpr(input.substring(parsedChars));
        if (parsedArgument != null) {
            parsedChars += parsedArgument.getParsedChars();
            final MathUnaryFunction trigonometricFunction = new MathUnaryFunction(unaryFunction, functionName, parsedArgument.getComponent());
            log.debug("Recognized trigonometric function [{}] from string [{}]", trigonometricFunction, input);
            return new ParsingResult<>(trigonometricFunction, parsedChars);
        }

        log.trace(unrecognizedInputMessage, input);
        return null;
    }


    /*
        Root       ::=  RootSymbol Factor
        RootSymbol ::=  √ | ∛ | ∜
    */
    private static ParsingResult<RootFunction> getRoot(String input) {

        final String unrecognizedInputMessage = "Unrecognized string [{}] as root function";
        final String parsedFunctionMessage = "Recognized root function [{}] from string [{}]";

        int parsedChars = 0;
        Integer rootIndex = null;

        switch (input.charAt(0)) {
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
            ParsingResult<Factor> parsedArgument = getFactor(input.substring(parsedChars));
            if (parsedArgument == null) {
                log.trace(unrecognizedInputMessage, input);
                return null;
            }
            Factor argument = parsedArgument.getComponent();
            parsedChars += parsedArgument.getParsedChars();
            final RootFunction rootFunction = new RootFunction(rootIndex, argument);
            log.debug(parsedFunctionMessage, rootFunction, input);
            return new ParsingResult<>(rootFunction, parsedChars);
        }

        // Root     ::=  root(Digit-th,Factor)
        Matcher rootFunctionMatcher = Pattern.compile(START_WITH_ROOT_FUNCTION_REGEX).matcher(input);
        if (rootFunctionMatcher.matches()) {
            parsedChars += (rootFunctionMatcher.end(2) + "-th,".length());
            rootIndex = Integer.parseInt(rootFunctionMatcher.group(2));
            ParsingResult<Factor> parsedArgument = getFactor(input.substring(parsedChars));
            if (parsedArgument == null) {
                log.trace(unrecognizedInputMessage, input);
                return null;
            }
            Factor argument = parsedArgument.getComponent();
            parsedChars += parsedArgument.getParsedChars();
            parsedChars++; // closed root parenthesis
            final RootFunction rootFunction = new RootFunction(rootIndex, argument);
            log.debug(parsedFunctionMessage, rootFunction, input);
            return new ParsingResult<>(rootFunction, parsedChars);
        }

        log.trace(unrecognizedInputMessage, input);
        return null;
    }


    /*
        Parenthesized ::= (Expression) | <pipe> Expression <pipe>
    */
    private static ParsingResult<? extends WrappedExpression> getWrappedExpr(String input) {

        final String unrecognizedInputMessage = "Unrecognized string [{}] as wrapped expression";

        int parsedChars = 0;

        // Parenthesized ::= (Expression)

        Matcher isParenthesizedExprMatcher = Pattern.compile(START_WITH_PARENTHESIS_REGEX).matcher(input);

        if (isParenthesizedExprMatcher.matches()) {
            int indexOfClosedPar = SyntaxUtils.getClosedParenthesisIndex(input, 0);
            String content = input.substring(1, indexOfClosedPar);
            ParsingResult<Expression> expression = getExpression(content);
            if (expression == null) {
                log.trace(unrecognizedInputMessage, input);
                return null;
            }
            parsedChars += expression.getParsedChars() + 2;
            final ParenthesizedExpression parExpression = new ParenthesizedExpression(expression.getComponent());
            log.debug("Recognized parenthesized expression [{}] from string [{}]", parExpression, input);
            return new ParsingResult<>(parExpression, parsedChars);
        }

        // Parenthesized ::= <pipe>Expression<pipe>

        if (input.charAt(0) == '|') {
            String toParse = input.substring(1);
            if (toParse.isEmpty()) {
                log.trace(unrecognizedInputMessage, input);
                return null;
            }
            ParsingResult<Expression> expression = getExpression(toParse);

            if (expression == null) {
                log.trace(unrecognizedInputMessage, input);
                return null;
            }

            Integer absContentParsedChars = expression.getParsedChars();
            if (absContentParsedChars >= toParse.length()) {
                log.trace(unrecognizedInputMessage, input);
                return null;
            }
            if (toParse.charAt(absContentParsedChars) != '|') {
                throw new IllegalArgumentException("Expected closing pipe char at index [" + absContentParsedChars + "]");
            }
            parsedChars += expression.getParsedChars() + 2;
            final AbsExpression absExpression = new AbsExpression(expression.getComponent());
            log.debug("Recognized absolute value expression [{}] from string [{}]", absExpression, input);
            return new ParsingResult<>(absExpression, parsedChars);
        }

        log.trace(unrecognizedInputMessage, input);
        return null;
    }


    /*
        Constant           ::=  Number | ⅇ | π | ∞
        Number             ::=  IntegerNumber [.IntegerNumber]
        IntegerNumber      ::=  IntegerNumber Digit
        Digit              ::=  1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | ε
    */
    private static ParsingResult<Constant> getConstant(String input) {

        int parsedChars = 0;

        Matcher startWithNumberMatcher = Pattern.compile(Constants.START_WITH_GENERIC_NUM_REGEX).matcher(input);
        if (startWithNumberMatcher.matches()) {
            parsedChars += startWithNumberMatcher.end(1);
            final Constant constant = new Constant(new BigDecimal(startWithNumberMatcher.group(1)));
            log.debug("Recognized constant [{}] from string [{}]", constant, input);
            return new ParsingResult<>(constant, parsedChars);
        }

        final ParsingResult<Constant> constantParsingResult = switch (input.charAt(parsedChars)) {
            case E_CHAR -> new ParsingResult<>(new Constant(NEP_NUMBER), ++parsedChars);
            case PI_CHAR -> new ParsingResult<>(new Constant(PI), ++parsedChars);
            case INFINITY -> new ParsingResult<>(new Infinity(), ++parsedChars);
            default -> null;
        };

        if (constantParsingResult != null) {
            log.debug("Recognized constant [{}] from string [{}]", constantParsingResult.getComponent(), input);
        } else {
            log.trace("Unrecognized string [{}] as constant", input);
        }

        return constantParsingResult;
    }


    /*
        Variable  ::=  [a-z]
    */
    private static ParsingResult<Variable> getVariable(String input) {

        final String unrecognizedInputMessage = "Unrecognized string [{}] as variable";
        int parsedChars = 0;
        char var = input.charAt(parsedChars);

        if (String.valueOf(var).matches("^[a-z]")) {
            if (moreCharsToParse(parsedChars + 1, input)) {
                char nextChar = input.charAt(parsedChars + 1);
                if (String.valueOf(nextChar).matches("^[a-z]")) {
                    log.trace(unrecognizedInputMessage, input);
                    return null; // 'var' is not a variable, but potentially a function name or something else
                }
            }
            log.debug("Recognized variable [{}] from string [{}]", var, input);
            return new ParsingResult<>(new Variable(var), ++parsedChars);
        }

        log.trace(unrecognizedInputMessage, input);
        return null;
    }


    /*
        Factorial  ::= { Parenthesized | MathFunction | Constant | Factorial } !
    */
    private static ParsingResult<Factorial> getFactorial(Factor factor, String toParse) {

        int parsedChars = 0;

        if (moreCharsToParse(parsedChars, toParse) && toParse.charAt(parsedChars) == '!') {

            ++parsedChars;
            Factorial factorial = new Factorial(factor);
            while (moreCharsToParse(parsedChars, toParse) && toParse.charAt(parsedChars) == '!') {
                ++parsedChars;
                factorial = new Factorial(factorial);
            }

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

