package com.nemesis.mathcore.expressionsolver.rewritting.rules;

import com.nemesis.mathcore.expressionsolver.expression.components.*;
import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.rewritting.Rule;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator.*;

/* Expected T(1)ree:

   (1) -----------------------------------------------EXPONENTIAL---------------------------------------------
   (2) ----------------------------------BASE-----------------------------------   ----------EXPONENT---------
   (3)                        <ParenthesizedExpression>                                  <Integer number>
   (4) -----------------------TERM---------------------   OP  --SUB_EXPRESSION--
   (5)                                                  <none>      <null>

   (6) -----FACTOR-----  OP  --------SUB_TERM--------
   (7)    <Constant>     ∀   --FACTOR--  OP --SUB_ST--    // NOTE: factor and subTerm can be inverted (see below)
   (8)                         <Base>  <none>  <null>

   (6_bis) -----------FACTOR-----------  OP  -----------SUB_TERM-----------
   (7_inv)            <Base>             ∀   ---FACTOR---   OP   --SUB_ST--
   (8_inv)                                    <Constant>  <none>  <null>

*/
public class ApplyConstantExponentToTerm implements Rule {

    @Override
    public Predicate<Component> precondition() {
        return component -> {

            if (!(component instanceof Exponential)) { // see line (1)
                return false;
            }

            Factor exponent = ((Exponential) component).getExponent();
            Base base = ((Exponential) component).getBase();
            if (!(base instanceof ParenthesizedExpression)) {  // see line (3)
                return false;
            }

            boolean exponentIsIntValue = true;
            try {
                exponent.getValue().intValueExact();
            } catch (ArithmeticException e) {
                exponentIsIntValue = false;
            }

            if (!(exponent instanceof Constant) && !exponentIsIntValue) {  // see line (3)
                return false;
            }

            if (!(((ParenthesizedExpression) base).getOperator() == ExpressionOperator.NONE)) {  // see line (5)
                return false;
            }

            Term term = (((ParenthesizedExpression) base).getExpression()).getTerm();
            Factor factor = term.getFactor();
            Term subTerm = term.getSubTerm();

            if (subTerm == null || !(subTerm.getOperator() == NONE)) {  // see line (8) or (8_inv)
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
            • (a^n) is named 'coefficientExponent' and its value is named 'coefficient'
            • x^n   is named 'varExponential'
     */
    @Override
    public Function<Component, ? extends Component> transformer() {
        return component -> {

            Constant exponent = (Constant) ((Exponential) component).getExponent();
            Base base = ((Exponential) component).getBase();
            Term term = (((ParenthesizedExpression) base).getExpression()).getTerm();
            Factor factor = term.getFactor();
            Term subTerm = term.getSubTerm();

            Exponential coefficientExponent = null;
            Exponential varExponential = null;

            if (factor instanceof Constant) {  // see line (7)
                coefficientExponent = new Exponential((Base) factor, exponent);
                varExponential = new Exponential((Base) subTerm.getFactor(), exponent);
            } else if (factor instanceof Base) {  // see line (7_inv)
                coefficientExponent = new Exponential((Base) subTerm.getFactor(), exponent);
                varExponential = new Exponential((Base) factor, exponent);
            }

            if (coefficientExponent == null || varExponential == null) {
                throw new RuntimeException("Unexpected error");
            }

            Constant coefficient = new Constant(coefficientExponent.getValue());

            if (term.getOperator() == MULTIPLY) { // rules [1] and [2]
                return new Term(coefficient, MULTIPLY, new Term(varExponential));
            } else {
                if (factor instanceof Constant) { // rule [3]
                    return new Term(coefficient, DIVIDE, new Term(varExponential));
                } else {    // rule [4]
                    return new Term(varExponential, DIVIDE, new Term(coefficient));
                }
            }

        };
    }
}
