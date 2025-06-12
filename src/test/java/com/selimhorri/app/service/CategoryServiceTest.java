package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.helper.CategoryMappingHelper;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private CategoryServiceImpl categoryService;
    
    private Category category;
    private CategoryDto categoryDto;
    private Category noCategory;
    
    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .build();
        
        categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .build();
                
        noCategory = Category.builder()
                .categoryId(999)
                .categoryTitle("No Category")
                .build();
    }
    
    @Test
    void findAll_ShouldReturnListOfCategories() {
        // Arrange
        when(categoryRepository.findAllNonReserved()).thenReturn(List.of(category));
        
        // Act
        List<CategoryDto> result = categoryService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category.getCategoryTitle(), result.get(0).getCategoryTitle());
        verify(categoryRepository, times(1)).findAllNonReserved();
    }
    
    @Test
    void findById_ExistingId_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findNonReservedById(1)).thenReturn(Optional.of(category));
        
        // Act
        CategoryDto result = categoryService.findById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(category.getCategoryId(), result.getCategoryId());
        verify(categoryRepository, times(1)).findNonReservedById(1);
    }
    
    @Test
    void findById_NonExistingId_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findNonReservedById(99)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(99));
        verify(categoryRepository, times(1)).findNonReservedById(99);
    }
    
    @Test
    void save_ValidCategory_ShouldReturnSavedCategory() {
        // Arrange
        categoryDto.setCategoryId(null);
        when(categoryRepository.existsByCategoryTitleIgnoreCase("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        
        // Act
        CategoryDto result = categoryService.save(categoryDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(category.getCategoryTitle(), result.getCategoryTitle());
        verify(categoryRepository, times(1)).existsByCategoryTitleIgnoreCase("Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    
    @Test
    void save_InvalidCategory_MissingTitle_ShouldThrowException() {
        // Arrange
        categoryDto.setCategoryTitle(null);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.save(categoryDto));
    }
    
    @Test
    void save_InvalidCategory_DuplicateName_ShouldThrowException() {
        // Arrange
        categoryDto.setCategoryId(null);
        when(categoryRepository.existsByCategoryTitleIgnoreCase("Electronics")).thenReturn(true);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.save(categoryDto));
    }
    
    @Test
    void update_ValidCategory_ShouldReturnUpdatedCategory() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryTitleIgnoreCaseAndCategoryIdNot("Updated", 1)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        
        categoryDto.setCategoryTitle("Updated");
        
        // Act
        CategoryDto result = categoryService.update(categoryDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(category.getCategoryId(), result.getCategoryId());
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).existsByCategoryTitleIgnoreCaseAndCategoryIdNot("Updated", 1);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    
    @Test
    void update_NonExistingCategory_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());
        categoryDto.setCategoryId(99);
        
        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.update(categoryDto));
    }
    
    @Test
    void updateWithId_ValidCategory_ShouldReturnUpdatedCategory() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryTitleIgnoreCaseAndCategoryIdNot("Updated", 1)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        
        categoryDto.setCategoryTitle("Updated");
        
        // Act
        CategoryDto result = categoryService.update(1, categoryDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(category.getCategoryId(), result.getCategoryId());
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).existsByCategoryTitleIgnoreCaseAndCategoryIdNot("Updated", 1);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
    
    @Test
    void deleteById_ValidCategory_ShouldDeleteAndMigrateProducts() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.findByCategoryTitleIgnoreCase("No Category")).thenReturn(Optional.of(noCategory));
        
        // Act
        categoryService.deleteById(1);
        
        // Assert
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).findByCategoryTitleIgnoreCase("No Category");
        verify(productRepository, times(1)).updateCategoryForProducts(1, noCategory);
        verify(categoryRepository, times(1)).delete(category);
    }
    
    @Test
    void deleteById_ReservedCategory_ShouldThrowException() {
        // Arrange
        Category reservedCategory = Category.builder()
                .categoryId(999)
                .categoryTitle("Deleted")
                .build();
        
        when(categoryRepository.findById(999)).thenReturn(Optional.of(reservedCategory));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteById(999));
    }
    
    @Test
    void deleteById_MissingNoCategory_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.findByCategoryTitleIgnoreCase("No Category")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> categoryService.deleteById(1));
    }
}