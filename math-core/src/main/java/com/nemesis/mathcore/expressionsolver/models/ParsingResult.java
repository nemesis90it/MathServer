package com.nemesis.mathcore.expressionsolver.models;

public class ParsingResult<T extends Component> {

    private T component;
    private Integer parsedIndex;

    public ParsingResult(T component, Integer parsedIndex) {
        this.component = component;
        this.parsedIndex = parsedIndex;
    }

    public T getComponent() {
        return component;
    }

    public void setComponent(T component) {
        this.component = component;
    }

    public Integer getParsedIndex() {
        return parsedIndex;
    }

    public void setParsedIndex(Integer parsedIndex) {
        this.parsedIndex = parsedIndex;
    }
}
