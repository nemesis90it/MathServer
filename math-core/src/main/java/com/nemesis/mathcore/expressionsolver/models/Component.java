package com.nemesis.mathcore.expressionsolver.models;

public abstract class Component<T> {

    T value = null;

    public abstract T getValue();
}
