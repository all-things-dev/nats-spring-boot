package dev.all_things.boot.nats.message.transform;

import java.nio.charset.StandardCharsets;

import io.nats.client.Message;

public interface MessageAttributeConverter
{
	public static final MessageAttributeConverter noOpConverter = message -> message;
	public static final MessageAttributeConverter messageToStringConverter = message -> new String(message.getData(), StandardCharsets.UTF_8);
	public static final MessageAttributeConverter messageToBytesConverter = Message::getData;

	public Object convert(Message message);
}
