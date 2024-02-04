package dev.all_things.boot.nats.message;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides consistent structure for sending message replies.
 */
public class MessageSender
{
	private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

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
	 * @param message    reply {@link Message}.
	 */
	public void send(final Connection connection, final Message message)
	{
		final String messageSubject = StringUtils.defaultIfBlank(message.getSubject(), this.subject);

		if (messageSubject.isBlank())
		{
			logger.warn("Unable to send message as 'subject' cannot be found in the current context.");
			return;
		}

		connection.publish(messageSubject, message.getHeaders(), message.getData());
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
