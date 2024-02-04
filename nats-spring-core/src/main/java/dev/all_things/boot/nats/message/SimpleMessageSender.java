package dev.all_things.boot.nats.message;

import java.nio.charset.StandardCharsets;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MessageSender} implementation for sending replies of type {@link String}.
 */
public class SimpleMessageSender
		extends MessageSender
{
	private static final Logger logger = LoggerFactory.getLogger(SimpleMessageSender.class);

	protected final String replySubject;

	public SimpleMessageSender(final String replySubject)
	{
		this.replySubject = replySubject;
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
		final String subject = StringUtils.defaultIfBlank(sourceMessage.getReplyTo(), this.replySubject).strip();

		if (subject.isBlank())
		{
			logger.warn("Unable to send message as 'subject' cannot be found in the current context.");
			return;
		}

		connection.publish(subject, ((String) reply).getBytes(StandardCharsets.UTF_8));
	}
}
