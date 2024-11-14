package com.pjsky.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneCapture {

    float sampleRate = 16000; //采样率， 8000,11025,16000,22050,44100
    int sampleSizeInBits = 16; //采样位数， 8,16
    int channels = 1; //声道数， 1-单声道,2-双声道

    CountDownLatch latch = new CountDownLatch(1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 采集到的音频数据暂时保存在这个流中

    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    boolean flag = true; // 是否开始录音
    int MAX_DOWN_SUM = 50; // 连续无声音自动退出最大次数

    MicrophoneCapture() {
        sampleRate = 16000; 
        sampleSizeInBits = 16; 
        channels = 1; 
    }

    /**
     * 
     * @param sampleRate 采样率，可选值：8000, 16000, 44100
     * @param sampleSizeInBits 采样位数，可选值：8, 16
     * @param channels 声道数，可选值：1, 2
     */
    MicrophoneCapture(float sampleRate, int sampleSizeInBits, int channels) {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
    }

    public AudioFormat getAudioFormat() {
        boolean signed = true; // true,false
        boolean bigEndian = false; // true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void stopCapture() {
        flag = false;
        targetDataLine.stop();
        targetDataLine.close();
    }

    public void startCapture() {
        try {
            // 获得指定的音频格式
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

            flag = true;
            new CaptureThread().start();
            System.out.println("开始录音，请说话");
            latch.await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CaptureThread extends Thread {
        public void run() {
            // 声音录入的最低权重
            int weight = 2;
            // 判断是否停止的计数器
            int downSum = 0;

            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                byte[] fragment = new byte[1024];

                while (flag) {

                    targetDataLine.read(fragment, 0, fragment.length);
                    // 当数组末位大于weight时开始存储字节（有声音传入），一旦开始不再需要判断末位
                    if (Math.abs(fragment[fragment.length - 1]) > weight || baos.size() > 0) {
                        baos.write(fragment);
                        System.out.println("首字节: " + fragment[0] + ", 末尾字节: " + fragment[fragment.length - 1]
                                + ", fragment.length: " + fragment.length);
                        // 判断语音是否停止
                        if (Math.abs(fragment[fragment.length - 1]) <= weight) {
                            downSum++;
                        } else {
                            System.out.println("重置停止计数器，继续录音");
                            downSum = 0;
                        }
                        // 计数超过 MAX_DOWN_SUM 说明此段时间没有声音传入
                        if (downSum > MAX_DOWN_SUM) {
                            System.out.println("无声音输入超过一定时间，自动停止录音");
                            flag = false;
                            break;
                        }

                    } else {
                        System.out.println("已经开始录音，请说话");
                        Thread.sleep(100);
                    }
                }

                // 停止录音
                stopCapture();

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

        }
    }

    public void saveAudioFile() {
        String filePath = "./voice_capture.wav";
        File audioFile = new File(filePath);
        ByteArrayInputStream bais = null;
        AudioInputStream ais = null;
        try {
            System.out.println("生成录音文件-开始");
            // 保存录音文件时使用相同的音频格式
            audioFormat = getAudioFormat();
            byte audioData[] = baos.toByteArray();
            bais = new ByteArrayInputStream(audioData);
            ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
            System.out.println("生成录音文件-结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (ais != null) {
                    ais.close();
                }
                if (bais != null) {
                    bais.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 
     * @return AudioInputStream 音频输入流，调用方用完需要关闭它
     */
    public AudioInputStream getAudioInputStream() {
        AudioInputStream ais = null;
        byte audioData[] = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
    
        audioFormat = getAudioFormat();
        ais = new AudioInputStream(bais,audioFormat, audioData.length / audioFormat.getFrameSize());

        try {
            if (baos != null) {
                baos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ais;
    }

    public byte[] getAudioData() {
        byte[] data = baos.toByteArray();

        try {
            if (baos != null) {
                baos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void main(String args[]) {
        MicrophoneCapture microphoneCapture = new MicrophoneCapture();

        microphoneCapture.startCapture();
        microphoneCapture.saveAudioFile();
        System.out.println("录音结束，退出");
    }
}
