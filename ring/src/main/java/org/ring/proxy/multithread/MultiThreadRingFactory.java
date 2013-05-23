package org.ring.proxy.multithread;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;

import org.ring.proxy.RingFactory;

/**
 * Multi-thread implementation of RingFactory, the asynchronized invokes will be
 * put into a thread pool defined.
 * 
 * @author yuxuan.wang
 * 
 */
public class MultiThreadRingFactory implements RingFactory {

	/**
	 * The thread pool asynchronized invokes execute in
	 */
	private ExecutorService executorService;

	/**
	 * Classloader who load delegate objects
	 */
	private ClassLoader delegateClassLoader;

	/**
	 * @param executorService
	 *            The thread pool asynchronized invokes execute in
	 */
	public MultiThreadRingFactory(ExecutorService executorService) {
		this(executorService, null);
	}

	/**
	 * @param executorService
	 *            The thread pool asynchronized invokes execute in
	 * @param delegateClassLoader
	 *            Classloader who load delegate objects
	 */
	public MultiThreadRingFactory(ExecutorService executorService, ClassLoader delegateClassLoader) {
		super();
		this.executorService = executorService;
		this.delegateClassLoader = delegateClassLoader;
	}

	@SuppressWarnings("unchecked")
	public <T> T delegate(Object obj, Class<T> inter) {
		final MultiThreadInvocationHandler handler = new MultiThreadInvocationHandler(obj, executorService);
		final ClassLoader classLoader = delegateClassLoader == null ? obj.getClass().getClassLoader() : delegateClassLoader;
		return (T) Proxy.newProxyInstance(classLoader, new Class[] { inter }, handler);
	}

}
