package com.reynaldoabreu.libraryapi.service.imp;

import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.repository.BookRepository;
import com.reynaldoabreu.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.prefs.BackingStoreException;

@Service
public class BookServiceImp implements BookService {
    private BookRepository repository;

    public BookServiceImp(BookRepository repository) {
        this.repository = repository;
}

    @Override
    public Book save(Book book) {

        if (repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }
}
