package com.reynaldoabreu.libraryapi.api.resource;

import com.reynaldoabreu.libraryapi.api.dto.LoanDto;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.entity.Loan;
import com.reynaldoabreu.libraryapi.service.BookService;
import com.reynaldoabreu.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto){

        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException (HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan entity = Loan.builder().book(book)
                .costumer(dto.getCostumer())
                .loanDate(LocalDate.now()).build();

        entity = loanService.save(entity);
        return entity.getId();
    }
}
