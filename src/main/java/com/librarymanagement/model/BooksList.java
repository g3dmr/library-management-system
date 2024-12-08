package com.librarymanagement.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class BooksList {

    private List<Book> bookList = new ArrayList<Book>();

}
