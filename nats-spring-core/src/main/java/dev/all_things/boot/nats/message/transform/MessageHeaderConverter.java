package dev.all_things.boot.nats.message.transform;

import java.util.Objects;

import io.nats.client.Message;

public class MessageHeaderConverter
		implements MessageAttributeConverter
{
	private final String headerName;

	public MessageHeaderConverter(final String headerName)
	{
		Objects.requireNonNull(headerName, "Header name must not be null");

		this.headerName = headerName;
	}

	@Override
	public String convert(final Message message)
	{
		return message.hasHeaders() ? message.getHeaders().getFirst(this.headerName) : null;
	}
}
