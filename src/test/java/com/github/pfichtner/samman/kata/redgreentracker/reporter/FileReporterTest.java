package com.github.pfichtner.samman.kata.redgreentracker.reporter;

import static java.lang.System.getProperty;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.samman.kata.redgreentracker.RedGreenResult;

class FileReporterTest {

	LocalDateTime localDateTime = LocalDateTime.of(2024, 06, 13, 21, 35, 00);
	long epochSecs = localDateTime.toEpochSecond(UTC);

	@Test
	void green() throws Exception {
		Path file = Path.of(getProperty("java.io.tmpdir"), "redgreen").resolve(epochSecs + "-green" + ".txt");
		extracted(new RedGreenResult(localDateTime).hasFailures(false).displayName("someDisplayName"), file);
	}

	@Test
	void red() throws Exception {
		Path file = Path.of(getProperty("java.io.tmpdir"), "redgreen").resolve(epochSecs + "-red" + ".txt");
		extracted(new RedGreenResult(localDateTime).hasFailures(true).displayName("someDisplayName"), file);
	}

	private void extracted(RedGreenResult result, Path file) throws Exception, IOException {
		try (FileReporter sut = new FileReporter()) {
			sut.report(result);
			try {
				assertEquals("someDisplayName", Files.readString(file));
				assertTrue(exists(file));
			} finally {
				delete(file);
			}
		}
	}

}
