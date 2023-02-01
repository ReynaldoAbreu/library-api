package com.reynaldoabreu.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reynaldoabreu.libraryapi.api.dto.LoanDto;
import com.reynaldoabreu.libraryapi.api.dto.LoanFilterDTO;
import com.reynaldoabreu.libraryapi.api.dto.ReturnedLoanDTO;
import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.entity.Loan;
import com.reynaldoabreu.libraryapi.service.BookService;
import com.reynaldoabreu.libraryapi.service.LoanService;
import com.reynaldoabreu.libraryapi.service.LoanServiceTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;



    @Test
    @DisplayName("Deve fazer um emprestimo")
    public void createLoanTest() throws Exception {

        LoanDto dto = LoanDto.builder()
                .isbn("123")
                .costumer("Fulano")
                .build();
        String jason = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L)
                .isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123") ).willReturn(Optional.of(book));
        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .costumer("Fulano")
                .loanDate(LocalDate.now())
                .build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jason);

        mvc.perform(request)
                .andExpect( status().isCreated() )
                .andExpect(content().string("1"));

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar realizar emprestimo de um livro inexistente")
    public void invalidIsbnLoanCreateTest() throws Exception {

        LoanDto dto = LoanDto.builder()
                .isbn("123")
                .costumer("Fulano")
                .build();
        String jason = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty() );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jason);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book not found for passed isbn"));

    }@Test
    @DisplayName("Deve retornar erro ao tentar realizar emprestimo de um livro emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {

        LoanDto dto = LoanDto.builder()
                .isbn("123")
                .costumer("Fulano")
                .build();
        String jason = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L)
                .isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123") ).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow( new BusinessException(" Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jason);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value(" Book already loaned"));

    }

    @Test
    @DisplayName("Deve realizar devolução de um livro")
    public void returnBookTest() throws Exception{

        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Loan loan = Loan.builder().id(1L).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);

    }
    @Test
    @DisplayName("Deve retornar 404 quando devolver um livro inexistente")
    public void returnNotExistBookTest() throws Exception{

        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Loan loan = Loan.builder().id(1L).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isNotFound());

        Mockito.verify(loanService, Mockito.never()).update(loan);

    }

    @Test
    @DisplayName("Deve filtrar emprestimo")
    public void findBooksTest() throws Exception {

        //cenario
        Long id = 1L;
        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().id(1L).isbn("321").build();
        loan.setBook(book);

        BDDMockito.given(loanService.find( Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100",
                book.getIsbn(), loan.getCostumer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect( status().isOk())
                .andExpect( jsonPath( "content", Matchers.hasSize(1)))
                .andExpect(jsonPath( "totalElements").value(1))
                .andExpect(jsonPath( "pageable.pageSize").value(10))
                .andExpect(jsonPath( "pageable.pageNumber").value(0));

    }

}
