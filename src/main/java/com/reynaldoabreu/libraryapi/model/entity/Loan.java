package com.reynaldoabreu.libraryapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String costumer;
    @JoinColumn(name = "id_book")
    @ManyToOne
    private Book book;
    @Column
    private LocalDate loanDate;
    @Column
    private Boolean returned;

}
