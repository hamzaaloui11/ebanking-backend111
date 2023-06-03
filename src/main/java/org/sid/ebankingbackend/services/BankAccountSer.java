package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.dtos.*;
import org.sid.ebankingbackend.entites.BankAccount;
import org.sid.ebankingbackend.entites.CurrentAccount;
import org.sid.ebankingbackend.entites.Customer;
import org.sid.ebankingbackend.entites.SavingAccount;
import org.sid.ebankingbackend.exceptions.BalaceNotSufficientException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountSer {
    CustomerDto saveCustomer(CustomerDto customerDtoS);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interesRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDto> listCutomer();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId,double amount,String description) throws BankAccountNotFoundException, BalaceNotSufficientException;
    void credit(String accountId,double amount,String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource,String accountIdDestination,double amount) throws BalaceNotSufficientException, BankAccountNotFoundException;


    List<BankAccountDTO> bankAccountsList();

    CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDto upditeCustomer(CustomerDto customerDto);

    void deleteCostumer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDto> searchCustomers(String keyword);
}
