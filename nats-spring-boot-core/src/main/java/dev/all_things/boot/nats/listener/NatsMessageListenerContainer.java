package dev.all_things.boot.nats.listener;

import java.lang.invoke.MethodHandle;
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

	public void registerListener(final String subject, final MethodHandle methodRef)
	{
		this.listeners.add(new NatsMessageListener(this.factory.getConnection(), subject, methodRef));
	}

	public void start()
	{
		logger.info("Initializing NATS message listeners ..");

		this.listeners.forEach(NatsMessageListener::start);

		logger.info("NATS message listeners initialized ..");
	}

	@PreDestroy
	protected void destroy()
	{
		this.listeners.forEach(NatsMessageListener::close);
	}
}
