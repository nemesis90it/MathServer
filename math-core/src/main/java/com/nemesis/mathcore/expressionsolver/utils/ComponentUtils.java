package com.nemesis.mathcore.expressionsolver.utils;


import com.nemesis.mathcore.expressionsolver.expression.components.*;

public class ComponentUtils {

    public static Factor getFactor(Component c) {
        if (c instanceof Term) {
            return new ParenthesizedExpression((Term) c);
        } else if (c instanceof Factor) {
            return (Factor) c;
        } else if (c instanceof Expression) {
            return new ParenthesizedExpression((Expression) c);
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Term getTerm(Component c) {
        if (c instanceof Term) {
            return (Term) c;
        } else if (c instanceof Factor) {
            return new Term((Factor) c);
        } else if (c instanceof Expression) {
            return new Term(new ParenthesizedExpression((Expression) c));
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }

    public static Expression getExpression(Component c) {
        if (c instanceof Term) {
            return new Expression((Term) c);
        } else if (c instanceof Factor) {
            return new Expression(new Term((Factor) c));
        } else if (c instanceof Expression) {
            return (Expression) c;
        } else {
            throw new RuntimeException("Unexpected type [" + c.getClass() + "]");
        }
    }
}
