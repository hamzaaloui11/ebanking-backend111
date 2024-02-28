package org.sid.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.dtos.CustomerDto;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.services.BankAccountSer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestConroller {
    private BankAccountSer bankAccountSer;

    @GetMapping("/customers")
    public List<CustomerDto> customers(){
        return bankAccountSer.listCutomer();
    }

    @GetMapping("/customers/search")
    public List<CustomerDto>searchCustomers(@RequestParam(name = "keyword",defaultValue = "") String keyword){
        return bankAccountSer.searchCustomers("%"+keyword+"%");
    }

    @GetMapping("/customers/{id}")
    public CustomerDto getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return bankAccountSer.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDto saveCustomer(@RequestBody CustomerDto customerDto){
       return bankAccountSer.saveCustomer(customerDto);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDto updateCustomer(@PathVariable Long customerId,@RequestBody  CustomerDto customerDto){
        customerDto.setId(customerId);
      return   bankAccountSer.upditeCustomer(customerDto);

    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        bankAccountSer.deleteCostumer(id);
    }
}
