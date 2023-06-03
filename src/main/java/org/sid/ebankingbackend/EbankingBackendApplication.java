package org.sid.ebankingbackend;

import org.sid.ebankingbackend.dtos.BankAccountDTO;
import org.sid.ebankingbackend.dtos.CurrentBankAccountDTO;
import org.sid.ebankingbackend.dtos.CustomerDto;
import org.sid.ebankingbackend.dtos.SavingBankAccountDTO;
import org.sid.ebankingbackend.entites.*;
import org.sid.ebankingbackend.enums.AccountStatus;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exceptions.BalaceNotSufficientException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.repositories.AccountOperationRepo;
import org.sid.ebankingbackend.repositories.BankAccountRepo;
import org.sid.ebankingbackend.repositories.CustomerRepo;
import org.sid.ebankingbackend.services.BankAccountSer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountSer bankAccountSer){
        return args -> {

           Stream.of("hassan","imane","hamza").forEach(name->{
               CustomerDto customer=new CustomerDto();
               customer.setName(name);
               customer.setEmail(name+"@gmail.com");
               bankAccountSer.saveCustomer(customer);
           });
           bankAccountSer.listCutomer().forEach(customer -> {
               try {
                   bankAccountSer.saveCurrentBankAccount(Math.random()*90000,9000, customer.getId());
                   bankAccountSer.saveSavingBankAccount(Math.random()*120000,5.5, customer.getId());

               } catch (CustomerNotFoundException e) {
                   e.printStackTrace();
               }

           });
            List<BankAccountDTO> bankAccounts = bankAccountSer.bankAccountsList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i=0;i<10;i++){
                    String accountId;
                    if (bankAccount instanceof SavingBankAccountDTO){
                        accountId=((SavingBankAccountDTO) bankAccount).getId();
                    }else{
                        accountId=((CurrentBankAccountDTO)bankAccount).getId();
                    }
                    bankAccountSer.credit(accountId,10000+Math.random()*120000,"credit");
                    bankAccountSer.debit(accountId,1000+Math.random()*9000,"debit");
                }
            }
        };
    }
    //@Bean
    CommandLineRunner start(CustomerRepo customerRepo ,
                            BankAccountRepo bankAccountRepo,
                            AccountOperationRepo accountOperationRepo){
        return args -> {
            Stream.of("hassan","hamza","laila").forEach(name->{
                Customer customer= new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepo.save(customer);
            });
            customerRepo.findAll().forEach(cust->{
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreateAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepo.save(currentAccount);

                SavingAccount savingAccount= new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCreateAt(new Date());
                savingAccount.setCustomer(cust);
                savingAccount.setIntersRate(5.5);
                bankAccountRepo.save(savingAccount);
            });
            bankAccountRepo.findAll().forEach(acc->{
                for (int i=0; i<10; i++){
                    AccountOperation accountOperation=new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepo.save(accountOperation);
                }
            });
        };
    }


}
