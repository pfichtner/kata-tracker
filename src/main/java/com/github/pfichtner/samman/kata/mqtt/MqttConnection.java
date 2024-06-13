package com.github.pfichtner.samman.kata.mqtt;

import static java.lang.String.format;
import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttConnection implements AutoCloseable {

	private static final String TOPIC = "redgreen/test";

	private MqttClient client;

	public MqttConnection(String host, int port) throws Exception {
		client = new MqttClient(format("tcp://%s:%d", host, port), generateClientId());
		client.connect();
	}

	public MqttConnection setListener(IMqttMessageListener listener) {
		try {
			client.subscribe(TOPIC, listener);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	@Override
	public void close() throws Exception {
		client.disconnect();
		client.close();
	}

}
