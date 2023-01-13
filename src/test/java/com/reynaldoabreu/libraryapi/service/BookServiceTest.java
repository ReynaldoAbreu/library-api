package com.reynaldoabreu.libraryapi.service;

import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Book;

import com.reynaldoabreu.libraryapi.model.repository.BookRepository;
import com.reynaldoabreu.libraryapi.service.imp.BookServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
public class BookServiceTest {
    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){

        this.service = new BookServiceImp(repository);

    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(Book.builder()
                .id(1L).isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build());

        //execução
        Book savedBook = service.save(book);

        //verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");

    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicateISBN(){
        // cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execução
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificação
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest(){

        //cenario
        Long id = 1L;
        Book  book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execução
        Optional<Book> foundBook = service.getById(id);

        //verificação
        assertThat( foundBook.isPresent()).isTrue();
        assertThat( foundBook.get().getId()).isEqualTo(book.getId());
        assertThat( foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat( foundBook.get().getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro quando ele não existir na base")
    public void bookNotFoundById(){

        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execução
        Optional<Book> book = service.getById(id);

        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){

        Book book = Book.builder().id(1L).build();

        //execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));

        //verificação

        Mockito.verify(repository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest(){

        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class, () -> service.delete(book) );

        Mockito.verify( repository, Mockito.never() ).delete(book);

    }

    @Test
    @DisplayName("deve ocorrer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest(){

        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class, () -> service.update(book) );

        Mockito.verify( repository, Mockito.never() ).save(book);

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){

        //cenario
        Long id = 1L;
        Book updatingBook = Book.builder().id(id).build();

        Book bookUpdate = createValidBook();
        bookUpdate.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(bookUpdate);

        //execução
        Book book = service.update(updatingBook);

        assertThat(book.getId()).isEqualTo(bookUpdate.getId());
        assertThat(book.getTitle()).isEqualTo(bookUpdate.getTitle());
        assertThat(book.getIsbn()).isEqualTo(bookUpdate.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(bookUpdate.getAuthor());

    }

    @Test
    @DisplayName("Deve filtrar um livro pelas propriedades")
    public void findBookTest(){

        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any( PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }



    private static Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

}
