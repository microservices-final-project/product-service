package com.selimhorri.app.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import com.selimhorri.app.service.CategoryService;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class CategoryResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryDto categoryDto;
    private List<CategoryDto> categoryDtos;

    @BeforeEach
    void setUp() {
        this.categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("electronics.jpg")
                .productDtos(Set.of(
                        ProductDto.builder()
                                .productId(1)
                                .productTitle("Laptop")
                                .priceUnit(999.99)
                                .quantity(10)
                                .build()))
                .build();

        this.categoryDtos = Collections.singletonList(this.categoryDto);
    }

    @Test
    void testFindAll() throws Exception {
        when(this.categoryService.findAll())
                .thenReturn(this.categoryDtos);

        this.mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection[0].categoryId").value(this.categoryDto.getCategoryId()))
                .andExpect(jsonPath("$.collection[0].categoryTitle").value(this.categoryDto.getCategoryTitle()))
                .andExpect(jsonPath("$.collection[0].imageUrl").value(this.categoryDto.getImageUrl()));
    }

    @Test
    void testFindById() throws Exception {
        when(this.categoryService.findById(anyInt()))
                .thenReturn(this.categoryDto);

        this.mockMvc.perform(get("/api/categories/{categoryId}", this.categoryDto.getCategoryId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(this.categoryDto.getCategoryId()))
                .andExpect(jsonPath("$.categoryTitle").value(this.categoryDto.getCategoryTitle()))
                .andExpect(jsonPath("$.imageUrl").value(this.categoryDto.getImageUrl()));
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        when(this.categoryService.findById(anyInt()))
                .thenThrow(new CategoryNotFoundException("Category not found"));

        this.mockMvc.perform(get("/api/categories/{categoryId}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSave() throws Exception {
        when(this.categoryService.save(any(CategoryDto.class)))
                .thenReturn(this.categoryDto);

        this.mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(this.categoryDto.getCategoryId()))
                .andExpect(jsonPath("$.categoryTitle").value(this.categoryDto.getCategoryTitle()));
    }
    
    @Test
    void testUpdate() throws Exception {
        when(this.categoryService.update(any(CategoryDto.class)))
                .thenReturn(this.categoryDto);

        this.mockMvc.perform(put("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(this.categoryDto.getCategoryId()))
                .andExpect(jsonPath("$.categoryTitle").value(this.categoryDto.getCategoryTitle()));
    }

    @Test
    void testUpdateWithId() throws Exception {
        when(this.categoryService.update(anyInt(), any(CategoryDto.class)))
                .thenReturn(this.categoryDto);

        this.mockMvc.perform(put("/api/categories/{categoryId}", this.categoryDto.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(this.categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(this.categoryDto.getCategoryId()))
                .andExpect(jsonPath("$.categoryTitle").value(this.categoryDto.getCategoryTitle()));
    }

    @Test
    void testDeleteById() throws Exception {
        doNothing().when(this.categoryService).deleteById(anyInt());

        this.mockMvc.perform(delete("/api/categories/{categoryId}", this.categoryDto.getCategoryId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}