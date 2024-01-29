package config;

import java.util.List;

import dev.all_things.boot.nats.annotation.EnableNats;
import dev.all_things.boot.nats.configuration.NatsConnectionConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableNats
@Configuration(proxyBeanMethods = false)
public class NatsConfiguration
{
	@Bean
	@ConditionalOnMissingBean
	public NatsConnectionConfiguration natsConnectionConfiguration()
	{
		final NatsConnectionConfiguration configuration = new NatsConnectionConfiguration();

		configuration.setServers(List.of("nats://0.0.0.0:58811"));
		configuration.setUsername("local");
		configuration.setPassword("dHrBIABqWsj2ydYsLrqPkE5e47qISqyu");

		return configuration;
	}
}
