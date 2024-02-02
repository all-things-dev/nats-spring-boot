package dev.all_things.boot.nats.configuration.annotation;

import java.lang.annotation.*;

import io.nats.client.Message;

/**
 * Specifies method parameter as NATS {@link Message} header.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Header
{
	/**
	 * @return name of the message header.
	 */
	String value() default "";
}


