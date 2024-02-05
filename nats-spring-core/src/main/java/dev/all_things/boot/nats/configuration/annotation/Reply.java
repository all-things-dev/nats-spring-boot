package dev.all_things.boot.nats.configuration.annotation;

import java.lang.annotation.*;

import io.nats.client.Message;

/**
 * Specifies method parameter as collections of NATS {@link Message} headers.
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reply
{
	/**
	 * @return (Optional) subject to which listener-method replies will be sent.
	 * However, precedence order for deciding the actual subject is as follows -
	 * 1. {@link Message#getSubject()} from the {@link Message} returned by listener method.
	 * 2. {@link Message#getReplyTo()} from the incoming message.
	 * 3. This value.
	 */
	String subject();
}


