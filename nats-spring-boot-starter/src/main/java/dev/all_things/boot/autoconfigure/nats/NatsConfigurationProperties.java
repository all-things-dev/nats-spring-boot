package dev.all_things.boot.autoconfigure.nats;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.nats")
public class NatsConfigurationProperties
{
	/**
	 * The list of NATS servers to connect to.
	 */
	protected List<String> servers;

	/**
	 * The username for connecting to the NATS server / cluster.
	 */
	protected String username;

	/**
	 * The password for connecting to the NATS server / cluster.
	 */
	protected String password;

	public List<String> getServers()
	{
		return this.servers;
	}

	public void setServers(final List<String> servers)
	{
		this.servers = servers;
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(final String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(final String password)
	{
		this.password = password;
	}
}
