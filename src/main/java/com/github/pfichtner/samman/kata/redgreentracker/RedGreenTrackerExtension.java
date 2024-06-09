package com.github.pfichtner.samman.kata.redgreentracker;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

import java.util.concurrent.CompletableFuture;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class RedGreenTrackerExtension implements AfterAllCallback, TestExecutionExceptionHandler {

	private static final String TOPIC = "redgreen/test";
	private final CompletableFuture<IMqttClient> completableFuture = new CompletableFuture<>();
	private boolean failures;

	public RedGreenTrackerExtension() {
		newSingleThreadExecutor().submit(() -> {
			try {
				var mqttClient = new MqttClient("tcp://localhost:1883", generateClientId(), new MemoryPersistence());
				mqttClient.connect();
				completableFuture.complete(mqttClient);
			} catch (MqttException e) {
				completableFuture.completeExceptionally(e);
			}
		});
	}

	@Override
	public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
		failures = true;
		throw throwable;
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		var result = new RedGreenResult(now()).displayName(context.getDisplayName()).hasFailures(failures);
		// TODO JUnit seems to kill threads, shutdownHooks seems to work (most of the
		// time?)
		Runtime.getRuntime().addShutdownHook(new Thread(() -> sendAndShutdown(result)));
	}

	private void sendAndShutdown(RedGreenResult result) {
		try {
			var mqttClient = completableFuture.get();
			mqttClient.publish(TOPIC, new MqttMessage(result.toString().getBytes()));
			mqttClient.disconnect();
			mqttClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
