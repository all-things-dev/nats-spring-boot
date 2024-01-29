package config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Provides startup configuration of the Spring Boot application.
 * <p>
 * Extending {@link SpringBootServletInitializer} allows application to be deployed
 * as WAR in servlet containers e.g. Tomcat.
 */
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = { "config", "dev.all_things" }, proxyBeanMethods = false)
public class TestApplication
		extends SpringBootServletInitializer
{
	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder)
	{
		return builder.sources(TestApplication.class);
	}

	/**
	 * Application starting point.
	 *
	 * @param args program arguments.
	 */
	public static void main(final String[] args)
	{
		// Creating spring application
		final SpringApplication application = new SpringApplication(TestApplication.class);

		// Starting the application
		application.run(args);
	}
}
