package com.github.pfichtner.samman.kata.redgreentracker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RedGreenResult {

	private final LocalDateTime timestamp;
	private String displayName;
	private boolean failures;

	public LocalDateTime timestamp() {
		return timestamp;
	}

	public RedGreenResult(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public RedGreenResult displayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public boolean hasFailures() {
		return failures;
	}

	public RedGreenResult hasFailures(boolean failures) {
		this.failures = failures;
		return this;
	}

	@Override
	public String toString() {
		return timestamp.toEpochSecond(ZoneOffset.UTC) + "," + displayName + "," + (failures ? "red" : "green");
	}

}
