package com.pjsky.audio;

import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;

import org.junit.jupiter.api.Test;

class AudioTest {

	@Test
	void contextLoads() {
	}

	@Test
	void testAudioRaw() {
		try {
			// 录音
			MicrophoneCapture microphoneCapture = new MicrophoneCapture();
			microphoneCapture.startCapture(); // 会自动识别录音结束

			// 播放刚才的录音
			AudioPlayer audioPlayer = new AudioPlayer();
			audioPlayer.play(microphoneCapture.getAudioData(), microphoneCapture.getAudioFormat());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			System.out.println("请选择音频来源：");
			System.out.println("1. 麦克风");
			System.out.println("2. 本地文件");
			System.out.print("请输入数字：");
			Scanner scanner = new Scanner(System.in);
			int choice = Integer.parseInt(scanner.nextLine());

			MicrophoneCapture microphoneCapture = new MicrophoneCapture();
			AudioInputStream audioInputStream;
			AudioPlayer audioPlayer = new AudioPlayer();
			if (choice == 1) {
				// 录音
				microphoneCapture.startCapture();
				audioInputStream = microphoneCapture.getAudioInputStream();

				// 播放刚才的录音
				audioPlayer.play(audioInputStream);

				if (audioInputStream != null) {
					audioInputStream.close();
				}
			} else if (choice == 2) {
				System.out.print("请输入音频文件路径：");
				String filePath = scanner.nextLine();
				audioPlayer.play(filePath);
			}

			scanner.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
