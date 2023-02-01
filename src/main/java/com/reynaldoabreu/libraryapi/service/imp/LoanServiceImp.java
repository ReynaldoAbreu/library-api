package com.reynaldoabreu.libraryapi.service.imp;

import com.reynaldoabreu.libraryapi.api.dto.LoanFilterDTO;
import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Loan;
import com.reynaldoabreu.libraryapi.model.repository.LoanRepository;
import com.reynaldoabreu.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImp implements LoanService {

    private final LoanRepository repository;

    public LoanServiceImp(LoanRepository loanRepository) {
        this.repository = loanRepository;

    }

    @Override
    public Loan save(Loan loan) {

        if (repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
        return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
    }

}
