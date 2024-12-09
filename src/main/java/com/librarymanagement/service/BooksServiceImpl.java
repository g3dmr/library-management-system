package com.librarymanagement.service;

import com.librarymanagement.exception.BooksNotFoundException;
import com.librarymanagement.exception.NoCopiesAvailableException;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BooksList;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *    - addNewBook(Book book): Adds a new book to the library
 *    - removeBook(String isbn): Removes a book from the library by ISBN
 *    - findBookByISBN(String isbn): Returns a book by its ISBN
 *    - findBooksByAuthor(String author): Returns a list of books by a given author
 *    - borrowBook(String isbn): Decreases the available copies of a book by 1
 *    - returnBook(String isbn): Increases the available copies of a book by 1
 */

@Service
public class BooksServiceImpl implements BooksService {

    private static final Logger logger = LoggerFactory.getLogger(BooksServiceImpl.class);

    ConcurrentHashMap<String, Book> booksMap = new ConcurrentHashMap<>();

    public BooksServiceImpl() {
        //Loading this temporarily.
        loadBooksData();
    }

    @Override
    public Book findBookByISBN(String isbn) {
        logger.debug("Finding book ISBN :"+isbn);
        Optional<Book> book = Optional.ofNullable(booksMap.get(isbn));
        return book.orElseThrow(() -> new BooksNotFoundException("Book (" +isbn+ ") Not found"));
    }

    /**
     * Find the book from given author name
     * @param author
     * @return
     */
    @Override
    public BooksList findBookByAuthor(String author) {
        if(null == author || StringUtils.isBlank(author)) {
            throw new BooksNotFoundException("Author name is Empty or null.");
        }
        logger.debug("Finding book by author :"+author);
        List<Book> bookList = booksMap.values().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toUnmodifiableList());
        if(bookList.isEmpty()) {
            throw new BooksNotFoundException("Book Not found for given author");
        }
        BooksList books = new BooksList();
        books.setBookList(bookList);
        return books;
    }

    /**
     * Add a new book, ISBN should be unique, if already exists in the library
     * return the exception
     * @param book
     */
    @Override
    public void addNewBook(Book book) {
        if(book == null) {
            throw new BooksNotFoundException("Not a valid book details.");
        }
        validateISBN(book.getIsbn());
        if(booksMap.containsKey(book.getIsbn())) {
            throw new BooksNotFoundException("Duplicate ISBN, Try with another ISBN no. ");
        } else {
            booksMap.put(book.getIsbn(), book);
        }
    }

    @Override
    public void removeBook(String isbn) {
        validateISBN(isbn);
        if(booksMap.containsKey(isbn)) {
            booksMap.remove(isbn);
        } else {
            throw new BooksNotFoundException("ISBN is not found in the library.");
        }
    }

    /**
     * Borrow a book which reduced the number of available copies by 1
     * @param isbn
     * @return
     */
    @Override
    public synchronized boolean borrowBook(String isbn) {
        validateISBN(isbn);
        Optional<Book> book = Optional.ofNullable(booksMap.get(isbn));
        return book.filter(bk -> bk.getAvailableCopies() > 0)
            .map(bk ->  {
                bk.setAvailableCopies(bk.getAvailableCopies() - 1);
                return true;
            }).orElseGet(() -> {
                throw new NoCopiesAvailableException("No available copies for book with ISBN " + isbn + ".");
            });
    }

    public void validateISBN(String isbn) {
        if( isbn == null || StringUtils.isBlank(isbn)) {
            throw new BooksNotFoundException("Not a valid ISBN number.");
        }
    }

    /**
     * Returning a borrowed book which increases the number of available copies by 1
     * @param isbn
     * @return boolean
     */
    @Override
    public synchronized boolean returnBook(String isbn) {
        validateISBN(isbn);
        Optional<Book> book = Optional.ofNullable(booksMap.get(isbn));
        return book.map(bk -> {
                bk.setAvailableCopies(bk.getAvailableCopies() + 1);
                return true;
            })
            .orElse(false);
    }

    /**
     * This is static data for initial testing purpose only
     * @return
     */
    public void loadBooksData() {
        Book book1 = new Book("AA111", "Arrival","Summer", 1995, 15);
        booksMap.put("AA111", book1);

        Book book2 = new Book("BB222", "Arrival", "Winter", 2000,10);
        booksMap.put("BB222", book2);

        Book book3 = new Book("CC333","Parkinson","Season",  2010, 20);
        booksMap.put("CC333", book3);
    }
}
