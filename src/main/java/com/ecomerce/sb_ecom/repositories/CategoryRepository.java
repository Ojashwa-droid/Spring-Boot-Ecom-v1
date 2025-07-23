package com.ecomerce.sb_ecom.repositories;


import com.ecomerce.sb_ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
}