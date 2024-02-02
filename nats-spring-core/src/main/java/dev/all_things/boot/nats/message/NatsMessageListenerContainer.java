package dev.all_things.boot.nats.message;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.annotation.PreDestroy;

import dev.all_things.boot.nats.connection.NatsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages lifecycle of all {@link NatsMessageListener}.
 */
public class NatsMessageListenerContainer
{
	private static final Logger logger = LoggerFactory.getLogger(NatsMessageListenerContainer.class);

	private final NatsConnectionFactory factory;
	private final Set<NatsMessageListener> listeners;

	public NatsMessageListenerContainer(final NatsConnectionFactory factory)
	{
		this.factory = factory;
		this.listeners = ConcurrentHashMap.newKeySet();
	}

	/**
	 * Registers a {@link NatsMessageListener} using provided initializer.
	 */
	public void registerListener(final NatsMessageListenerInitializer initializer)
	{
		this.listeners.add(initializer.initialize(this.factory.getConnection()));
	}

	/**
	 * Starts all registered {@link NatsMessageListener}.
	 */
	public void start()
	{
		logger.info("Initializing NATS message listeners ..");

		this.listeners.forEach(NatsMessageListener::start);

		logger.info("NATS message listeners initialized ..");
	}

	/**
	 * Closes all registered {@link NatsMessageListener}.
	 */
	@PreDestroy
	protected void destroy()
	{
		this.listeners.forEach(NatsMessageListener::close);
	}
}
