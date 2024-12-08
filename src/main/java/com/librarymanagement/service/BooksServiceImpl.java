package com.librarymanagement.service;

import com.librarymanagement.exception.BooksNotFoundException;
import com.librarymanagement.exception.NoCopiesAvailableException;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BooksList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BooksServiceImpl implements BooksService {

    ConcurrentHashMap<String, Book> booksMap = new ConcurrentHashMap<>();

    public BooksServiceImpl() {
        //Loading this temporarily.
        booksMap = loadBooksData();
    }

    @Override
    public Book findBookByISBN(String isbn) {
        Optional<Book> book = Optional.ofNullable(booksMap.get(isbn));
        return book.orElseThrow(() -> new BooksNotFoundException("Book (" +isbn+ ") Not found"));
    }

    @Override
    public BooksList findBookByAuthor(String author) {
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

    @Override
    public void addNewBook(Book book) {
        if(booksMap.containsKey(book.getIsbn())) {
            throw new BooksNotFoundException("Duplicate ISBN, Try with another ISBN no. ");
        } else {
            booksMap.put(book.getIsbn(), book);
        }
    }

    @Override
    public void removeBook(String isbn) {
        booksMap.remove(isbn);
    }

    @Override
    public synchronized boolean borrowBook(String isbn) {
        Optional<Book> book = Optional.ofNullable(booksMap.get(isbn));
        return book.filter(bk -> bk.getAvailableCopies() > 0)
            .map(bk ->  {
                bk.setAvailableCopies(bk.getAvailableCopies() - 1);
                return true;
            }).orElseGet(() -> {
                throw new NoCopiesAvailableException("No available copies for book with ISBN " + isbn + ".");
            });
    }

    @Override
    public synchronized boolean returnBook(String isbn) {
        Optional<Book> book = Optional.ofNullable(booksMap.get(isbn));
        return book.map(bk -> {
                bk.setAvailableCopies(bk.getAvailableCopies() + 1);
                return true;
            })
            .orElse(false);
    }

    /**
     * This is static data for initial testing
     * @return
     */
    public ConcurrentHashMap<String, Book> loadBooksData() {
        Book book1 = new Book("AA111", "Arrival","Summer", 1995, 15);
        booksMap.put("AA111", book1);

        Book book2 = new Book("BB222", "Arrival", "Winter", 2000,10);
        booksMap.put("BB222", book2);

        Book book3 = new Book("CC333","Parkinson","Season",  2010, 20);
        booksMap.put("CC333", book3);

        return booksMap;
    }
}
