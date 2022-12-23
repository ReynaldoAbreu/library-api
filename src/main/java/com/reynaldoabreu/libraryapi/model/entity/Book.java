package com.reynaldoabreu.libraryapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Book {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;
    @Column
    private String author;
    @Column
    private String isbn;
}
