package org.sid.ebankingbackend.repositories;

import org.sid.ebankingbackend.entites.BankAccount;
import org.sid.ebankingbackend.entites.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepo extends JpaRepository<BankAccount,String> {
}
