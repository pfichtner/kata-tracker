package com.github.pfichtner.samman.kata.redgreentracker.reporter;

import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.github.pfichtner.samman.kata.redgreentracker.RedGreenResult;

public class MqttReporter implements Reporter {

	private static final String TOPIC = "redgreen/test";

	private final MqttClient mqttClient;

	public MqttReporter() throws Exception {
		mqttClient = new MqttClient("tcp://localhost:1883", generateClientId(), new MemoryPersistence());
		mqttClient.connect();
	}

	@Override
	public void report(RedGreenResult result) throws Exception {
		mqttClient.publish(TOPIC, new MqttMessage(result.toString().getBytes()));
	}

	@Override
	public void close() throws Exception {
		mqttClient.disconnect();
		mqttClient.close();
	}

}
