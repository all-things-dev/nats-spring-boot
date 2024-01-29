package dev.all_things.boot.nats.listener;

import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NatsMessageListener
		implements AutoCloseable
{
	private static final Logger logger = LoggerFactory.getLogger(NatsMessageListener.class);

	private final Connection connection;
	private final String subject;
	private final MethodHandle methodRef;

	public NatsMessageListener(final Connection connection, final String subject, final MethodHandle methodRef)
	{
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(subject, "Subject must not be null");
		Objects.requireNonNull(methodRef, "MethodHandle must not be null");

		this.connection = connection;
		this.subject = subject;
		this.methodRef = methodRef;
	}

	public void start()
	{
		logger.info("Starting NATS message listener for subject '{}' ..", this.subject);

		this.connection.createDispatcher(this::onMessage).subscribe(this.subject);

		logger.info("NATS message listener for subject '{}' started ..", this.subject);
	}

	private void onMessage(final Message message)
	{
		try
		{
			this.methodRef.invokeWithArguments(new String(message.getData(), StandardCharsets.UTF_8));
		}
		catch (final Throwable e)
		{
			final String errorMessage = String.format("Failed to invoke message listener for subject '%s' due to '%s' ..", this.subject, e.getMessage());

			throw new RuntimeException(errorMessage, e);
		}
	}

	@Override
	public void close()
	{
		try (this.connection)
		{
			logger.trace("Closing NATS connection ..");
		}
		catch (final Exception e)
		{
			logger.error("Failed to close NATS connection due to '{}' ..", e.getMessage());
			logger.trace(e.getMessage(), e);
		}

		logger.info("NATS connection closed ..");
	}
}
