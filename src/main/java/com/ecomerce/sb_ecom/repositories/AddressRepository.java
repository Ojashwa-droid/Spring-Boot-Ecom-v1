package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.model.Address;
import com.ecomerce.sb_ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
