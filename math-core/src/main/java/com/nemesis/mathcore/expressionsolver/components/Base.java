package com.nemesis.mathcore.expressionsolver.components;

import java.util.Arrays;

public abstract class Base extends Factor {

    private enum Order {

        ABS_EXPR(AbsExpression.class, 110),
        PAR_EXPR(ParenthesizedExpression.class, 100),
        ROOT_FUN(RootFunction.class, 60),
        FACTORIAL(Factorial.class, 55),
        VAR(Variable.class, 50),
        LOG(Logarithm.class, 45),
        MATH_UNARY_FUN(MathUnaryFunction.class, 43),
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

    public abstract Base getClone();

}
