package dev.all_things.boot.nats.message;

import io.nats.client.Connection;

/**
 * Provides consistent structure for sending message replies.
 */
public class MessageSender
{
	public static final MessageSender noOpMessageSender = new NoOpMessageSender();

	private final String subject;

	public MessageSender(final String subject)
	{
		this.subject = subject;
	}

	/**
	 * Sends provided {message} to the subject {replyTo}.
	 *
	 * @param connection connection through which reply will be snet.
	 * @param message    reply content.
	 */
	public void send(final Connection connection, final byte[] message)
	{
		connection.publish(this.subject, message);
	}

	/**
	 * No-Op implementation of {@link MessageSender}.
	 */
	private static final class NoOpMessageSender
			extends MessageSender
	{
		private NoOpMessageSender()
		{
			super("");
		}

		@Override
		public void send(final Connection connection, final byte[] message)
		{
			// No-op
		}
	}
}
