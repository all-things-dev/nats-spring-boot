package dev.all_things.boot.nats.message;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

import dev.all_things.boot.nats.message.transform.ListenerArgumentGenerator;
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
	private final ListenerArgumentGenerator argumentGenerator;
	private final MessageSender replySender;

	NatsMessageListener(final Connection connection, final String subject, final MethodHandle methodRef,
						final ListenerArgumentGenerator argumentGenerator, final MessageSender replySender)
	{
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
		this.subject = Objects.requireNonNull(subject, "Subject must not be null");
		this.methodRef = Objects.requireNonNull(methodRef, "Method handle must not be null");
		this.argumentGenerator = Objects.requireNonNull(argumentGenerator, "Argument generator must not be null");
		this.replySender = Objects.requireNonNull(replySender, "Reply sender must not be null");
	}

	/**
	 * Starts operation of this message listener.
	 */
	public void start()
	{
		this.connection.createDispatcher(this::onMessage).subscribe(this.subject);
	}

	/**
	 * Processes incoming message.
	 *
	 * @param message incoming message.
	 */
	private void onMessage(final Message message)
	{
		try
		{
			final Object reply = this.methodRef.invokeWithArguments(this.argumentGenerator.generate(message));

			if (reply != null)
			{
				this.replySender.send(this.connection, message, reply);
			}
		}
		catch (final Throwable e)
		{
			logger.error(e.getMessage(), e);

			final String errorMessage = String.format("Failed to invoke message listener for subject '%s' due to '%s' ..", this.subject, e.getMessage());

			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * Cleans up acquired {@link Connection}.
	 */
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

		logger.debug("NATS connection closed ..");
	}
}
