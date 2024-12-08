package com.librarymanagement.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private int availableCopies;

 public Book(String isbn, String title, String author, int publicationYear, int availableCopies) {
    this.isbn = isbn;
    this.title = title;
    this.author = author;
    this.publicationYear = publicationYear;
    this.availableCopies = availableCopies;
 }

 Book() { }
}
