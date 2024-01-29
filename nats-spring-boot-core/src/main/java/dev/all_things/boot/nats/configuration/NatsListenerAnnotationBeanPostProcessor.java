package dev.all_things.boot.nats.configuration;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dev.all_things.boot.nats.annotation.NatsListener;
import dev.all_things.boot.nats.listener.NatsMessageListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;

public class NatsListenerAnnotationBeanPostProcessor
		implements ApplicationContextAware, BeanPostProcessor, SmartInitializingSingleton, Ordered
{
	private static final Logger logger = LoggerFactory.getLogger(NatsListenerAnnotationBeanPostProcessor.class);

	private final Map<String, Set<MethodHandle>> listeners = new ConcurrentHashMap<>();
	private final Set<Class<?>> ineligibleClasses = ConcurrentHashMap.newKeySet();

	private ApplicationContext context;

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName)
	{
		final Class<?> beanClass = bean.getClass();

		if (this.ineligibleClasses.contains(beanClass))
		{
			return bean;
		}

		final Class<?> targetClass = AopUtils.getTargetClass(bean);
		final Map<Method, NatsListener> methods = MethodIntrospector.selectMethods(targetClass, this::findAnnotatedMethods);

		for (final Map.Entry<Method, NatsListener> entry : methods.entrySet())
		{
			final Method method = entry.getKey();
			final NatsListener annotation = entry.getValue();

			logger.info("Found method '{}' with annotation '{}'", method, annotation.subject());

			try
			{
				final MethodHandle methodRef = MethodHandles.publicLookup().unreflect(method).bindTo(bean);

				this.listeners.computeIfAbsent(annotation.subject(), key -> ConcurrentHashMap.newKeySet()).add(methodRef);
			}
			catch (final IllegalAccessException e)
			{
				final String message = String.format("Failed to access method '%s' due to '%s' ..", method.getName(), e.getMessage());

				throw new BeanInitializationException(message, e);
			}

		}

		if (methods.isEmpty())
		{
			this.ineligibleClasses.add(beanClass);
		}

		return bean;
	}

	@Override
	public void afterSingletonsInstantiated()
	{
		final var container = this.context.getBean("natsMessageListenerContainer", NatsMessageListenerContainer.class);

		for (final var entry : this.listeners.entrySet())
		{
			final String subject = entry.getKey();
			final Set<MethodHandle> methodRefs = entry.getValue();

			for (final MethodHandle methodRef : methodRefs)
			{
				container.registerListener(subject, methodRef);
			}
		}

		container.start();
	}


	private NatsListener findAnnotatedMethods(final Method method)
	{
		return method.getDeclaredAnnotation(NatsListener.class);
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.context = applicationContext;
	}

	/**
	 * Returns lowest precedence initialization order to delay bean initialization until all beans are ready.
	 *
	 * @return initialization order.
	 */
	@Override
	public int getOrder()
	{
		return LOWEST_PRECEDENCE;
	}
}
