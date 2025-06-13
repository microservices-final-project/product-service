package com.selimhorri.app.unit.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.resource.CategoryResource;
import com.selimhorri.app.service.CategoryService;

@ExtendWith(MockitoExtension.class)
class CategoryResourceTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryResource categoryResource;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryResource).build();
        objectMapper = new ObjectMapper();
        
        categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/image.jpg")
                .build();
    }

    @Test
    void testFindAll_ShouldReturnListOfCategories() throws Exception {
        // Given
        List<CategoryDto> categories = Arrays.asList(
                CategoryDto.builder()
                        .categoryId(1)
                        .categoryTitle("Electronics")
                        .imageUrl("http://example.com/electronics.jpg")
                        .build(),
                CategoryDto.builder()
                        .categoryId(2)
                        .categoryTitle("Books")
                        .imageUrl("http://example.com/books.jpg")
                        .build()
        );

        when(categoryService.findAll()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection").isArray())
                .andExpect(jsonPath("$.collection.length()").value(2))
                .andExpect(jsonPath("$.collection[0].categoryId").value(1))
                .andExpect(jsonPath("$.collection[0].categoryTitle").value("Electronics"))
                .andExpect(jsonPath("$.collection[1].categoryId").value(2))
                .andExpect(jsonPath("$.collection[1].categoryTitle").value("Books"));

        verify(categoryService, times(1)).findAll();
    }

    @Test
    void testFindById_WithValidId_ShouldReturnCategory() throws Exception {
        // Given
        when(categoryService.findById(1)).thenReturn(categoryDto);

        // When & Then
        mockMvc.perform(get("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Electronics"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/image.jpg"));

        verify(categoryService, times(1)).findById(1);
    }

    @Test
    void testFindById_WithValidStringId_ShouldReturnCategory() throws Exception {
        // Given
        when(categoryService.findById(123)).thenReturn(categoryDto);

        // When & Then
        mockMvc.perform(get("/api/categories/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Electronics"));

        verify(categoryService, times(1)).findById(123);
    }

    @Test
    void testSave_WithValidCategory_ShouldReturnSavedCategory() throws Exception {
        // Given
        CategoryDto inputDto = CategoryDto.builder()
                .categoryTitle("New Category")
                .imageUrl("http://example.com/new.jpg")
                .build();

        CategoryDto savedDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("New Category")
                .imageUrl("http://example.com/new.jpg")
                .build();

        when(categoryService.save(any(CategoryDto.class))).thenReturn(savedDto);

        // When & Then
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("New Category"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/new.jpg"));

        verify(categoryService, times(1)).save(any(CategoryDto.class));
    }

    @Test
    void testUpdate_WithoutId_ShouldReturnUpdatedCategory() throws Exception {
        // Given
        CategoryDto updatedDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Updated Category")
                .imageUrl("http://example.com/updated.jpg")
                .build();

        when(categoryService.update(any(CategoryDto.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Updated Category"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/updated.jpg"));

        verify(categoryService, times(1)).update(any(CategoryDto.class));
    }

    @Test
    void testUpdate_WithId_ShouldReturnUpdatedCategory() throws Exception {
        // Given
        CategoryDto updatedDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Updated Category")
                .imageUrl("http://example.com/updated.jpg")
                .build();

        when(categoryService.update(anyInt(), any(CategoryDto.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryTitle").value("Updated Category"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/updated.jpg"));

        verify(categoryService, times(1)).update(anyInt(), any(CategoryDto.class));
    }

    @Test
    void testDeleteById_WithValidId_ShouldReturnTrue() throws Exception {
        // Given
        doNothing().when(categoryService).deleteById(1);

        // When & Then
        mockMvc.perform(delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(categoryService, times(1)).deleteById(1);
    }

    @Test
    void testDeleteById_WithStringId_ShouldReturnTrue() throws Exception {
        // Given
        doNothing().when(categoryService).deleteById(123);

        // When & Then
        mockMvc.perform(delete("/api/categories/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(categoryService, times(1)).deleteById(123);
    }

    @Test
    void testFindAll_WhenServiceReturnsEmptyList_ShouldReturnEmptyCollection() throws Exception {
        // Given
        when(categoryService.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection").isArray())
                .andExpect(jsonPath("$.collection.length()").value(0));

        verify(categoryService, times(1)).findAll();
    }

    @Test
    void testSave_WithCategoryContainingAllFields_ShouldReturnSavedCategory() throws Exception {
        // Given
        CategoryDto complexDto = CategoryDto.builder()
                .categoryTitle("Complex Category")
                .imageUrl("http://example.com/complex.jpg")
                .build();

        CategoryDto savedComplexDto = CategoryDto.builder()
                .categoryId(10)
                .categoryTitle("Complex Category")
                .imageUrl("http://example.com/complex.jpg")
                .build();

        when(categoryService.save(any(CategoryDto.class))).thenReturn(savedComplexDto);

        // When & Then
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(complexDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(10))
                .andExpect(jsonPath("$.categoryTitle").value("Complex Category"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/complex.jpg"));

        verify(categoryService, times(1)).save(any(CategoryDto.class));
    }

    @Test
    void testUpdate_WithIdAndComplexCategory_ShouldReturnUpdatedCategory() throws Exception {
        // Given
        CategoryDto complexUpdateDto = CategoryDto.builder()
                .categoryId(5)
                .categoryTitle("Complex Updated Category")
                .imageUrl("http://example.com/complex-updated.jpg")
                .build();

        when(categoryService.update(anyInt(), any(CategoryDto.class))).thenReturn(complexUpdateDto);

        // When & Then
        mockMvc.perform(put("/api/categories/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(complexUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(5))
                .andExpect(jsonPath("$.categoryTitle").value("Complex Updated Category"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/complex-updated.jpg"));

        verify(categoryService, times(1)).update(anyInt(), any(CategoryDto.class));
    }
}