package com.reynaldoabreu.libraryapi.service;

import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.entity.Loan;
import com.reynaldoabreu.libraryapi.model.repository.LoanRepository;
import com.reynaldoabreu.libraryapi.service.imp.LoanServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository loanRepository;
    LoanService loanService;
    @BeforeEach
    public void setUp(){
        this.loanService = new LoanServiceImp(loanRepository);

    }

    @Test
    @DisplayName("deve salvar um emprestimo")
    public void saveLoanTest(){

        Book book = Book.builder().id(1L).build();
        Loan saivingLoan = Loan.builder()
                .book(book)
                .loanDate(LocalDate.now())
                .costumer("Fulano")
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .costumer("Fulano")
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.save(saivingLoan)).thenReturn(savedLoan);
        when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(false);

        Loan loan = loanService.save(saivingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCostumer()).isEqualTo(savedLoan.getCostumer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Dev lançar erro de negocio ao salvar um livro já emprestado")
    public void loanedBookSaveTest(){

        Book book = Book.builder().id(1L).build();
        Loan saivingLoan = Loan.builder()
                .book(book)
                .loanDate(LocalDate.now())
                .costumer("Fulano")
                .build();

        when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> loanService.save(saivingLoan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(loanRepository, never()).save(saivingLoan);

    }

}
