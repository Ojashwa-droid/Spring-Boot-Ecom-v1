package com.ecomerce.sb_ecom.controller;
// this is the package for all our category controllers


import com.ecomerce.sb_ecom.config.AppConstants;
import com.ecomerce.sb_ecom.payload.CategoryDTO;
import com.ecomerce.sb_ecom.payload.CategoryResponse;
import com.ecomerce.sb_ecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {


    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

//    @GetMapping("/api/public/sayhello")
//    public ResponseEntity<String> sayHelloToTheworld(
//    @RequestParam (name = "message", defaultValue = "Hello nub coder", required = false) String message){
//        return new ResponseEntity<>("Message: " + message, HttpStatus.OK);
//    }

//    @GetMapping("/api/public/echo")
//    public ResponseEntity<String> echoMessage(@RequestParam(name = "message", defaultValue = "Hello World!") String message) {
//        return new ResponseEntity<>("Echoed message: " + message, HttpStatus.OK);
//    }


    //  @RequestMapping(value = "/api/public/categories", method = RequestMethod.GET)

    // or we can use the @RequestMapping("/api") at the class level as well
    // so that it becomes evident that all the following methods are
    // going to have that particular portion of the url in them

//    @GetMapping("/api/public/categories")
//    public ResponseEntity<CategoryResponse> getAllCategories(){
//        CategoryResponse categoryResponse = categoryService.getAllCategories();
//        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
//    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories (
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);

        // we want to handle exceptions, if any, at the service layer where all the business logic has been written
        // it's better to have all the exceptions to be handled there only

        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO deleteCategoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deleteCategoryDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
           @Valid @RequestBody CategoryDTO categoryDTO,
            @PathVariable Long categoryId) {
        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
    }
}
