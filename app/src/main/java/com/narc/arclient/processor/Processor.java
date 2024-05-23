package com.narc.arclient.processor;

public interface Processor<T, S> {
    S process(T t);
}
