package com.pjsky.audio;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer {

    private SourceDataLine speaker;

    public void play(String filePath) {
        File file = new File(filePath);
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            this.play(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 播放音频文件输入流
     * @param inputStream 必须是音频文件的输入流，否则不支持
     * @throws Exception
     */
    public void play(InputStream inputStream) throws Exception {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(inputStream)) {
            this.play(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void play(AudioInputStream ais) throws Exception {
        AudioFormat format = ais.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        speaker = (SourceDataLine) AudioSystem.getLine(info);
        speaker.open(format);
        speaker.start();

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = ais.read(buffer)) > 0) {
            speaker.write(buffer, 0, len);
        }

        speaker.drain();
        speaker.close();
    }

    public void play(byte[] data, AudioFormat format) throws Exception {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        speaker = (SourceDataLine) AudioSystem.getLine(info);
        speaker.open(format);
        speaker.start();

        speaker.write(data, 0, data.length);

        speaker.drain();
        speaker.close();
    }

    public void play2(String filePath) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(filePath);
            bis = new BufferedInputStream(fis);

            this.play(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        String filePath = "./voice_capture.wav";
        AudioPlayer player = new AudioPlayer();

        player.play(filePath);
        //player.play2(filePath);

    }

}
