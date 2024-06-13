package com.github.pfichtner.samman.kata.chart;

import static com.github.pfichtner.samman.kata.io.Closeables.closeQuiety;
import static com.github.pfichtner.samman.kata.swing.Windows.centerWindow;
import static java.lang.String.format;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.github.pfichtner.samman.kata.redgreentracker.consumer.MqttResultConsumer;
import com.github.pfichtner.samman.kata.redgreentracker.consumer.ResultConsumer;

public class TimeSeriesChart extends JPanel {

	private static final long serialVersionUID = 1L;

	private final Timeline timeline = new Timeline();

	private final JLabel totalKataTimeLabel = new JLabel();
	private final JLabel totalTimeInRedLabel = new JLabel();
	private final JLabel totalTimeInGreenLabel = new JLabel();
	private final JLabel longestTimeInRedLabel = new JLabel();

	private int totalKataTime;
	private int totalTimeInRed;
	private int totalTimeInGreen;
	private int longestTimeInRed;

	public TimeSeriesChart() {
		updateLabels();
		setPreferredSize(new Dimension(800, 200));
		setLayout(new BorderLayout());

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(totalKataTimeLabel);
		infoPanel.add(totalTimeInGreenLabel);
		infoPanel.add(totalTimeInRedLabel);
		infoPanel.add(longestTimeInRedLabel);

		add(infoPanel, BorderLayout.SOUTH);
	}

	public void update(Period period) {
		SwingUtilities.invokeLater(() -> {
			timeline.update(period);
			updateLabelsAndRepaintGraph();
		});
	}

	private void updateLabelsAndRepaintGraph() {
		updateLabels();
		repaint();
	}

	private void calculateTimes() {
		List<Period> copyOfPeriods = List.copyOf(timeline.getPeriods());
		totalTimeInGreen = colorSum(copyOfPeriods, "green");
		totalTimeInRed = colorSum(copyOfPeriods, "red");
		totalKataTime = totalTimeInGreen + totalTimeInRed;
		longestTimeInRed = durationStreamOfColor(copyOfPeriods, "red").max().orElse(0);
	}

	private static int colorSum(List<Period> periods, String color) {
		return durationStreamOfColor(periods, color).sum();
	}

	private static IntStream durationStreamOfColor(List<Period> periods, String color) {
		return periods.stream().filter(p -> color.equals(p.getColor())).mapToInt(Period::getDuration);
	}

	private void updateLabels() {
		calculateTimes();
		totalKataTimeLabel.setText(format("Total kata time: %d seconds", totalKataTime));
		totalTimeInRedLabel.setText(format("Total time in red: %d seconds", totalTimeInRed));
		totalTimeInGreenLabel.setText(format("Total time in green: %d seconds", totalTimeInGreen));
		longestTimeInRedLabel.setText(format("Longest time in red: %d seconds", longestTimeInRed));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (timeline.getPeriods().isEmpty()) {
			return;
		}

		List<Period> periods = List.copyOf(timeline.getPeriods());
		long totalDuration = periods.get(periods.size() - 1).getEndTimestamp() - periods.get(0).getStartTimestamp();
		drawPeriods(g, totalDuration, periods);
		drawMarkers(g, totalDuration, periods);
	}

	private void drawPeriods(Graphics g, long totalDuration, List<Period> periods) {
		int width = getWidth();
		int x = 0;
		for (Period period : periods) {
			int periodWidth = (int) ((double) period.getDuration() / totalDuration * width);
			g.setColor(period.getColor().equals("red") ? Color.RED : Color.GREEN);
			g.fillRect(x, 20, periodWidth, 50);
			x += periodWidth;
		}
	}

	private void drawMarkers(Graphics g, long totalDuration, List<Period> periods) {
		int width = getWidth();
		int numberOfMarkers = width / 120;
		double markerInterval = ((double) totalDuration) / (numberOfMarkers + 1);

		for (int i = 1; i <= numberOfMarkers; i++) {
			int markerX = (int) ((double) i / (numberOfMarkers + 1) * width);
			g.setColor(Color.BLACK);
			g.drawLine(markerX, 70, markerX, 80);
			int seconds = (int) (i * markerInterval);
			int minutes = seconds / 60;
			int remainingSeconds = seconds % 60;
			g.drawString(format("%02d:%02d", minutes, remainingSeconds), markerX - 10, 95);
		}
	}

	public static void main(String[] args) throws Exception {
		ResultConsumer resultConsumer = new MqttResultConsumer();
		invokeLater(() -> {
			TimeSeriesChart chart = new TimeSeriesChart();
			resultConsumer.setListener(chart::update);
			JFrame frame = new JFrame("Time Series Chart");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setAlwaysOnTop(true);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					closeQuiety(resultConsumer);
				}
			});

			frame.add(chart);
			frame.pack();
			centerWindow(frame);
			frame.setVisible(true);

			// we only repaint on new data
			// new Timer((int) TimeUnit.SECONDS.toMillis(1), __ -> chart.repaint()).start();
		});
	}

}
