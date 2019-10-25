package com.nireworks.teabox.commands;

import com.nireworks.teabox.service.AudioService;
import com.nireworks.teabox.utility.IO;
import java.io.IOException;
import java.util.List;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class AudioCommand {

  private AudioService service;

  private IO io;

  @Autowired
  public void setIo(IO io) {
    this.io = io;
  }

  @Autowired
  public void setService(AudioService service) {
    this.service = service;
  }

  @ShellMethod("Play a specified audio file")
  public String play(@ShellOption String filename) {
    try {
      service.play(filename);
    } catch (IOException e) {
      return "Some IO error occurred: " + e.getMessage();
    } catch (UnsupportedAudioFileException e) {
      return "Audio file type not supported: " + e.getMessage();
    } catch (LineUnavailableException e) {
      return "No available outputs to play the file on: " + e.getMessage();
    }

    return "playing " + filename;
  }

  @ShellMethod("Start a recording")
  public String record(@ShellOption({"-f", "--to-file"}) String filename) {
    try {
      service.record(filename);
    } catch (LineUnavailableException e) {
      return "Failed recording: " + e.getMessage();
    }

    return "Started recording. Stop with stop.";
  }

  @ShellMethod("Stop recording")
  public String stop() {
    service.finish();

    return "If recording was happening, not anymore";
  }

  @ShellMethod("List recordings")
  public String list() {
    List<String> recordings;
    try {
      recordings = io.listWavFiles();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    StringBuilder sb = new StringBuilder(recordings.size() * 3 + 1);

    sb.append("Known records:\n");

    recordings.forEach(r -> {
      sb.append("> ");
      sb.append(r);
      sb.append("\n");
    });

    return sb.toString();
  }
}
