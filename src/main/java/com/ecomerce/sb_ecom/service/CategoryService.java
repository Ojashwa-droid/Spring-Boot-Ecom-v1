package com.ecomerce.sb_ecom.service;

// * We can use class as well but using service layer as an interface
// will enhance modularity and loose coupling


// having separate interface for service(business logic) and separate implementation of it
// will promote loose coupling and will help in the long run


import com.ecomerce.sb_ecom.model.Category;
import com.ecomerce.sb_ecom.payload.CategoryDTO;
import com.ecomerce.sb_ecom.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
