package es.predictia.util;

import java.io.Serializable;

import com.google.common.base.Supplier;

public interface SerializableSupplier<T> extends Serializable, Supplier<T> {

}
