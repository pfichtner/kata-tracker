package org.counterdisplay;

import static org.approvaltests.awt.AwtApprovals.verify;

import org.junit.jupiter.api.Test;

import com.github.pfichtner.samman.kata.chart.Period;

class CounterDisplayTest {

	@Test
	void empty() {
		verify(new CounterDisplay(null, null));
	}

	@Test
	void oneToTwo() {
		CounterDisplay sut = new CounterDisplay(null, null);
		sut.update(new Period(0, "red"));
		sut.update(new Period(0, "green"));
		sut.update(new Period(0, "red"));
		verify(sut);
	}

}
