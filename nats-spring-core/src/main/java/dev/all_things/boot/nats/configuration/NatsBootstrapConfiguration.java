package dev.all_things.boot.nats.configuration;

import dev.all_things.boot.nats.configuration.annotation.EnableNats;
import dev.all_things.boot.nats.connection.NatsConnectionFactory;
import dev.all_things.boot.nats.message.NatsMessageListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Bootstraps NATS abstractions.
 * <p>
 * This configuration class is automatically imported when using {@link EnableNats} annotation.
 *
 * @see EnableNats
 * @see NatsAnnotationsConfigurationSelector
 */
public class NatsBootstrapConfiguration
		implements ImportBeanDefinitionRegistrar
{
	private static final Logger logger = LoggerFactory.getLogger(NatsBootstrapConfiguration.class);

	/**
	 * Registers beans required for NATS abstractions.
	 *
	 * @param metadata metadata associated with the annotation which triggered this configuration.
	 * @param registry target {@link BeanDefinitionRegistry} for registering new bean definitions.
	 */
	@Override
	public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry)
	{
		registerMissingBean(registry, "natsConnectionFactory", NatsConnectionFactory.class);
		registerMissingBean(registry, "natsMessageListenerContainer", NatsMessageListenerContainer.class);
		registerMissingBean(registry, "natsListenerAnnotationBeanPostProcessor", NatsListenerAnnotationBeanPostProcessor.class);
	}

	/**
	 * Registers a bean if it is missing.
	 *
	 * @param registry  target {@link BeanDefinitionRegistry} for registering new bean definition.
	 * @param beanName  name of the bean to register.
	 * @param beanClass class of the bean to register.
	 */
	private static void registerMissingBean(final BeanDefinitionRegistry registry, final String beanName, final Class<?> beanClass)
	{
		if (!registry.containsBeanDefinition(beanName))
		{
			registry.registerBeanDefinition(beanName, new RootBeanDefinition(beanClass));
		}
	}
}
