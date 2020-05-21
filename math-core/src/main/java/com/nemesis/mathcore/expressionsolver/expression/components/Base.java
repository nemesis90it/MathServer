package com.nemesis.mathcore.expressionsolver.expression.components;

import java.util.Arrays;

public abstract class Base extends Factor {

    private enum Order {

        PAR_EXPR(ParenthesizedExpression.class, 100),
        LOG(Logarithm.class, 90),
        MATH_UNARY_FUN(MathUnaryFunction.class, 70),
        ROOT_FUN(RootFunction.class, 60),
        FACTORIAL(Factorial.class, 55),
        VAR(Variable.class, 50),
        FACT(Factorial.class, 40),
        CONST(Constant.class, 30);

        private Class<?> baseClass;
        private int weight;

        Order(Class<?> baseClass, int weight) {
            this.baseClass = baseClass;
            this.weight = weight;
        }

        private static int getWeight(Class<?> clazz) {
            return Arrays.stream(Order.values())
                    .filter(value -> value.baseClass.equals(clazz))
                    .findFirst()
                    .map(value -> value.weight)
                    .orElse(0);
        }

    }

    public static int compare(Base b1, Base b2) {
        return Order.getWeight(b2.getClass()) - Order.getWeight(b1.getClass());
    }

}
