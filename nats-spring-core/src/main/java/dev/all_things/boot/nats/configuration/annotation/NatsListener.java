package dev.all_things.boot.nats.configuration.annotation;

import java.lang.annotation.*;

import io.nats.client.Message;

/**
 * Specifies the method as NATS message handler.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NatsListener
{
	/**
	 * @return Subject from which the listener method will receive the messages.
	 */
	String subject();

	/**
	 * @return (Optional) subject to which method replies will be sent.
	 * However, precedence order for deciding the actual subject is as follows -
	 * 1. {@link Message#getSubject()} from the {@link Message} returned by listener method.
	 * 2. {@link Message#getReplyTo()} from the incoming message.
	 * 3. This value.
	 */
	String replySubject() default "";
}


