package dev.all_things.boot.nats.message.transform;

import java.util.*;
import java.util.stream.Collectors;

import io.nats.client.Message;

public class MessageHeaderMultiMapConverter
		implements MessageAttributeConverter
{
	public static final MessageHeaderMultiMapConverter INSTANCE = new MessageHeaderMultiMapConverter();

	@Override
	public Map<String, List<String>> convert(final Message message)
	{
		if (message.hasHeaders())
		{
			return message.getHeaders().entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
		}

		return Collections.emptyMap();
	}
}
