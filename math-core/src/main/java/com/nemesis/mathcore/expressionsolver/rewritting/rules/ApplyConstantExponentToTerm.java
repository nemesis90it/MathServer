package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.components.*;
import com.nemesis.mathcore.expressionsolver.exception.UnexpectedComponentTypeException;
import com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.operators.TermOperator.*;

/* Expected Tree:

   (1) -----------------------------------------------EXPONENTIAL---------------------------------------------
   (2) ----------------------------------BASE-----------------------------------   ----------EXPONENT---------
   (3)                        <ParenthesizedExpression>                                  <Integer number>
   (4) -----------------------TERM---------------------   OP  --SUB_EXPRESSION--
   (5)                                                  <none>      <null>

   (6)     ----FACTOR----  OP  ----------SUB_TERM----------
   (7)       <Constant>    ∀   ---FACTOR---  OP ---SUB_ST---    // NOTE: factor and subTerm can be inverted (see below)
   (8)                             <Base>  <none>  <null>

   (6_bis) ----FACTOR----  OP  -------------SUB_TERM-------------
   (7_inv)     <Base>      ∀   ----FACTOR----   OP  ---SUB_ST---
   (8_inv)                        <Constant>   <none>   <null>

*/
public class ApplyConstantExponentToTerm implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component -> {

            if (!(component instanceof Exponential exponential)) { // see line (1)
                return false;
            }

            if (!(exponential.getBase() instanceof ParenthesizedExpression base) || !ComponentUtils.isInteger(exponential.getExponent())) {  // see line (3)
                return false;
            }

            if (base.getOperator() != ExpressionOperator.NONE) {  // see line (5)
                return false;
            }

            Term term = (base.getExpression()).getTerm();

            Factor factor = term.getFactor();
            Term subTerm = term.getSubTerm();

            if (subTerm == null || subTerm.getOperator() != NONE) {  // see line (8) or (8_inv)
                return false;
            }

            if (factor instanceof Constant) {  // see line (7)
                return subTerm.getFactor() instanceof Base;  // see line (8)
            } else if (factor instanceof Base) {  // see line (7_inv)
                return subTerm.getFactor() instanceof Constant;  // see line (8_inv)
            }

            return false;
        };
    }

    /*
        RULES:
            [1]   (a*x)^n --> (a^n)x^n
            [2]   (x*a)^n --> (a^n)x^n
            [3]   (a/x)^n --> a^n/x^n
            [4]   (x/a)^n --> x^n/a^n
        NOTE:
            • (a^n) is named 'coefficientExponential' and its value is named 'coefficient'
            • x^n   is named 'variableExponential'
     */
    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            final Constant exponent = (Constant) ((Exponential) component).getExponent();
            final Base base = ((Exponential) component).getBase();
            final Term term = (((ParenthesizedExpression) base).getExpression()).getTerm();
            final Factor factor = term.getFactor();
            final Term subTerm = term.getSubTerm();

            if (!(subTerm.getFactor() instanceof Base subFactor)) {
                throw new UnexpectedComponentTypeException("Unexpected type [" + subTerm.getFactor().getClass() + "]");
            }

            final Exponential coefficientExponential;
            final Exponential variableExponential;


            if (factor instanceof Constant c) {  // see line (7)
                coefficientExponential = new Exponential(c, exponent);
                variableExponential = new Exponential(subFactor, exponent);
            } else if (factor instanceof Base b) {  // see line (7_inv)
                coefficientExponential = new Exponential(subFactor, exponent);
                variableExponential = new Exponential(b, exponent);
            } else {
                throw new UnexpectedComponentTypeException("Unexpected type [" + factor.getClass() + "]");
            }

            Constant coefficient = new Constant(coefficientExponential.getValue());

            if (term.getOperator() == MULTIPLY) { // rules [1] and [2]
                return new Term(coefficient, MULTIPLY, variableExponential);
            } else {
                if (factor instanceof Constant) { // rule [3]
                    return new Term(coefficient, DIVIDE, variableExponential);
                } else {    // rule [4]
                    return new Term(variableExponential, DIVIDE, coefficient);
                }
            }
        };
    }
}
