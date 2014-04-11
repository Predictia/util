package es.predictia.util;

import com.google.common.base.Predicate;

public class SerializablePredicateImpl<T> implements SerializablePredicate<T>{

	private final transient Predicate<T> delegate;
	
	public SerializablePredicateImpl(Predicate<T> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public boolean apply(T arg0) {
		return delegate.apply(arg0);
	}

	private static final long serialVersionUID = -6029350649373254002L;

}
