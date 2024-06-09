package com.github.pfichtner.samman.kata.chart;

public class Period {

	private String color;
	private int startTimestamp, endTimestamp;

	public Period(int startTimestamp, String color) {
		this.color = color;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = startTimestamp;
	}

	public String getColor() {
		return color;
	}

	public void setEndTimestamp(int endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public int getEndTimestamp() {
		return endTimestamp;
	}

	public int getStartTimestamp() {
		return startTimestamp;
	}

	public int getDuration() {
		return endTimestamp - startTimestamp;
	}

}