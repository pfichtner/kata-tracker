package com.github.pfichtner.samman.kata.mqtt;

import static io.moquette.broker.config.IConfig.ENABLE_TELEMETRY_NAME;
import static io.moquette.broker.config.IConfig.HOST_PROPERTY_NAME;
import static io.moquette.broker.config.IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME;
import static io.moquette.broker.config.IConfig.PORT_PROPERTY_NAME;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;

public class MqttBroker implements AutoCloseable {

	public static class Builder {

		private final Properties properties = new Properties();
		private String host = "localhost";
		private Integer port;

		public Builder host(String host) {
			this.host = host;
			return this;
		}

		public Builder port(Integer port) {
			this.port = port;
			return this;
		}

		public MqttBroker startBroker() {
			return new MqttBroker(this);
		}

		public Properties properties() {
			properties.put(HOST_PROPERTY_NAME, host);
			properties.put(PORT_PROPERTY_NAME, String.valueOf(port == null ? 1883 : port));
			properties.put(PERSISTENCE_ENABLED_PROPERTY_NAME, Boolean.FALSE.toString());
			properties.put(ENABLE_TELEMETRY_NAME, Boolean.FALSE.toString());
			return properties;
		}

	}

	private final Server broker;
	private final IConfig config;

	public static Builder builder() {
		return new Builder();
	}

	public MqttBroker(Builder builder) {
		config = new MemoryConfig(builder.properties());
		broker = startBroker(new Server(), config);
	}

	private Server startBroker(Server server, IConfig memoryConfig) {
		try {
			server.startServer(memoryConfig);
			return server;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getHost() {
		return config.getProperty(HOST_PROPERTY_NAME);
	}

	public int getPort() {
		return Integer.parseInt(config.getProperty(PORT_PROPERTY_NAME));
	}

	@Override
	public void close() {
		broker.stopServer();
	}

	public static void main(String[] args) throws InterruptedException {
		String hostname = "localhost";
		int port = 1883;
		try (MqttBroker mqttBroker = MqttBroker.builder().host(hostname).port(port).startBroker()) {
			System.out.println("Hit Ctrl+C to stop");
			new CountDownLatch(1).await();
		}
	}

}
