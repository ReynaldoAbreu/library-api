package com.reynaldoabreu.libraryapi.service.imp;

import com.reynaldoabreu.libraryapi.exception.BusinessException;
import com.reynaldoabreu.libraryapi.model.entity.Loan;
import com.reynaldoabreu.libraryapi.model.repository.LoanRepository;
import com.reynaldoabreu.libraryapi.service.LoanService;
import org.springframework.stereotype.Service;

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
}
