package com.librarymanagement.service;

import com.librarymanagement.exception.BooksNotFoundException;
import com.librarymanagement.exception.NoCopiesAvailableException;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BooksList;
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

    public BooksServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindBookByISBN() {
        Book book1 = new Book("AA111", "Arrival", "Summer", 1995, 15);
        when(booksMap.get("AA111")).thenReturn(book1);

        Book result = service.findBookByISBN("AA111");
        assertEquals("AA111", result.getIsbn());
        verify(booksMap, times(1)).get("AA111");
    }

    @Test
    public void testFindBookByISBNNull() {
        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.findBookByISBN(null);
        });
        assertEquals("Not a valid ISBN number.", exp.getMessage());
    }

    @Test
    public void testFindBookByISBNBlank() {
        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.findBookByISBN("");
        });
        assertEquals("Not a valid ISBN number.", exp.getMessage());
    }

    @Test
    public void testFindBookByAuthor() {
        Book book1 = new Book("AA111", "Arrival", "Summer", 1995, 15);
        Book book2 = new Book("BB222", "Arrival", "Winter", 2000, 10);
        when(booksMap.values()).thenReturn(Arrays.asList(book1, book2));

        BooksList bookList = service.findBookByAuthor("Winter");
        assertEquals(1, bookList.getBookList().size());
        verify(booksMap, times(1)).values();
    }

    @Test
    public void testFindBookByAuthorNull() {
        when(booksMap.values()).thenReturn(Arrays.asList());

        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.findBookByAuthor(null);
        });
        assertEquals("Author name is Empty or null.", exp.getMessage());
    }

    @Test
    public void testFindBookByAuthorIsEmpty() {
        when(booksMap.values()).thenReturn(Arrays.asList());

        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.findBookByAuthor("");
        });
        assertEquals("Author name is Empty or null.", exp.getMessage());
    }

    @Test
    public void testFindBookByAuthorNotAvailable() {
        when(booksMap.values()).thenReturn(Arrays.asList());

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
        Book newBook = new Book("CC123", "New Book", "New Season", 2024, 50);
        service.addNewBook(newBook);
        verify(booksMap, times(1)).put("CC123", newBook);
    }

    @Test
    public void testAddNewBookDuplicate() {
        Book newBook = new Book("CC123", "New Book", "New Season", 2024, 50);
        when(booksMap.containsKey("CC123")).thenReturn(true);

        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.addNewBook(newBook);
        });
        assertEquals("Duplicate ISBN, Try with another ISBN no. ", exp.getMessage());
    }

    @Test
    public void testAddNewBookNull() {
        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.addNewBook(null);
        });
        assertEquals("Not a valid book details.", exp.getMessage());

    }

    @Test
    public void testRemoveBook() {
        when(booksMap.containsKey("AA111")).thenReturn(true);
        service.removeBook("AA111");
        verify(booksMap, times(1)).remove("AA111");
    }

    @Test
    public void testRemoveBookNotAvailable() {
        when(booksMap.containsKey("AA111")).thenReturn(false);

        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.removeBook("AA111");
        });
        assertEquals("ISBN is not found in the library.", exp.getMessage());
    }

    @Test
    public void testBorrowBook() {
        Book book1 = new Book("AA111", "Arrival", "Summer", 1995, 15);
        when(booksMap.containsKey("AA111")).thenReturn(true);
        when(booksMap.get("AA111")).thenReturn(book1);

        boolean status = service.borrowBook("AA111");
        assertTrue(status);
        assertEquals(14, book1.getAvailableCopies());
    }

    @Test
    public void testBorrowBookForZeroCopies() {
        Book book1 = new Book("AA111", "Arrival", "Summer", 1995, 0);
        when(booksMap.containsKey("AA111")).thenReturn(true);
        when(booksMap.get("AA111")).thenReturn(book1);

        NoCopiesAvailableException exp = assertThrows(NoCopiesAvailableException.class, () -> {
            service.borrowBook("AA111");
        });
        assertEquals("No available copies for book with ISBN AA111.", exp.getMessage());
    }

    @Test
    public void testReturnBook() {
        Book book1 = new Book("AA111", "Arrival", "Summer", 1995, 15);
        when(booksMap.containsKey("AA111")).thenReturn(true);
        when(booksMap.get("AA111")).thenReturn(book1);

        boolean status = service.returnBook("AA111");
        assertTrue(status);
        assertEquals(16, book1.getAvailableCopies());
    }

    @Test
    public void testBorrowBookNotFound() {
        when(booksMap.containsKey("AA111")).thenReturn(false);

        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.borrowBook("AA111");
        });
        assertEquals("Book (AA111) Not found", exp.getMessage());
    }

    @Test
    public void testReturnBookNotFound() {
        when(booksMap.containsKey("AA111")).thenReturn(false);

        BooksNotFoundException exp = assertThrows(BooksNotFoundException.class, () -> {
            service.returnBook("AA111");
        });
        assertEquals("Book (AA111) Not found", exp.getMessage());
    }
}