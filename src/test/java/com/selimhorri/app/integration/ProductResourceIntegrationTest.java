package com.selimhorri.app.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.service.ProductService;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class ProductResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDto productDto;
    private List<ProductDto> productDtos;

    @BeforeEach
    void setUp() {
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .build();

        this.productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Smartphone")
                .imageUrl("smartphone.jpg")
                .sku("SMARTPHONE-001")
                .priceUnit(599.99)
                .quantity(50)
                .categoryDto(categoryDto)
                .build();

        this.productDtos = Collections.singletonList(this.productDto);
    }

    @Test
    void testFindAll() throws Exception {
        when(this.productService.findAll())
                .thenReturn(this.productDtos);

        this.mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].productId").value(this.productDto.getProductId()))
                .andExpect(jsonPath("$.collection[0].productTitle").value(this.productDto.getProductTitle()))
                .andExpect(jsonPath("$.collection[0].sku").value(this.productDto.getSku()));
    }

    @Test
    void testFindById() throws Exception {
        when(this.productService.findById(anyInt()))
                .thenReturn(this.productDto);

        this.mockMvc.perform(get("/api/products/{productId}", this.productDto.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(this.productDto.getProductId()))
                .andExpect(jsonPath("$.productTitle").value(this.productDto.getProductTitle()))
                .andExpect(jsonPath("$.priceUnit").value(this.productDto.getPriceUnit()));
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        when(this.productService.findById(anyInt()))
                .thenThrow(new ProductNotFoundException("Product not found"));

        this.mockMvc.perform(get("/api/products/{productId}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSave() throws Exception {
        when(this.productService.save(any(ProductDto.class)))
                .thenReturn(this.productDto);

        this.mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(this.productDto.getProductId()))
                .andExpect(jsonPath("$.productTitle").value(this.productDto.getProductTitle()));
    }

    @Test
    void testSaveCategoryNotFound() throws Exception {
        when(this.productService.save(any(ProductDto.class)))
                .thenThrow(new CategoryNotFoundException("Category not found"));

        this.mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.productDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate() throws Exception {
        when(this.productService.update(any(ProductDto.class)))
                .thenReturn(this.productDto);

        this.mockMvc.perform(put("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(this.productDto.getProductId()))
                .andExpect(jsonPath("$.sku").value(this.productDto.getSku()));
    }

    @Test
    void testUpdateWithId() throws Exception {
        when(this.productService.update(anyInt(), any(ProductDto.class)))
                .thenReturn(this.productDto);

        this.mockMvc.perform(put("/api/products/{productId}", this.productDto.getProductId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(this.productDto.getProductId()))
                .andExpect(jsonPath("$.quantity").value(this.productDto.getQuantity()));
    }

    @Test
    void testUpdateNotFound() throws Exception {
        when(this.productService.update(anyInt(), any(ProductDto.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        this.mockMvc.perform(put("/api/products/{productId}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.productDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(this.productService).deleteById(anyInt());

        this.mockMvc.perform(delete("/api/products/{productId}", this.productDto.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testDeleteByIdNotFound() throws Exception {
        doThrow(new ProductNotFoundException("Product not found"))
                .when(this.productService)
                .deleteById(anyInt());

        this.mockMvc.perform(delete("/api/products/{productId}", 999))
                .andExpect(status().isNotFound());
    }
}