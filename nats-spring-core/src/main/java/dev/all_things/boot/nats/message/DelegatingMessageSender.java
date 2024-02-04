package dev.all_things.boot.nats.message;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MessageSender} implementation for sending replies of any supported type.
 */
public class DelegatingMessageSender
		extends MessageSender
{
	private static final Logger logger = LoggerFactory.getLogger(DelegatingMessageSender.class);

	protected final String replySubject;
	protected final MessageSender simpleMessageSender, rawMessageSender;

	public DelegatingMessageSender(final String replySubject)
	{
		this.replySubject = replySubject;
		this.rawMessageSender = new RawMessageSender(replySubject);
		this.simpleMessageSender = new SimpleMessageSender(replySubject);
	}

	/**
	 * Sends provided {message} to the subject resolved as per precedence.
	 *
	 * @param connection    connection through which reply will be sent.
	 * @param sourceMessage source {@link Message} object.
	 * @param reply         reply {@link Message} object.
	 */
	@Override
	public void send(final Connection connection, final Message sourceMessage, final Object reply)
	{
		if (reply instanceof Message)
		{
			super.send(connection, sourceMessage, reply);
			return;
		}

		if (reply instanceof String)
		{
			this.simpleMessageSender.send(connection, sourceMessage, reply);
			return;
		}

		if (reply instanceof byte[])
		{
			this.rawMessageSender.send(connection, sourceMessage, reply);
			return;
		}

		throw new IllegalArgumentException("Unsupported reply type '" + reply.getClass().getSimpleName() + "' for listener method");
	}
}
