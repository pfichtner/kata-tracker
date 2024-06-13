package com.github.pfichtner.samman.kata.redgreentracker.consumer;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.github.pfichtner.samman.kata.chart.Period;
import com.github.pfichtner.samman.kata.redgreentracker.RedGreenResult;
import com.github.pfichtner.samman.kata.redgreentracker.reporter.FileReporter;

class FileResultConsumerTest {

	LocalDateTime localDateTime = LocalDateTime.of(2024, 06, 13, 21, 35, 00);
	long epochSecs = localDateTime.toEpochSecond(UTC);

	@AfterEach
	void tearDown() throws IOException {
		Files.walk(FileReporter.getOrCreateDirectory()) //
				.sorted(Comparator.reverseOrder()) //
				.map(Path::toFile) //
				.forEach(File::delete) //
		;
	}

	@Test
	void consume() throws Exception {
		List<Period> periods = new ArrayList<>();
		try (FileResultConsumer sut = new FileResultConsumer(); FileReporter reporter = new FileReporter()) {
			sut.setListener(periods::add);
			RedGreenResult reported = new RedGreenResult(localDateTime).hasFailures(true)
					.displayName("someDisplayName");
			reporter.report(reported);
			assertTrue(awaitUntilNotEmpty(periods));
			assertEquals(1, periods.size());
			Period period = periods.get(0);
			assertEquals(epochSecs, period.getStartTimestamp());
			assertEquals("red", period.getColor());
		}
	}

	private boolean awaitUntilNotEmpty(List<Period> periods) throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			if (!periods.isEmpty()) {
				return true;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}
		return false;
	}

}
