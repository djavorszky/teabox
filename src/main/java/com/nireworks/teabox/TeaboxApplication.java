package com.nireworks.teabox;

import org.jline.utils.AttributedString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class TeaboxApplication {

  @Value("${teabox.prompt}")
  private String prompt;

  public static void main(String[] args) {
    SpringApplication.run(TeaboxApplication.class, args);
  }

  @Bean
  public PromptProvider getPrompt() {
    return () -> new AttributedString(prompt);
  }
}
