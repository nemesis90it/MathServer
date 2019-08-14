package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.models.Number;
import com.nemesis.mathcore.expressionsolver.models.*;
import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.SyntaxUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
         Expression ::= Term + Expression
         Expression ::= Term - Expression
         Expression ::= Term
         Term ::= Factor * Term
         Term ::= Factor / Term
         Term ::= Factor
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= Factorial
         Factor ::= (Expression)
         Exponential ::= Number^Number
         Exponential ::= Number^Exponential
         Exponential ::= Number^(Expression)
         Exponential ::= (Expression)^Number
         Exponential ::= (Expression)^(Expression)
         Factorial ::= Number!
         Factorial ::= (Expression)!

*/

public class ExpressionSolver {


    private String expression;
    private int currentIndex = 0;

    public static BigDecimal evaluate(String expression) {
        BigDecimal rawResult = new ExpressionSolver(expression).getExpression().getValue();
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

    private ExpressionSolver(String expression) {
        SyntaxUtils.checkParenthesis(expression);
        this.expression = expression;
    }

    private Expression getExpression() {

        if (expression.isEmpty()) {
            return new Expression(new Term(new Number("0")));
        }

        Term term;
        ExpressionOperator expressionOperator;
        Expression subExpression;

        term = this.getTerm();

        // Expression = Term
        if (currentIndex == expression.length()) {
            return new Expression(term, ExpressionOperator.NONE, null);
        }

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
        subExpression = this.getExpression();

        return new Expression(term, expressionOperator, subExpression);

    }

    private Term getTerm() {

        Factor factor;
        TermOperator termOperator;
        Term subTerm;

        factor = this.getFactor();

        // Term = Factor
        if (currentIndex == expression.length()) {
            return new Term(factor, TermOperator.NONE, null);
        }

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
        subTerm = this.getTerm();

        return new Term(factor, termOperator, subTerm);
    }


    private Factor getFactor() {

        /*
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= -Exponential
         Factor ::= Number!
         Factor ::= -Number!
         Factor ::= (Expression)
         Factor ::= -(Expression)
         Factor ::= (Expression)!
         Factor ::= -(Expression)!
         Factor ::= Exponential
        */


        String toParse = expression.substring(currentIndex);


        /* TODO: standardize
            factor = this.getExponential();
            // Factor ::= Exponential
            if (currentIndex == expression.length()) {
                return factor;
            }

         */

        // Factor ::= Exponential
        Factor factor = this.tryToGetExponential(toParse);
        if (factor != null) {
            return factor;
        }

        // Factor = (Expression)
        // Factor = -(Expression)
        // Factor = (Expression)!
        // Factor = -(Expression)!
        Pattern isExpressionPattern = Pattern.compile(Constants.START_WITH_EXPRESSION_REGEX);
        Matcher isExpressionMatcher = isExpressionPattern.matcher(toParse);
        if (isExpressionMatcher.matches()) {
            int indexOfOpenParenthesis = toParse.indexOf('(');
            int indexOfClosedParenthesis = SyntaxUtils.getIndexOfClosedParenthesis(toParse, indexOfOpenParenthesis);
            String firstParenthesisContent = toParse.substring(indexOfOpenParenthesis + 1, indexOfClosedParenthesis);
            Expression absExpression = (new ExpressionSolver(firstParenthesisContent)).getExpression();
            currentIndex += indexOfClosedParenthesis + 1;
            if (currentIndex < toParse.length() && toParse.charAt(currentIndex) == '!') { // TODO: manage multiple factorial (maybe with 'while' loop)
                BigDecimal absExpressionValue = absExpression.getValue();
                String absExpressionValueAsString = absExpressionValue.toPlainString();
                if (absExpressionValueAsString.contains(".") || absExpressionValueAsString.startsWith("-")) {
                    throw new IllegalArgumentException("Factorial must be a positive integer");
                }
                if (toParse.startsWith("-")) {
                    return new Factorial(Sign.MINUS, new BigInteger(absExpressionValueAsString));
                } else {
                    return new Factorial(new BigInteger(absExpressionValueAsString));
                }
            }
            if (toParse.startsWith("-")) {
                return new Expression(Sign.MINUS, absExpression);
            } else {
                return absExpression;
            }
        }


        // Factor = Number!
        Pattern isFactorialPattern = Pattern.compile(Constants.IS_FACTORIAL_REGEX); // TODO: manage multiple factorial
        Matcher isFactorialMatcher = isFactorialPattern.matcher(toParse);
        if (isFactorialMatcher.matches()) {
            currentIndex += (isFactorialMatcher.end(1) + 1);
            return new Factorial(new BigInteger(isFactorialMatcher.group(1)));
        }

        // Factor = Number
        Pattern startWithNumberPattern = Pattern.compile(Constants.IS_GENERIC_NUM_REGEX);
        Matcher startWithNumberMatcher = startWithNumberPattern.matcher(toParse);
        if (startWithNumberMatcher.matches()) {
            currentIndex += startWithNumberMatcher.end(1);
            return new Number(startWithNumberMatcher.group(1));
        }

        throw new UnsupportedOperationException();
    }


    private Exponential tryToGetExponential(String toParse) {

    /*
         CASE 1: Exponential ::= (+/-) Number^Number
         CASE 2: Exponential ::= (+/-) Number^(Expression)
         CASE 3: Exponential ::= (+/-) (Expression)^Number
         CASE 4: Exponential ::= (+/-) (Expression)^(Expression)
         CASE 5: Exponential ::= (+/-) Number^Exponential
    */

        Exponential exponential = null;
        Sign sign = toParse.startsWith("-") ? Sign.MINUS : Sign.PLUS;

        Pattern expCase1Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_1_REGEX);
        Matcher expCase1Matcher = expCase1Pattern.matcher(toParse);
        if (expCase1Matcher.matches()) {
            String baseAsString = expCase1Matcher.group(1);
            String exponentAsString = expCase1Matcher.group(3);
            exponential = new Exponential(sign, new Number(baseAsString), new Number(exponentAsString));
            currentIndex += expCase1Matcher.end(3);
        }

        Pattern expCase2Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_2_REGEX);
        Matcher expCase2Matcher = expCase2Pattern.matcher(toParse);
        if (expCase2Matcher.matches()) {
            String baseAsString = expCase2Matcher.group(1);
            String exponentAsString = expCase2Matcher.group(3);
            exponential = new Exponential(sign, new Number(baseAsString), new Number(ExpressionSolver.evaluate(exponentAsString)));
            currentIndex += expCase2Matcher.end(3);
        }

        Pattern expCase3Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_3_REGEX);
        Matcher expCase3Matcher = expCase3Pattern.matcher(toParse);
        if (expCase3Matcher.matches()) {
            String baseAsString = expCase3Matcher.group(1);
            String exponentAsString = expCase3Matcher.group(2);
            exponential = new Exponential(sign, new Number(ExpressionSolver.evaluate(baseAsString)), new Number(exponentAsString));
            currentIndex += expCase3Matcher.end(2);
        }



        /* case 5 */
        //        Pattern expCase1Pattern = Pattern.compile(Constants.IS_EXPONENTIAL_CASE_1_REGEX);
//        Matcher expCase1Matcher = expCase1Pattern.matcher(toParse);
//        if (expCase1Matcher.matches()) {
//            String baseAsString = expCase1Matcher.group(1);
//            String exponentAsString = expCase1Matcher.group(3);
//            BigDecimal exponent = ExpressionSolver.evaluate(exponentAsString);
//            Exponential firstBase = new Exponential(sign, new Number(baseAsString), new Number(exponent));
////            Exponential firstExponent = this.tryToGetExponential(toParse.substring(expCase1Matcher.end(3)));
//            exponential = new Exponential(firstBase, firstExponent);
//        }


        // TODO: Manage case 4,5

        return exponential;

    }


}
