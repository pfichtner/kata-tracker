package com.github.pfichtner.samman.kata.chart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class TimelineTest {

	Timeline timeline = new Timeline();

	@Test
	void testAddPeriodToEmptyTimeline() {
		Period period = new Period(1, "red");
		timeline.update(period);
		List<Period> periods = timeline.getPeriods();
		assertEquals(1, periods.size());
		assertEquals(period, periods.get(0));
	}

	@Test
	void testAddPeriodWithSameColor() {
		Period firstPeriod = new Period(1, "red");
		Period secondPeriod = new Period(2, "red");
		timeline.update(firstPeriod);
		timeline.update(secondPeriod);
		List<Period> periods = timeline.getPeriods();
		assertEquals(1, periods.size());
		assertEquals(1L, periods.get(0).getStartTimestamp());
		assertEquals(2L, periods.get(0).getEndTimestamp());
	}

	@Test
	void testAddPeriodWithDifferentColor() {
		Period firstPeriod = new Period(1, "red");
		Period secondPeriod = new Period(2, "green");
		timeline.update(firstPeriod);
		timeline.update(secondPeriod);
		List<Period> periods = timeline.getPeriods();
		assertEquals(2, periods.size());
		assertEquals(1L, periods.get(0).getStartTimestamp());
		assertEquals(2L, periods.get(0).getEndTimestamp());
		assertEquals(2L, periods.get(1).getStartTimestamp());
	}

	@Test
	void testGetPeriodsReturnsUnmodifiableList() {
		Period period = new Period(1, "red");
		timeline.update(period);
		List<Period> periods = timeline.getPeriods();
		assertThrows(UnsupportedOperationException.class, () -> periods.add(new Period(2, "green")));
	}

}
