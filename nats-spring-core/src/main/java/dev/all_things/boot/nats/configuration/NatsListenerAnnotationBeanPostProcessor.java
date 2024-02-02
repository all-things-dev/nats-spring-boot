package dev.all_things.boot.nats.configuration;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dev.all_things.boot.nats.configuration.annotation.NatsListener;
import dev.all_things.boot.nats.message.NatsMessageListenerContainer;
import dev.all_things.boot.nats.message.NatsMessageListenerInitializer;
import dev.all_things.boot.nats.message.transform.ListenerArgumentGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;

/**
 * {@link BeanPostProcessor} implementation that processes methods annotated with {@link NatsListener} and
 * configures corresponding message listeners in the {@link NatsMessageListenerContainer}.
 *
 * @see NatsListener
 * @see NatsMessageListenerContainer
 */
public class NatsListenerAnnotationBeanPostProcessor
		implements ApplicationContextAware, BeanPostProcessor, SmartInitializingSingleton, Ordered
{
	private static final Logger logger = LoggerFactory.getLogger(NatsListenerAnnotationBeanPostProcessor.class);

	private static record MethodInfo(MethodHandle handle, ListenerArgumentGenerator argumentGenerator)
	{
	}

	private final Set<NatsMessageListenerInitializer> initializers = ConcurrentHashMap.newKeySet();
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
			this.initializers.add(new NatsMessageListenerInitializer(bean, targetClass, entry.getKey(), entry.getValue()));
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

		for (final var initializer : this.initializers)
		{
			container.registerListener(initializer);
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
