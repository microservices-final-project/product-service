package com.selimhorri.app.unit.resource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.ApiExceptionHandler;
import com.selimhorri.app.resource.ProductResource;
import com.selimhorri.app.service.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductResource productResource;

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(productResource).setControllerAdvice(new ApiExceptionHandler())
                .build();

        productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Laptop")
                .imageUrl("laptop.jpg")
                .sku("LP123")
                .priceUnit(999.99)
                .quantity(10)
                .build();
    }

    @Test
    void findAll_ShouldReturnAllProducts() throws Exception {
        // Arrange
        when(productService.findAll()).thenReturn(List.of(productDto));

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].productTitle").value("Laptop"));

        verify(productService, times(1)).findAll();
    }

    @Test
    void findById_ValidId_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.findById(1)).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle").value("Laptop"));

        verify(productService, times(1)).findById(1);
    }

    @Test
    void findById_BlankId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/ ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void save_ValidProduct_ShouldReturnSavedProduct() throws Exception {
        // Arrange
        when(productService.save(any(ProductDto.class))).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle").value("Laptop"));

        verify(productService, times(1)).save(any(ProductDto.class));
    }

    @Test
    void update_ValidProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        when(productService.update(any(ProductDto.class))).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(put("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle").value("Laptop"));

        verify(productService, times(1)).update(any(ProductDto.class));
    }

    @Test
    void updateWithId_ValidProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        when(productService.update(eq(1), any(ProductDto.class))).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productTitle").value("Laptop"));

        verify(productService, times(1)).update(eq(1), any(ProductDto.class));
    }

    @Test
    void updateWithId_BlankId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/products/ ")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_ValidId_ShouldReturnTrue() throws Exception {
        // Arrange
        doNothing().when(productService).deleteById(1);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(productService, times(1)).deleteById(1);
    }
}