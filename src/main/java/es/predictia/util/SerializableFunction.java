package es.predictia.util;

import java.io.Serializable;

import com.google.common.base.Function;

public interface SerializableFunction<F, T> extends Serializable, Function<F, T> {

}
