package com.librarymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 *  Library Management System for managing the books
 */

@SpringBootApplication
@EnableCaching
public class LibrarymanagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(LibrarymanagementApplication.class, args);
	}

}
