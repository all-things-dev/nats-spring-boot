package dev.all_things.boot.nats.connection;

public class ConnectionException
		extends RuntimeException
{
	public ConnectionException(final String message)
	{
		super(message);
	}

	public ConnectionException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
