package com.github.pfichtner.samman.kata.redgreentracker.consumer;

import static com.github.pfichtner.samman.kata.io.Sockets.portIsAvailable;
import static java.lang.Integer.parseInt;

import java.util.stream.Stream;

import com.github.pfichtner.samman.kata.chart.Period;
import com.github.pfichtner.samman.kata.io.Closeables;
import com.github.pfichtner.samman.kata.mqtt.MqttBroker;
import com.github.pfichtner.samman.kata.mqtt.MqttConnection;

public class MqttResultConsumer implements ResultConsumer {

	private final MqttBroker mqttBroker;
	private final MqttConnection mqttConnection;

	public MqttResultConsumer() throws Exception {
		this("localhost", 1883);
	}

	public MqttResultConsumer(String hostname, int port) throws Exception {
		mqttBroker = portIsAvailable(port) ? MqttBroker.builder().host(hostname).port(port).startBroker() : null;
		mqttConnection = new MqttConnection(hostname, port);
	}

	@Override
	public void setListener(Listener listener) {
		mqttConnection.setListener((__, msg) -> {
			String[] data = new String(msg.getPayload()).split(",");
			listener.received(new Period(parseInt(data[0].trim()), data[2].trim()));
		});

	}

	@Override
	public void close() throws Exception {
		Stream.of(mqttConnection, mqttBroker).forEach(Closeables::closeQuiety);
	}

}
