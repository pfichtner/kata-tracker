package org.counterdisplay;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioSystem.getLine;
import static javax.sound.sampled.LineEvent.Type.START;
import static javax.sound.sampled.LineEvent.Type.STOP;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer implements AutoCloseable {

	private final AudioInputStream audioStream;
	private Runnable playbackStartedListener = () -> {
	};

	public SoundPlayer(File soundFile) throws UnsupportedAudioFileException, IOException {
		audioStream = getAudioInputStream(soundFile);
	}

	public SoundPlayer(URL soundFile) throws UnsupportedAudioFileException, IOException {
		audioStream = getAudioInputStream(soundFile);
	}

	public SoundPlayer(InputStream soundFile) throws UnsupportedAudioFileException, IOException {
		audioStream = getAudioInputStream(soundFile);
	}

	@Override
	public void close() throws Exception {
		audioStream.close();
	}

	public void onPlaybackStarted(Runnable playbackStartedListener) {
		this.playbackStartedListener = playbackStartedListener;
	}

	public void playSound() throws LineUnavailableException, IOException, InterruptedException {
		Clip clip = (Clip) getLine(new DataLine.Info(Clip.class, audioStream.getFormat()));
		try {
			CountDownLatch playbackCompletedLatch = new CountDownLatch(1);
			clip.addLineListener(e -> {
				if (e.getType() == START) {
					playbackStartedListener.run();
				} else if (e.getType() == STOP) {
					playbackCompletedLatch.countDown();
				}
			});
			clip.open(audioStream);
			clip.start();
			playbackCompletedLatch.await();
		} finally {
			clip.close();
		}
	}

}
