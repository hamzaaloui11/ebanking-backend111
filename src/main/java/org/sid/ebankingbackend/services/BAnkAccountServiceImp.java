package org.sid.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.dtos.*;
import org.sid.ebankingbackend.entites.*;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exceptions.BalaceNotSufficientException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.mappers.BankAccountMapperImp;
import org.sid.ebankingbackend.repositories.AccountOperationRepo;
import org.sid.ebankingbackend.repositories.BankAccountRepo;
import org.sid.ebankingbackend.repositories.CustomerRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BAnkAccountServiceImp implements BankAccountSer{
    private CustomerRepo customerRepo;
     private BankAccountRepo bankAccountRepo;
     private AccountOperationRepo accountOperationRepo;
     private BankAccountMapperImp dtoMapper;

    @Override
    public CustomerDto saveCustomer(CustomerDto customerDto) {
        log.info("Saving new Customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDto);
        Customer savedCustomer = customerRepo.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        CurrentAccount currentAccount=new CurrentAccount();
        Customer customer = customerRepo.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException("Customer not found");

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreateAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount savedBankAccount=bankAccountRepo.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interesRate, Long customerId) throws CustomerNotFoundException {
        SavingAccount savingAccount=new SavingAccount();
        Customer customer = customerRepo.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException("Customer not found");

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreateAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setIntersRate(interesRate);
        savingAccount.setCustomer(customer);
        SavingAccount savedBankAccount=bankAccountRepo.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDto> listCutomer() {
       List<Customer> customers = customerRepo.findAll();
       List<CustomerDto> customerDtos=customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
       /*
       List<CustomerDto> customerDtos=new ArrayList<>();
       for (Customer customer:customers){
           CustomerDto customerDto=dtoMapper.fromCustomer(customer);
           customerDtos.add(customerDto);
       }*/
       return customerDtos;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepo.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount Not found"));
        if (bankAccount instanceof SavingAccount){
            SavingAccount savingAccount= (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else{
            CurrentAccount currentAccount= (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalaceNotSufficientException {
        BankAccount bankAccount=bankAccountRepo.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount Not found"));
          if (bankAccount.getBalance()<amount)
              throw new BalaceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepo.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepo.save(bankAccount);

    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepo.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount Not found"));
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepo.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepo.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BalaceNotSufficientException, BankAccountNotFoundException {
        debit(accountIdSource,amount,"transfer");
        credit(accountIdDestination,amount,"transfer from"+accountIdSource);

    }
    @Override
   public List<BankAccountDTO> bankAccountsList(){
        List<BankAccount> bankAccounts = bankAccountRepo.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepo.findById(customerId).orElseThrow(()->new CustomerNotFoundException("customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDto upditeCustomer(CustomerDto customerDto) {
        log.info("Saving new Customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDto);
        Customer savedCustomer = customerRepo.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCostumer(Long customerId){
       customerRepo.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
      List<AccountOperation> accountOperations= accountOperationRepo.findByBankAccountId(accountId);
      return accountOperations.stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
       BankAccount bankAccount=bankAccountRepo.findById(accountId).orElse(null);
       if(bankAccount==null)throw new BankAccountNotFoundException("Account not found");
        Page<AccountOperation> accountOperation = accountOperationRepo.findByBankAccountId(accountId, PageRequest.of(page,size));
       AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
       List<AccountOperationDTO>accountOperationDTOS = accountOperation.getContent().stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
       accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
       accountHistoryDTO.setAccountId(bankAccount.getId());
       accountHistoryDTO.setBalance(bankAccount.getBalance());
       accountHistoryDTO.setCurrentPage(page);
       accountHistoryDTO.setPageSize(size);
       accountHistoryDTO.setTotalPage(accountOperation.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDto> searchCustomers(String keyword) {
        List<Customer> customers=customerRepo.searchCustomer(keyword);
        List<CustomerDto>customerDTOs = customers.stream().map(cust->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
        return customerDTOs;
    }
}
