package org.example.repository;

import org.example.model.RecetaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecetaItemRepository extends JpaRepository<RecetaItem, Long> {
    List<RecetaItem> findByRecetaId(Long recetaId);
}
