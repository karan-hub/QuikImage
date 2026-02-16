package com.image.quickimage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class QuickimageApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuickimageApplication.class, args);
	}

}
