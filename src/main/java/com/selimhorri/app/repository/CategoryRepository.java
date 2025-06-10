package com.selimhorri.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.selimhorri.app.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryTitle(String string);

    boolean existsByCategoryTitleIgnoreCase(String categoryTitle);

}
