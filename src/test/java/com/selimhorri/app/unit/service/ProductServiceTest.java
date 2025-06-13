package com.selimhorri.app.unit.service;

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
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product product;
    private ProductDto productDto;
    private Category category;
    private CategoryDto categoryDto;
    
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
        
        product = Product.builder()
                .productId(1)
                .productTitle("Laptop")
                .imageUrl("laptop.jpg")
                .sku("LP123")
                .priceUnit(999.99)
                .quantity(10)
                .category(category)
                .build();
        
        productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Laptop")
                .imageUrl("laptop.jpg")
                .sku("LP123")
                .priceUnit(999.99)
                .quantity(10)
                .categoryDto(categoryDto)
                .build();
    }
    
    @Test
    void findAll_ShouldReturnListOfProducts() {
        // Arrange
        when(productRepository.findAllWithoutDeleted()).thenReturn(List.of(product));
        
        // Act
        List<ProductDto> result = productService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getProductTitle(), result.get(0).getProductTitle());
        verify(productRepository, times(1)).findAllWithoutDeleted();
    }
    
    @Test
    void findById_ExistingId_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findByIdWithoutDeleted(1)).thenReturn(Optional.of(product));
        
        // Act
        ProductDto result = productService.findById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(product.getProductId(), result.getProductId());
        verify(productRepository, times(1)).findByIdWithoutDeleted(1);
    }
    
    @Test
    void findById_NonExistingId_ShouldThrowException() {
        // Arrange
        when(productRepository.findByIdWithoutDeleted(99)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.findById(99));
        verify(productRepository, times(1)).findByIdWithoutDeleted(99);
    }
    
    @Test
    void save_ValidProduct_ShouldReturnSavedProduct() {
        // Arrange
        productDto.setProductId(null); // For new product
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        // Act
        ProductDto result = productService.save(productDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(product.getProductId(), result.getProductId());
        verify(categoryRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void save_InvalidProduct_MissingTitle_ShouldThrowException() {
        // Arrange
        productDto.setProductTitle(null);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.save(productDto));
    }
    
    @Test
    void save_InvalidProduct_MissingCategory_ShouldThrowException() {
        // Arrange
        productDto.setCategoryDto(null);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.save(productDto));
    }
    
    @Test
    void save_InvalidProduct_NonExistingCategory_ShouldThrowException() {
        // Arrange
        productDto.getCategoryDto().setCategoryId(99);
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> productService.save(productDto));
    }
    
    @Test
    void update_ValidProduct_ShouldReturnUpdatedProduct() {
        // Arrange
        when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        productDto.setProductId(1);
        
        // Act
        ProductDto result = productService.update(productDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(product.getProductId(), result.getProductId());
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void update_NonExistingProduct_ShouldThrowException() {
        // Arrange
        productDto.setProductId(99);
        when(productRepository.existsById(99)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.update(productDto));
    }
    
    @Test
    void updateWithId_ValidProduct_ShouldReturnUpdatedProduct() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        // Act
        ProductDto result = productService.update(1, productDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(product.getProductId(), result.getProductId());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void updateWithId_NonExistingProduct_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.update(99, productDto));
    }
    
    @Test
    void deleteById_ExistingProduct_ShouldSoftDelete() {
        // Arrange
        Category deletedCategory = Category.builder()
                .categoryId(999)
                .categoryTitle("Deleted")
                .build();
        
        when(productRepository.findByIdWithoutDeleted(1)).thenReturn(Optional.of(product));
        when(categoryRepository.findByCategoryTitle("Deleted")).thenReturn(Optional.of(deletedCategory));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        // Act
        productService.deleteById(1);
        
        // Assert
        verify(productRepository, times(1)).findByIdWithoutDeleted(1);
        verify(categoryRepository, times(1)).findByCategoryTitle("Deleted");
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void deleteById_NonExistingProduct_ShouldThrowException() {
        // Arrange
        when(productRepository.findByIdWithoutDeleted(99)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteById(99));
    }
    
    @Test
    void deleteById_MissingDeletedCategory_ShouldThrowException() {
        // Arrange
        when(productRepository.findByIdWithoutDeleted(1)).thenReturn(Optional.of(product));
        when(categoryRepository.findByCategoryTitle("Deleted")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.deleteById(1));
    }
}