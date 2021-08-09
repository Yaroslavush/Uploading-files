package com.example.uploadingfiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.example.uploadingfiles.parser.ParsingProperties;
import com.example.uploadingfiles.parser.ParsingService;
import com.example.uploadingfiles.service.Neo4jServiceImpl;

@SpringBootApplication
@EnableConfigurationProperties(ParsingProperties.class)
public class UploadingFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadingFilesApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ParsingService parsingService) {
		return (args) -> {
			parsingService.deleteAll();
		    parsingService.init();
		};
	}
}

