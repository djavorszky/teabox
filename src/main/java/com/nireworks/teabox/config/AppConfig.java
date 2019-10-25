package com.nireworks.teabox.config;

import com.nireworks.teabox.utility.IO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sound.sampled.AudioFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class AppConfig {

  @Value("${teabox.audioformat.sampleRate}")
  private float sampleRate;

  @Value("${teabox.audioformat.sampleSizeInBits}")
  private int sampleSizeInBits;

  @Value("${teabox.audioformat.channels}")
  private int channels;

  @Value("${teabox.audioformat.signed}")
  private boolean signed;

  @Value("${teabox.audioformat.bigEndian}")
  private boolean bigEndian;

  @Value("${teabox.recording.targetFolder}")
  private String recordingTargetFolder;

  private IO io;

  @Autowired
  public void setIo(IO io) {
    this.io = io;
  }

  @Bean
  public AudioFormat getAudioFormat() {
    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
  }

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    try {
      io.ensureFolderExists(recordingTargetFolder);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
