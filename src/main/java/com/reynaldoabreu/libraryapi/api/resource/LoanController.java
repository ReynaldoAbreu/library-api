package com.reynaldoabreu.libraryapi.api.resource;

import com.reynaldoabreu.libraryapi.api.dto.LoanDto;
import com.reynaldoabreu.libraryapi.api.dto.LoanFilterDTO;
import com.reynaldoabreu.libraryapi.api.dto.ReturnedLoanDTO;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.model.entity.Loan;
import com.reynaldoabreu.libraryapi.service.BookService;
import com.reynaldoabreu.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto){

        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException (HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan entity = Loan.builder().book(book)
                .costumer(dto.getCostumer())
                .loanDate(LocalDate.now()).build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){

        Loan loan = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());

        service.update(loan);
    }

    @GetMapping
    public Page<LoanDto> find(LoanFilterDTO dto, Pageable pageable){

        Page<Loan> result = service.find(dto, pageable);

        return null;
    }



}
