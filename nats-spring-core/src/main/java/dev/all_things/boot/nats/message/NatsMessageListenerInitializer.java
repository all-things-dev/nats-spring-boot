package dev.all_things.boot.nats.message;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;

import dev.all_things.boot.nats.configuration.annotation.*;
import dev.all_things.boot.nats.message.transform.*;
import io.nats.client.Connection;
import io.nats.client.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;

public class NatsMessageListenerInitializer
{
	private static final Logger logger = LoggerFactory.getLogger(NatsMessageListenerInitializer.class);

	private final Object bean;
	private final Class<?> targetClass;
	private final Method method;
	private final NatsListener annotation;
	private final boolean isVoid;
	private final String replyTo;

	private MessageSender messageSender = MessageSender.noOpMessageSender;

	private MethodHandle methodRef;
	private ListenerArgumentGenerator argumentGenerator;

	public NatsMessageListenerInitializer(final Object bean, final Class<?> targetClass, final Method method,
										  final NatsListener annotation)
	{
		Objects.requireNonNull(bean, "Bean must not be null");
		Objects.requireNonNull(targetClass, "Target class must not be null");
		Objects.requireNonNull(method, "Method must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");

		this.bean = bean;
		this.targetClass = targetClass;
		this.method = method;
		this.annotation = annotation;
		this.isVoid = method.getReturnType() == void.class;
		this.replyTo = StringUtils.defaultString(annotation.replyTo()).strip();

		validate();
	}

	public NatsMessageListener initialize(final Connection connection)
	{
		return new NatsMessageListener(connection, this.annotation.subject(), this.methodRef, this.argumentGenerator,
				this.messageSender);
	}

	private void validate()
	{
		if (this.isVoid && !this.replyTo.isBlank())
		{
			logger.warn("Ignoring replyTo ('{}') configuration for 'void' listener method '{}' ..",
					this.replyTo, this.method.getName());
		}

		if (!this.isVoid && !this.replyTo.isBlank())
		{
			this.messageSender = new MessageSender(this.replyTo);
		}

		final Parameter[] parameters = this.method.getParameters();
		final Type[] parameterTypes = this.method.getGenericParameterTypes();
		final MessageAttributeConverter[] converters = new MessageAttributeConverter[parameters.length];

		for (int i = 0, length = parameters.length; i < length; i++)
		{
			converters[i] = createMessageAttributeConverter(parameters[i], parameterTypes[i]);
		}

		this.argumentGenerator = new ListenerArgumentGenerator(converters);

		try
		{
			this.methodRef = MethodHandles.publicLookup().unreflect(this.method).bindTo(this.bean);
		}
		catch (final IllegalAccessException e)
		{
			final String message = String.format("Failed to access method '%s' due to '%s' ..",
					this.method.getName(), e.getMessage());

			throw new BeanInitializationException(message, e);
		}
	}

	private MessageAttributeConverter createMessageAttributeConverter(final Parameter parameter, final Type parameterType)
	{
		final Class<?> type = parameter.getType();

		if (type == Message.class)
		{
			return MessageAttributeConverter.noOpConverter;
		}

		if (type == byte[].class)
		{
			return MessageAttributeConverter.messageToBytesConverter;
		}

		if (parameter.isAnnotationPresent(Header.class))
		{
			return createMessageHeaderConverter(parameter, type);
		}

		if (parameter.isAnnotationPresent(Headers.class))
		{
			return createMessageHeaderConverter(parameter, type, parameterType);
		}

		if (type == String.class)
		{
			return MessageAttributeConverter.messageToStringConverter;
		}

		final String errorMessage = String.format("Unsupported type '%s' for parameter '%s' of listener method '%s'",
				type.getName(), parameter.getName(), this.method);

		throw new IllegalArgumentException(errorMessage);
	}

	private static MessageAttributeConverter createMessageHeaderConverter(final Parameter parameter, final Class<?> type)
	{
		if (type != String.class)
		{
			final String message = "Unsupported type '%s' for header parameter '%s'. Only 'java.lang.String' is supported.";

			throw new IllegalArgumentException(String.format(message, type.getName(), parameter.getName()));
		}

		final String headerName = StringUtils.defaultString(parameter.getAnnotation(Header.class).value()).strip();

		if (headerName.isBlank())
		{
			final String message = "Header name for parameter '%s' must not be blank";

			throw new IllegalArgumentException(String.format(message, parameter.getName()));
		}

		return new MessageHeaderConverter(headerName);
	}

	private static MessageAttributeConverter createMessageHeaderConverter(final Parameter parameter, final Class<?> type, final Type parameterType)
	{
		if (type != Map.class)
		{
			final String message = "Unsupported type '%s' for headers parameter '%s'. Only 'java.util.Map' is supported.";

			throw new IllegalArgumentException(String.format(message, type.getName(), parameter.getName()));
		}

		final Type[] types = ((ParameterizedType) parameterType).getActualTypeArguments();
		final Type keyType = types[0], valueType = types[1];

		if (keyType != String.class)
		{
			final String message = "Unsupported key type '%s' for header parameter '%s'. Only 'java.lang.String' is supported.";

			throw new IllegalArgumentException(String.format(message, type.getName(), parameter.getName()));
		}

		if (valueType == String.class)
		{
			return MessageHeaderMapConverter.INSTANCE;
		}

		if (valueType instanceof final ParameterizedType parameterizedType && parameterizedType.getRawType() == List.class)
		{
			final Type[] valueTypes = parameterizedType.getActualTypeArguments();

			if (valueTypes[0] != String.class)
			{
				final String message = "Unsupported value type '%s' for values in header parameter '%s'. Only 'java.util.List<java.lang.String>' is supported.";

				throw new IllegalArgumentException(String.format(message, valueType.getTypeName(), parameter.getName()));
			}

			return MessageHeaderMultiMapConverter.INSTANCE;
		}

		final String message = "Unsupported value type '%s' for header parameter '%s'. Only 'java.lang.String' or 'java.util.List<java.lang.String>' are supported.";

		throw new IllegalArgumentException(String.format(message, valueType.getTypeName(), parameter.getName()));
	}
}
