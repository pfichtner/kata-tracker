package org.counterdisplay;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.Test;

class SoundPlayerTest {

	@Test
	void playPass() throws Exception {
		playResource("/pass.wav");
	}

	@Test
	void playFail() throws Exception {
		playResource("/fail.wav");
	}

	@Test
	void doesCallCallback() throws Exception {
		AtomicBoolean callbackCalled = new AtomicBoolean();
		try (SoundPlayer soundPlayer = new SoundPlayer(SoundPlayerTest.class.getResource("/pass.wav"))) {
			soundPlayer.onPlaybackStarted(() -> callbackCalled.set(true));
			soundPlayer.playSound();
		}
		assertTrue(callbackCalled.get());
	}

	private static void playResource(String string) throws LineUnavailableException, IOException, InterruptedException,
			Exception, UnsupportedAudioFileException {
		try (SoundPlayer soundPlayer = new SoundPlayer(SoundPlayerTest.class.getResource(string))) {
			soundPlayer.playSound();
		}
	}

}
