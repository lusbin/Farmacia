package org.example.repository;

import org.example.model.CompraItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraItemRepository extends JpaRepository<CompraItem, Long> {
    List<CompraItem> findByCompraId(Long compraId);
    List<CompraItem> findByLoteId(Long loteId);
}
