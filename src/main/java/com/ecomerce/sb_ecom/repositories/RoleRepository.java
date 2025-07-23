package com.ecomerce.sb_ecom.repositories;

import com.ecomerce.sb_ecom.model.AppRole;
import com.ecomerce.sb_ecom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole appRole);
}
