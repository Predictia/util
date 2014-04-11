package es.predictia.util;

import java.io.Serializable;

import com.google.common.base.Predicate;

public interface SerializablePredicate<T> extends Serializable, Predicate<T> {

}
