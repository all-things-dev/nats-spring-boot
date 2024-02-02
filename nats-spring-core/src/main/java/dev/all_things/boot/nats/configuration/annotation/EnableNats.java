package dev.all_things.boot.nats.configuration.annotation;

import java.lang.annotation.*;

import dev.all_things.boot.nats.configuration.NatsAnnotationsConfigurationSelector;
import org.springframework.context.annotation.Import;

/**
 * Enables support for NATS abstractions e.g. {@link NatsListener}.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(NatsAnnotationsConfigurationSelector.class)
public @interface EnableNats
{
}
