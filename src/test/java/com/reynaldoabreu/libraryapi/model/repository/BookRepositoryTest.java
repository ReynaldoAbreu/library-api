package com.reynaldoabreu.libraryapi.model.repository;

import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExist(){

        //cenario
        String isbn = "123";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        //execução
        boolean exist = repository.existsByIsbn(isbn);

        //verificação
        assertThat(exist).isTrue();

    }



    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnNotExist(){

        //cenario
        String isbn = "123";

        //execução
        boolean exist = repository.existsByIsbn(isbn);

        //verificação
        assertThat(exist).isFalse();

    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void findByIdTest(){
        //cenarrio
        Book book = createNewBook("123");
        entityManager.persist(book);

        //execução
        Optional<Book> foundBook = repository.findById(book.getId());

        //verificação
        assertThat(foundBook.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenarrio
        Book book = createNewBook("123");

        //execução
        Book saveBook = repository.save(book);

        //verificação
        assertThat(saveBook.getId()).isNotNull();

    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void deleteBookTest(){
        //cenarrio
        Book book = createNewBook("123");
        entityManager.persist(book);
        Book foundBook = entityManager.find(Book.class, book.getId());

        //execução
        repository.delete(foundBook);
        Book deletedBook = entityManager.find(Book.class, book.getId());

        //verificação

        assertThat(deletedBook).isNull();

    }

    public static Book createNewBook(String isbn) {
        return Book.builder().title("As aventuras").author("arthur").isbn(isbn).build();
    }

}
