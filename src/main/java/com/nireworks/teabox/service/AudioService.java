package com.nireworks.teabox.service;

import com.nireworks.teabox.utility.IO;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioService {

  private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

  private boolean recording = false;
  private TargetDataLine line = null;

  private AudioFormat audioFormat;
  private IO io;

  @Autowired
  public void setAudioFormat(AudioFormat audioFormat) {
    this.audioFormat = audioFormat;
  }

  @Autowired
  public void setIo(IO io) {
    this.io = io;
  }

  public void record(String wavName) throws LineUnavailableException {
    if (recording) {
      System.out.println("Already recording");
      return;
    }

    recording = true;

    DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

    line = (TargetDataLine) AudioSystem.getLine(info);
    line.open(audioFormat);
    line.start();

    Thread captureThread = new Thread(() -> {

      try (AudioInputStream ais = new AudioInputStream(line)) {
        File wavFile = io.createWavFile(wavName);

        AudioSystem.write(ais, fileType, wavFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    captureThread.start();
  }

  /**
   * Closes the target data line to finish capturing and recording
   */
  public void finish() {
    if (!recording) {
      return;
    }
    recording = false;

    line.stop();
    line.close();
  }

  public void play(String recordingName)
      throws IOException, UnsupportedAudioFileException, LineUnavailableException {

    if (recording) {
      throw new LineUnavailableException("recording in progress");
    }

    try (AudioInputStream ais =
        AudioSystem.getAudioInputStream(io.loadWavFile(recordingName))) {
      Clip clip = AudioSystem.getClip();

      clip.open(ais);

      clip.start();
    }
  }
}
