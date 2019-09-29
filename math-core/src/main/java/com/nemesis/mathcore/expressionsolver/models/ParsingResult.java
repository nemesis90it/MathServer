package com.nemesis.mathcore.expressionsolver.models;

import com.nemesis.mathcore.expressionsolver.expression.components.Component;

public class ParsingResult<T extends Component> {

    private T component;
    private Integer parsedChars;

    public ParsingResult(T component, Integer parsedChars) {
        this.component = component;
        this.parsedChars = parsedChars;
    }

    public T getComponent() {
        return component;
    }

    public void setComponent(T component) {
        this.component = component;
    }

    public Integer getParsedChars() {
        return parsedChars;
    }

    public void setParsedChars(Integer parsedChars) {
        this.parsedChars = parsedChars;
    }
}
