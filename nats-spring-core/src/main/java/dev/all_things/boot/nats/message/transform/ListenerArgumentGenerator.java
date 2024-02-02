package dev.all_things.boot.nats.message.transform;

import java.util.function.Function;

import io.nats.client.Message;

public class ListenerArgumentGenerator
{
	private final MessageAttributeConverter[] converters;
	private final Function<Message, Object[]> generator;

	public ListenerArgumentGenerator(final MessageAttributeConverter[] converters)
	{
		this.converters = converters;
		this.generator = createGenerator(converters.length);
	}

	public Object[] generate(final Message message)
	{
		return this.generator.apply(message);
	}

	private Function<Message, Object[]> createGenerator(final int argumentCount)
	{
		return switch (argumentCount)
		{
			case 1 -> this::generate1;
			case 2 -> this::generate2;
			case 3 -> this::generate3;
			case 4 -> this::generate4;
			default -> this::generateAll;
		};
	}

	private Object[] generate1(final Message message)
	{
		return new Object[]{ this.converters[0].convert(message) };
	}

	private Object[] generate2(final Message message)
	{
		return new Object[]{ this.converters[0].convert(message), this.converters[1].convert(message) };
	}

	private Object[] generate3(final Message message)
	{
		return new Object[]{ this.converters[0].convert(message), this.converters[1].convert(message),
				this.converters[2].convert(message) };
	}

	private Object[] generate4(final Message message)
	{
		return new Object[]{ this.converters[0].convert(message), this.converters[1].convert(message),
				this.converters[2].convert(message), this.converters[3].convert(message) };
	}

	private Object[] generateAll(final Message message)
	{
		final Object[] objects = new Object[this.converters.length];

		for (int i = 0, length = objects.length; i < length; i++)
		{
			objects[i] = this.converters[i].convert(message);
		}

		return objects;
	}
}
