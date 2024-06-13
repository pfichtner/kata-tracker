package com.github.pfichtner.samman.kata.redgreentracker.consumer;

import com.github.pfichtner.samman.kata.chart.Period;

public interface ResultConsumer extends AutoCloseable {

	public static interface Listener {
		Listener NULL = nextPeriod -> {
			// noop
		};

		void received(Period nextPeriod);
	}

	void setListener(Listener listener);

}
