package com.pjsky.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioDeviceInfo {

    public static void main(String[] args) {

        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);

            // 麦克风
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo.getLineClass().equals(TargetDataLine.class)) {
                    // 音频输入设备（麦克风）
                    System.out.println("麦克风-TargetDataLine, Mixer: " + info.getName());
                    System.out.println("麦克风-TargetDataLine, Line Info: " + lineInfo);
                }
            }

            // 音频输出设备
            lineInfos = mixer.getSourceLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo.getLineClass().equals(SourceDataLine.class)) {
                    // 音频输出设备（扬声器）
                    System.out.println("扬声器-SourceDataLine, Mixer: " + info.getName());
                    System.out.println("扬声器-SourceDataLine, Line Info: " + lineInfo);
                }
            }

        }
    }
}
