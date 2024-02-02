package dev.all_things.boot.nats.configuration.annotation;

import java.lang.annotation.*;

import io.nats.client.Message;

/**
 * Specifies method parameter as collections of NATS {@link Message} headers.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Headers
{

}


