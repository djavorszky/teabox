package com.nireworks.teabox.utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IO {

  @Value("${teabox.recording.targetFolder}")
  private String recordingTargetFolder;

  public File loadWavFile(String recordingName) throws IOException {
    Path path = Paths.get(recordingTargetFolder, recordingName + ".wav");

    if (!Files.exists(path)) {
      throw new IOException("Recording does not exist: " + recordingName);
    }

    return path.toFile();
  }

  public File createWavFile(String recordingName) throws IOException {
    Path path = Paths.get(recordingTargetFolder, recordingName + ".wav");

    if (Files.exists(path)) {
      throw new IOException("Recording already exists: " + recordingName);
    }

    return Files.createFile(path).toFile();
  }

  public File loadFile(String filename) throws IOException {
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

  public void ensureFolderExists(String folderName) throws IOException {
    Path folder = Paths.get(folderName);

    if (Files.isRegularFile(folder)) {
      throw new IOException("Link already exists as a regular file: " + folderName);
    }

    if (!Files.exists(folder)) {
      Files.createDirectory(folder);
    }
  }

  public List<String> listWavFiles() throws IOException {
    Path folder = Paths.get(recordingTargetFolder);

    if (!Files.exists(folder)) {
      throw new IOException("Recording folder does not exist: " + recordingTargetFolder);
    }

    return Files.list(folder).map(path -> {
      String name = path.toFile().getName();

      return name.substring(0, name.lastIndexOf("."));
    }).collect(Collectors.toList());
  }


}
