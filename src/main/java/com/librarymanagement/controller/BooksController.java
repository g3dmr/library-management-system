package com.librarymanagement.controller;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.BooksList;
import com.librarymanagement.service.BooksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *  This class handles library book operations, supporting the following HTTP requests
 *  GET requests - Find and return a book by ISBN or author name
 *  POST - Add a new book to the library
 *  PUT - Borrow a book and Return a borrowed book
 *  DELETE - Remove a book from the library
 *
 *  Supports in memory caching
 *
 */
@RestController
@RequestMapping("/api/v1/books")
public class BooksController {

    private static final Logger logger = LoggerFactory.getLogger(BooksController.class);

    @Autowired
    public BooksService booksService;

    @Cacheable(value="book", key="#isbn")
    @GetMapping("/find/isbn/{isbn}")
    public ResponseEntity<Book> findBookByISBN(@PathVariable String isbn) {
        logger.info("Call to backend to retrieve book information");
        Book book = booksService.findBookByISBN(isbn);
        return ResponseEntity.ok(book);
    }

    @Cacheable(value="book", key="#authorName")
    @GetMapping("find/author/{authorName}")
    public ResponseEntity<BooksList> findBooksByAuthor(@PathVariable String authorName) {
        BooksList bookList = booksService.findBookByAuthor(authorName);
        return ResponseEntity.ok(bookList);
    }

    @CachePut(value="book", key="#book.isbn")
    @PostMapping("/newbook")
    public ResponseEntity<String> addNewBook(@RequestBody Book book) {
        booksService.addNewBook(book);
        return ResponseEntity.ok("Book Added Successfully");
    }

    @CacheEvict(value="book", key="#isbn")
    @DeleteMapping("/delete/{isbn}")
    public ResponseEntity<String> removeBook(@PathVariable String isbn) {
        booksService.removeBook(isbn);
        return ResponseEntity.ok("Book Removed Successfully");
    }

    @CacheEvict(value="book", key="#isbn")
    @PutMapping("/borrow/{isbn}")
    public ResponseEntity<String> borrowBook(@PathVariable String isbn) {
        boolean borrowedBook = booksService.borrowBook(isbn);
        return ResponseEntity.ok("Book borrowed successfully");
    }

    @CacheEvict(value="book", key="#isbn")
    @PutMapping("/return/{isbn}")
    public ResponseEntity<String> returnBook(@PathVariable String isbn) {
        boolean returnBook = booksService.returnBook(isbn);
        return ResponseEntity.ok("Book returned successfully");
    }

    @CacheEvict(value="book", allEntries = true)
    @GetMapping("/clearcache")
    public String clearCache() {
        return "Cache Cleared.";
    }

}
