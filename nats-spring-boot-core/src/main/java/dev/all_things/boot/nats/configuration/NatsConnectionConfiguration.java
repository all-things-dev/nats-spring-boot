package dev.all_things.boot.nats.configuration;

import java.util.List;

/**
 * NATS client configuration.
 */
public class NatsConnectionConfiguration
{
	/*
	 * The list of NATS servers to connect to.
	 */
	private List<String> servers;

	/*
	 * The username for connecting to the NATS server / cluster.
	 */
	private String username;

	/*
	 * The password for connecting to the NATS server / cluster.
	 */
	private String password;

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
