package com.librarymanagement.service;

import com.librarymanagement.exception.BooksNotFoundException;
import com.librarymanagement.exception.NoCopiesAvailableException;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BooksList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BooksServiceImplTest {

    @InjectMocks
    BooksServiceImpl service;

    @Mock
    ConcurrentHashMap<String, Book> booksMap;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Book book1 = new Book("AA111", "Arrival","Summer", 1995, 15);
        Book book2 = new Book("BB222", "Arrival", "Winter", 2000,10);
        when(booksMap.get("AA111")).thenReturn(book1);
        when(booksMap.values()).thenReturn(Arrays.asList(book1, book2));
    }

    @Test
    public void testFindBookByISBN() {
        Book book1  = service.findBookByISBN("AA111");
        assertEquals("AA111", book1.getIsbn());
        verify(booksMap, times(1)).get("AA111");
    }

    @Test
    public void testFindBookByAuthor() {
        BooksList bookList  = service.findBookByAuthor("Winter");
        assertEquals(1, bookList.getBookList().size());
        verify(booksMap, times(1)).values();
    }

    @Test
    public void testFindBookByAuthorNotAvailable() {
        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.findBookByAuthor("Winter12");
        });
        assertEquals("Book Not found for given author", exp.getMessage());
    }

    @Test
    public void testFindBookByAuthorForEmptyCollection() {
        when(booksMap.values()).thenReturn(Arrays.asList());
        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.findBookByAuthor("Wintsdfs");
        });
        assertEquals("Book Not found for given author", exp.getMessage());
    }

    @Test
    public void testAddNewBook() {
        Book newBook = new Book("CC123", "New Book", "New Season", 2024,50);
        service.addNewBook(newBook);
        verify(booksMap, times(1)).put("CC123", newBook);
    }

    @Test
    public void testRemoveBook() {
        service.removeBook("AA111");
        verify(booksMap, times(1)).remove("AA111");
    }

    @Test
    public void testBorrowBook(){
        boolean status = service.borrowBook("AA111");
        assertTrue(status);
        Book book = service.findBookByISBN("AA111");
        assertEquals(14, book.getAvailableCopies());
    }

    @Test
    public void testBorrowBookForZeroCopies() {
        Book book = service.findBookByISBN("AA111");
        book.setAvailableCopies(0);
        NoCopiesAvailableException exp = assertThrows(NoCopiesAvailableException.class, () -> {
            service.borrowBook("AA111");
        });
        assertEquals("No available copies for book with ISBN AA111.", exp.getMessage());
    }

    @Test
    public void testReturnBook() {
        boolean status = service.returnBook("AA111");
        assertTrue(status);
        Book book = service.findBookByISBN("AA111");
        assertEquals(16, book.getAvailableCopies());
    }
}