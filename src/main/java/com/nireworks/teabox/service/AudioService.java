package com.nireworks.teabox.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.springframework.stereotype.Service;

@Service
public class AudioService {

  private boolean recording = false;
  private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
  private TargetDataLine line = null;
  private AudioInputStream ais = null;


  public void record(String fileName) throws LineUnavailableException, IOException {
    if (recording) {
      System.out.println("Already recording");
      return;
    }

    recording = true;

    try {
      final AudioFormat format = getFormat();
      DataLine.Info info = new DataLine.Info(
          TargetDataLine.class, format);
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start();

      Thread captureThread = new Thread(() -> {
        AudioInputStream ais = new AudioInputStream(line);

        File wavFile = new File(fileName);

        try {
          AudioSystem.write(ais, fileType, wavFile);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      captureThread.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private AudioFormat getFormat() {
    float sampleRate = 16000;
    int sampleSizeInBits = 8;
    int channels = 2;
    boolean signed = true;
    boolean bigEndian = true;
    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
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

    System.out.println("Finished");
  }

  public void play(String filename)
      throws IOException, UnsupportedAudioFileException, LineUnavailableException {

    if (recording) {
      throw new LineUnavailableException("recording in progress");
    }

    try (AudioInputStream ais = AudioSystem.getAudioInputStream(loadFile(filename))) {
      Clip clip = AudioSystem.getClip();

      clip.open(ais);

      clip.start();
    }

  }

  private File loadFile(String filename) throws IOException {
    URL resource = getClass().getClassLoader().getResource(filename);

    if (resource != null) {
      return new File(resource.getFile());
    }

    File file = new File(filename);

    if (!file.exists()) {
      throw new IOException("file does not exist: " + filename);
    }

    return file;
  }

}
