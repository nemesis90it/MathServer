package com.nemesis.mathcore.expressionsolver.expression.components;

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
            for (Order value : Order.values()) {
                if (value.baseClass.equals(clazz)) {
                    return value.weight;
                }
            }
            return 0;
        }

    }

    public static int compare(Object o1, Object o2) {
        return Order.getWeight((o2.getClass())) - Order.getWeight(o1.getClass());
    }

}
