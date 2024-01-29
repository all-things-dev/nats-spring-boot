package dev.all_things.boot.nats.connection;

import java.io.IOException;

import dev.all_things.boot.nats.configuration.NatsConnectionConfiguration;
import io.nats.client.*;

/**
 * A {@link ConnectionFactory} implementation that creates a NATS {@link Connection} using the provided
 * {@link NatsConnectionConfiguration}.
 */
public class NatsConnectionFactory
		implements ConnectionFactory
{
	private final NatsConnectionConfiguration configuration;

	public NatsConnectionFactory(final NatsConnectionConfiguration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public Connection getConnection()
	{
		try
		{
			final Options options = Options.builder().servers(this.configuration.getServers().toArray(new String[0]))
										   .userInfo(this.configuration.getUsername(), this.configuration.getPassword())
										   .build();

			return Nats.connect(options);
		}
		catch (final IOException | InterruptedException e)
		{
			throw new ConnectionException(e.getMessage(), e);
		}
	}
}
