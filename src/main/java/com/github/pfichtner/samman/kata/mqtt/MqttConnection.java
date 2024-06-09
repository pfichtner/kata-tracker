package com.github.pfichtner.samman.kata.mqtt;

import static java.lang.String.format;
import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

import org.eclipse.paho.client.mqttv3.MqttClient;

import com.github.pfichtner.samman.kata.chart.Period;

public class MqttConnection implements AutoCloseable {

	private static final String TOPIC = "redgreen/test";

	public static interface Listener {
		static final Listener NULL = p -> {
		};

		void received(Period nextPeriod);
	}

	private MqttClient client;
	private Listener listener = Listener.NULL;

	public MqttConnection(String host, int port) throws Exception {
		client = new MqttClient(format("tcp://%s:%d", host, port), generateClientId());
		client.connect();
		client.subscribe(TOPIC, (__, msg) -> {
			String[] data = new String(msg.getPayload()).split(",");
			listener.received(new Period(Integer.parseInt(data[0]), data[2].trim()));
		});
	}

	public MqttConnection setListener(Listener listener) {
		this.listener = listener == null ? Listener.NULL : listener;
		return this;
	}

	@Override
	public void close() throws Exception {
		client.disconnect();
		client.close();
	}

}
