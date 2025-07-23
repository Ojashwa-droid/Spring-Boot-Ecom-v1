package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.APIException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.model.Category;
import com.ecomerce.sb_ecom.payload.CategoryDTO;
import com.ecomerce.sb_ecom.payload.CategoryResponse;
import com.ecomerce.sb_ecom.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class CategoryServiceImpl implements CategoryService {

    // managing the categories in a list of Category
//    private List<Category> categories = new ArrayList<>();
//    private Long nextId = 1L; we dont need it anylonger since we are making use of @GeneratedValue(strategy = Generationtype.IDENTITY)

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // in order to use sorting of content of pages we are going to use sort class

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        /*
         Pageable is an interface provided by spring data JPA
         it represents the request for a specific page of data from the db query result

          // 1. method
         Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
         Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

         // 2. method
         Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending : Sort.by(sortBy).descnding();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        // now converting Page<Category> into a list
        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()){
            throw new ResourceNotFoundException("Nothing to be found!");
        }

        */
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
//        List<Category> categories = categoryRepository.findAll(); we used to do this until we applied concept of pages and sorting

        if (categories.isEmpty()) {
            throw new APIException("No categories created till now!");
        }

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        // setting up a category response
        categoryResponse.setContent(categoryDTOS);
        // setting up necessary info about the page through categoryPage
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;

        // return categories; --> used earlier when the model was tied to the presentation layer
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        // before creating a new category check if the category with that
        // name already exists
        // if it does throw a custom exception

        Category matchedCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());

        if (matchedCategory != null) {
            throw new APIException("Category with the name " + categoryDTO.getCategoryName() + " already exists!!");
        }
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

//         to find the category that the user wants to delete
//
//        List<Category> categories = categoryRepository.findAll();
//
//        Category category = categories.stream()
//                .filter(c -> c.getCategoryId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
//
//        if (category == null) {
//            return "Category not found!";

        // String resourceName, String field, String fieldName

        Category matchedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // instead of using the below exception we are making use of custom exception
        //new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));

        // using modelMapper to map the model object to categoryDTO (DTO object)
        CategoryDTO deletedCategoryDTO = modelMapper.map(matchedCategory, CategoryDTO.class);

        categoryRepository.delete(matchedCategory);
        return deletedCategoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = modelMapper.map(categoryDTO, Category.class); // map the categoryDTO to entity with map()

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        category.setCategoryName(category.getCategoryName());
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);

        CategoryDTO updatedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return updatedCategoryDTO;
    }
}
