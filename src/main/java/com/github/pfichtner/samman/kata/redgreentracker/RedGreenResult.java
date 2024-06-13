package com.github.pfichtner.samman.kata.redgreentracker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

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

	public String displayName() {
		return displayName;
	}

	public boolean hasFailures() {
		return failures;
	}

	public RedGreenResult hasFailures(boolean failures) {
		this.failures = failures;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(displayName, failures, timestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RedGreenResult other = (RedGreenResult) obj;
		return Objects.equals(displayName, other.displayName) && failures == other.failures
				&& Objects.equals(timestamp, other.timestamp);
	}

	@Override
	public String toString() {
		return timestamp.toEpochSecond(ZoneOffset.UTC) + "," + displayName + "," + (failures ? "red" : "green");
	}

}
