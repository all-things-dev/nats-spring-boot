package dev.all_things.boot.nats.configuration.annotation;

import java.lang.annotation.*;

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
	 * @return information required for sending replies.
	 */
	Reply reply() default @Reply(subject = "");
}


