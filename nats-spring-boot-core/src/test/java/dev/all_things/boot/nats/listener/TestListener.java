package dev.all_things.boot.nats.listener;

import dev.all_things.boot.nats.annotation.NatsListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class TestListener
{
	private static final Logger logger = LogManager.getLogger(TestListener.class);

	@NatsListener(subject = "test")
	public void onMessage(final String message)
	{
		logger.info("Received message: {}", message);
	}
}
