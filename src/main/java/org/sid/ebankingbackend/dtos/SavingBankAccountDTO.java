package org.sid.ebankingbackend.dtos;

import lombok.Data;
import org.sid.ebankingbackend.entites.Customer;
import org.sid.ebankingbackend.enums.AccountStatus;

import java.util.Date;


@Data
public class SavingBankAccountDTO extends BankAccountDTO{

    private String id;
    private double balance;
    private Date createAt;

    private AccountStatus status;
    private CustomerDto customerDTO;

    private double intersRate;
}
