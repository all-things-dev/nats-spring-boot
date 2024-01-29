package dev.all_things.boot.nats.connection;

import io.nats.client.Connection;

public interface ConnectionFactory
{
	/**
	 * Creates and returns a valid NATS {@link Connection}.
	 *
	 * @return a valid NATS {@link Connection}.
	 * @throws ConnectionException if the connection could not be established.
	 */
	public Connection getConnection();

}
