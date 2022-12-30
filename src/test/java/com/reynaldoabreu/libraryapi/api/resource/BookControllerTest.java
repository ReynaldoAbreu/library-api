package com.reynaldoabreu.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reynaldoabreu.libraryapi.api.dto.BookDTO;
import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest
public class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    static String BOOK_API = "/api/books";

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {
        BookDTO dto = BookDTO.builder().title("As aventuras").author("Arthur").isbn("123456").build();
        Book saveBook = Book.builder().id(10L).title("As aventuras").author("Arthur").isbn("123456").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(10L))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficiente para criação do livro.")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    public void createBookWithDuplicateIsbn() throws Exception {

        //cenario (givem)
        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);

        String messageError = "Isbn já cadastrado.";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(messageError));

        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(messageError));

    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetailsTest() throws Exception {
        //cenario (give)
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .isbn(createNewBook().getIsbn())
                .author(createNewBook().getAuthor())
                .title(createNewBook().getTitle())
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    public void bookNotFoundTest() throws Exception {

        //cenario
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1L))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1L));

        mvc.perform(request)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar um livro para deletar")
    public void deleteNoExistentBookTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1L));

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {

        //cenario
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book updatingBook = Book.builder().id(id).title("some title").author("some author").isbn("321").build();
        BDDMockito.given( service.getById(id) ).willReturn( Optional.of(updatingBook) );


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("id").value(id) )
                .andExpect( jsonPath("title").value(createNewBook().getTitle()) )
                .andExpect( jsonPath("author").value(createNewBook().getAuthor()) )
                .andExpect( jsonPath("isbn").value("321") );


    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateNoExistentBookTest() throws Exception {

        //cenario
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given( service.getById(id))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1L))
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    private static BookDTO createNewBook() {
        return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();

    }

}
