package com.vmandre.batchprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImportDataBatchApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(
				SpringApplication.run(ImportDataBatchApplication.class, args)));
	}

}
