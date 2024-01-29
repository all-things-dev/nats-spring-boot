package dev.all_things.boot.nats.configuration;

import dev.all_things.boot.nats.annotation.EnableNats;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Allows {@link NatsBootstrapConfiguration} to be applied as late as possible.
 */
@Order
public class NatsAnnotationsConfigurationSelector
		implements DeferredImportSelector
{
	/**
	 * Provides configuration classes to import.
	 *
	 * @param metadata metadata associated with the annotation which triggered this selector e.g., {@link EnableNats}.
	 * @return FQDN of the configuration classes to import, if applicable or empty array.
	 */
	@Override
	public String[] selectImports(final AnnotationMetadata metadata)
	{
		return new String[]{ NatsBootstrapConfiguration.class.getName() };
	}
}
