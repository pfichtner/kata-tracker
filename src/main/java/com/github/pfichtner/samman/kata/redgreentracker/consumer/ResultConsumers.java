package com.github.pfichtner.samman.kata.redgreentracker.consumer;

public final class ResultConsumers {

	private ResultConsumers() {
		super();
	}

	public static ResultConsumer defaultConsumer() throws Exception {
		return new MqttResultConsumer();
	}

}
