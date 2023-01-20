package com.reynaldoabreu.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    private Long id;
    private String costumer;
    private Book book;
    private LocalDate loanDate;
    private Boolean returned;

}
