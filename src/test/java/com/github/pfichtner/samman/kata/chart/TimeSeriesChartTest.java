package com.github.pfichtner.samman.kata.chart;

import static org.approvaltests.awt.AwtApprovals.verify;

import org.junit.jupiter.api.Test;

class TimeSeriesChartTest {

	@Test
	void empty() {
		verify(new TimeSeriesChart());
	}

	@Test
	void withData() {
		TimeSeriesChart sut = new TimeSeriesChart();
		int now = 4242;
		sut.update(new Period(now + 0, "red"));
		sut.update(new Period(now + 4, "green"));
		sut.update(new Period(now + 12, "green"));
		sut.update(new Period(now + 18, "green"));
		sut.update(new Period(now + 22, "red"));
		sut.update(new Period(now + 25, "green"));
		sut.update(new Period(now + 31, "green"));
		sut.update(new Period(now + 51, "green"));
		sut.update(new Period(now + 74, "green"));
		verify(sut);
	}

}
