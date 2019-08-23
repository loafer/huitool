package com.huitool.util;

import java.io.Serializable;
import java.util.function.Function;

/**
 * <描述信息>
 */
public class Functions {
    @FunctionalInterface
    public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {}
}
