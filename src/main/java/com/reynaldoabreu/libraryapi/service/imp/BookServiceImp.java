package com.reynaldoabreu.libraryapi.service.imp;

import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.repository.BookRepository;
import com.reynaldoabreu.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImp implements BookService {
    private BookRepository repository;

    public BookServiceImp(BookRepository repository) {
        this.repository = repository;
}

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
