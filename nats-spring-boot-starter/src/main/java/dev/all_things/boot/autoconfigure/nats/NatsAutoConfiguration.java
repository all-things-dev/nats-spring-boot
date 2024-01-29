package dev.all_things.boot.autoconfigure.nats;

import dev.all_things.boot.nats.configuration.NatsConnectionConfiguration;
import dev.all_things.boot.nats.connection.NatsConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnBean(NatsConnectionFactory.class)
@EnableConfigurationProperties(NatsConfigurationProperties.class)
public class NatsAutoConfiguration
{
	@Bean
	@ConditionalOnMissingBean
	public NatsConnectionConfiguration natsClientConfiguration(final NatsConfigurationProperties properties)
	{
		final NatsConnectionConfiguration configuration = new NatsConnectionConfiguration();

		configuration.setServers(properties.getServers());
		configuration.setUsername(properties.getUsername());
		configuration.setPassword(properties.getPassword());

		return configuration;
	}
}
