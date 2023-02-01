package com.reynaldoabreu.libraryapi.service;

import com.reynaldoabreu.libraryapi.api.dto.LoanFilterDTO;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;
    LoanService service;
    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImp(repository);

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

        Mockito.when(repository.save(saivingLoan)).thenReturn(savedLoan);
        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);

        Loan loan = service.save(saivingLoan);

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

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(saivingLoan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(saivingLoan);

    }

    @Test
    @DisplayName("Deve obter informações de um emprestimo pelo ID")
    public void getLoanDetailsTest(){

        //cenario
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(loan.getId());
        assertThat(result.get().getCostumer()).isEqualTo(loan.getCostumer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);

    }

    @Test
    @DisplayName("Deve atualizar o emprestimo")
    public void updateLoanTest(){

        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updateLoan = service.update(loan);

        assertThat(updateLoan.getReturned()).isTrue();
        verify(repository).save(loan);

    }

    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void findLoanTest(){

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> list = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<>(list, pageRequest, list.size());
        when( repository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execução
        Page<Loan> result = service.find( loanFilterDTO, pageRequest );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    public static Loan createLoan() {

        Book book = Book.builder().id(1L).build();
        return Loan.builder()
                .book(book)
                .loanDate(LocalDate.now())
                .costumer("Fulano")
                .build();

    }

}
