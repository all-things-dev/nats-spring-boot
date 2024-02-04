package dev.all_things.boot.nats.message;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides consistent structure for sending message replies.
 */
public class MessageSender
{
	private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

	public static final MessageSender noOpMessageSender = new NoOpMessageSender();

	/**
	 * Sends provided {message} to the subject resolved as per precedence.
	 *
	 * @param connection    connection through which reply will be sent.
	 * @param sourceMessage source {@link Message} object.
	 * @param reply         reply {@link Message} object.
	 */
	public void send(final Connection connection, final Message sourceMessage, final Object reply)
	{
		final Message message = (Message) reply;

		connection.publish(message.getSubject(), message.getHeaders(), message.getData());
	}

	/**
	 * No-Op implementation of {@link MessageSender}.
	 */
	private static final class NoOpMessageSender
			extends MessageSender
	{
		private NoOpMessageSender()
		{

		}

		@Override
		public void send(final Connection connection, final Message sourceMessage, final Object reply)
		{
			// No-op
		}
	}
}
