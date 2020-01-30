package com.nemesis.mathcore.expressionsolver.expression.components;


/*
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= Factorial
         Factor ::= (Expression)
 */

import com.nemesis.mathcore.expressionsolver.expression.operators.ExpressionOperator;
import com.nemesis.mathcore.expressionsolver.expression.operators.Sign;
import com.nemesis.mathcore.expressionsolver.expression.operators.TermOperator;
import com.nemesis.mathcore.expressionsolver.utils.ComponentUtils;

import java.math.BigDecimal;

import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.MINUS;
import static com.nemesis.mathcore.expressionsolver.expression.operators.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;

public abstract class Factor extends Component {

    protected Sign sign = PLUS;

    @Override
    public BigDecimal getValue() {
        return value = sign.equals(PLUS) ? value : value.multiply(MINUS_ONE_DECIMAL);
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    /* Transform the given component in the simplest possible factor */
    public static Factor getFactor(Component c) {

        if (c instanceof ParenthesizedExpression parExpression) {
            Sign parExpressionSign = parExpression.getSign();
            if (parExpression.getOperator() == ExpressionOperator.NONE && parExpression.getTerm().getOperator() == TermOperator.NONE) {
                Factor factor = parExpression.getTerm().getFactor();
                if (parExpressionSign == MINUS) {
                    return getFactor(ComponentUtils.cloneAndChangeSign(factor));
                } else {
                    return getFactor(factor);
                }
            }
        }

        if (c instanceof Expression expression) {
            if (expression.getOperator() == ExpressionOperator.NONE && expression.getTerm().getOperator() == TermOperator.NONE) {
                return getFactor(expression.getTerm().getFactor());
            } else {
                return new ParenthesizedExpression((Expression) c);
            }
        }

        if (c instanceof Term term) {
            if (term.getOperator() == TermOperator.NONE) {
                return getFactor(term.getFactor());
            } else {
                return new ParenthesizedExpression(term);
            }
        }

        if (c instanceof Factor) {
            return (Factor) c;
        }

        throw new IllegalArgumentException("Unexpected type [" + c.getClass() + "]");

    }


    public static <T extends Factor> T getFactorOfSubtype(Component component, Class<? extends Factor> c) {
        Factor factor = getFactor(component);
        if (c.isAssignableFrom(factor.getClass())) {
            return (T) factor;
        } else {
            return null;
        }
    }


    public static boolean isFactorOfSubType(Component component, Class<? extends Factor> c) {
        return getFactorOfSubtype(component, c) != null;
    }

}
