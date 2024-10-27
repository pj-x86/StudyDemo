package com.pjsky.audio;

import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.Test;

class AudioTest {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) {

		try {
			// 录音
			MicrophoneCapture microphoneCapture = new MicrophoneCapture();
			microphoneCapture.startCapture();
			AudioInputStream audioInputStream = microphoneCapture.getAudioInputStream();

			// 播放刚才的录音
			AudioPlayer audioPlayer = new AudioPlayer();
			audioPlayer.play(audioInputStream);

			if (audioInputStream != null) {
				audioInputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
