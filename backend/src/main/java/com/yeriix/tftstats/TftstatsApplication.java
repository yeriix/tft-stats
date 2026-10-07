package com.yeriix.tftstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan 
public class TftstatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TftstatsApplication.class, args);
	}

}
