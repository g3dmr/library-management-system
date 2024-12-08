package com.librarymanagement.service;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.BooksList;

public interface BooksService {

    public Book findBookByISBN(String isbn);

    public BooksList findBookByAuthor(String author);

    public void addNewBook(Book book);

    public void removeBook(String isbn);

    public boolean borrowBook(String isbn);

    public boolean returnBook(String isbn);
}
