package org.counterdisplay;

import static com.github.pfichtner.samman.kata.io.Sockets.portIsAvailable;
import static com.github.pfichtner.samman.kata.swing.Windows.centerWindow;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.github.pfichtner.samman.kata.chart.Period;
import com.github.pfichtner.samman.kata.io.Closeables;
import com.github.pfichtner.samman.kata.mqtt.MqttBroker;
import com.github.pfichtner.samman.kata.mqtt.MqttConnection;

public class CounterDisplay extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Color forestGreen = Color.decode("#228B22");
	private static final Color ghostWhite = Color.decode("#F8F8FF");
	private static final Color red = Color.decode("#FF0000");

	private int sucessCount;
	private int failureCount;

	public CounterDisplay() {
		setPreferredSize(new Dimension(200, 100));
	}

	public void update(Period period) {
		try {
			if ("red".equalsIgnoreCase(period.getColor())) {
				increaseFailureCount();
			} else if ("green".equalsIgnoreCase(period.getColor())) {
				increaseSucessCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void increaseSucessCount() throws Exception {
		this.sucessCount++;
		playAndRepaint("/pass.wav");
	}

	private void increaseFailureCount() throws Exception {
		this.failureCount++;
		playAndRepaint("/fail.wav");
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(forestGreen);
		g.fillRect(0, 0, 100, 100);
		g.setColor(red);
		g.fillRect(101, 0, 100, 100);
		g.setColor(ghostWhite);
		g.setFont(new Font("Serif", 1, 80));
		int dx = getWidth() / 4;
		drawCenteredString(g, String.valueOf(sucessCount), dx, getHeight() / 2);
		drawCenteredString(g, String.valueOf(failureCount), dx * 3, getHeight() / 2);
	}

	private static void drawCenteredString(Graphics g, String text, int x, int y) {
		FontMetrics metrics = g.getFontMetrics();
		int width = metrics.stringWidth(text);
		x -= width / 2;
		int height = metrics.getHeight();
		y -= height / 2;
		y += metrics.getAscent();
		g.drawString(text, x, y);
	}

	private void playAndRepaint(String name) throws Exception {
		Executors.newCachedThreadPool().execute(() -> {
			try (SoundPlayer soundPlayer = new SoundPlayer(getClass().getResource(name))) {
				soundPlayer.onPlaybackStarted(() -> SwingUtilities.invokeLater(this::repaint));
				soundPlayer.playSound();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		String hostname = "localhost";
		int port = 1883;
		MqttBroker mqttBroker = portIsAvailable(port) ? MqttBroker.builder().host(hostname).port(port).startBroker()
				: null;
		MqttConnection mqttConnection = new MqttConnection(hostname, port);
		invokeLater(() -> {
			JFrame frame = new JFrame("Testing Frame");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setAlwaysOnTop(true);
			frame.setResizable(false);
			CounterDisplay counterDisplay = new CounterDisplay();
			mqttConnection.setListener(counterDisplay::update);

			frame.getContentPane().add(counterDisplay);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					try {
						Stream.of(mqttConnection, mqttBroker).forEach(Closeables::closeQuiety);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});

			frame.pack();
			centerWindow(frame);
			frame.setVisible(true);
		});

	}

}
