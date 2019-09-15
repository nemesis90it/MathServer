package com.nemesis.mathcore.expressionsolver.models;


/*
         Factor ::= Number
         Factor ::= Exponential
         Factor ::= Factorial
         Factor ::= (Expression)
 */

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.nemesis.mathcore.expressionsolver.models.Sign.PLUS;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_DECIMAL;
import static com.nemesis.mathcore.expressionsolver.utils.Constants.MINUS_ONE_INTEGER;

public abstract class Factor<T> extends Component<T> {

    protected Sign sign = PLUS;

    @Override
    public T getValue() {
        if (value instanceof BigDecimal) {
            return value = sign.equals(PLUS) ? value : (T) ((BigDecimal) value).multiply(MINUS_ONE_DECIMAL);
        }
        if (value instanceof BigInteger) {
            return value = sign.equals(PLUS) ? value : (T) ((BigInteger) value).multiply(MINUS_ONE_INTEGER);
        }
        if (value instanceof String) {
            return value = sign.equals(PLUS) ? value : (T) ("-" + value);
        }
        throw new UnsupportedOperationException("Type [" + value.getClass() + "] not supported as factor");
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }
}
