package com.narc.arclient.process;

public interface Processor<T, S> {
    S process(T t);
}
