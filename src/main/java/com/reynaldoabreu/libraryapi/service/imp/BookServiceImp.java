package com.reynaldoabreu.libraryapi.service.imp;

import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.repository.BookRepository;
import com.reynaldoabreu.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {

        if (book == null || book.getId() == null){
            throw new IllegalArgumentException(" Book Id can't be null");
        }

        this.repository.delete(book);

    }

    @Override
    public Book update(Book book) {

        if (book == null || book.getId() == null){
            throw new IllegalArgumentException(" Book Id can't be null");
        }

        return this.repository.save(book);

    }

}
