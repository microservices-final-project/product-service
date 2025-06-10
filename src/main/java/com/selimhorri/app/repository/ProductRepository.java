package com.selimhorri.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.selimhorri.app.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    @Query("SELECT p FROM Product p WHERE p.category.categoryTitle <> 'Deleted'")
    List<Product> findAllWithoutDeleted();
    
    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.category.categoryTitle <> 'Deleted'")
    Optional<Product> findByIdWithoutDeleted(Integer productId);
}