package com.github.pfichtner.samman.kata.chart;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Timeline {

	private final List<Period> periods = new CopyOnWriteArrayList<>();
	private final List<Period> view = unmodifiableList(periods);

	public List<Period> getPeriods() {
		return view;
	}

	public void update(Period period) {
		if (tryUpdateLastPeriodWith(period)) {
			periods.add(period);
		}
	}

	private boolean tryUpdateLastPeriodWith(Period next) {
		if (periods.isEmpty()) {
			return true;
		}
		Period lastPeriod = periods.get(periods.size() - 1);
		lastPeriod.setEndTimestamp(next.getStartTimestamp());
		return !colorsAreEqual(lastPeriod, next);
	}

	private boolean colorsAreEqual(Period period1, Period period2) {
		return period1.getColor().equals(period2.getColor());
	}

}