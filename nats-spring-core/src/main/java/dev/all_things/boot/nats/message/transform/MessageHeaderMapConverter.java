package dev.all_things.boot.nats.message.transform;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import io.nats.client.Message;

public class MessageHeaderMapConverter
		implements MessageAttributeConverter
{
	public static final MessageHeaderMapConverter INSTANCE = new MessageHeaderMapConverter();

	@Override
	public Map<String, String> convert(final Message message)
	{
		if (message.hasHeaders())
		{
			return message.getHeaders().entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().get(0)));
		}

		return Collections.emptyMap();
	}
}
