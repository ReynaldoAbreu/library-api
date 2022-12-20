package com.reynaldoabreu.libraryapi.api.resource;

import com.reynaldoabreu.libraryapi.api.dto.BookDto;
import com.reynaldoabreu.libraryapi.api.exception.ApiErrors;
import com.reynaldoabreu.libraryapi.model.entity.Book;
import com.reynaldoabreu.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@RequestBody @Valid BookDto dto){

        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);

        return modelMapper.map(entity, BookDto.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();

        return  new ApiErrors(bindingResult);
    }

}
