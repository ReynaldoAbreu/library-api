package com.reynaldoabreu.libraryapi.service;

import com.reynaldoabreu.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);
}
