package com.github.pfichtner.samman.kata.redgreentracker.reporter;

public final class ResultReporters {

	private ResultReporters() {
		super();
	}

	public static Reporter defaultReporter() throws Exception {
		return new MqttReporter();
	}

}
