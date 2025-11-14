package org.example.repository;

import org.example.model.OrdenItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {
    List<OrdenItem> findByOrdenId(Long ordenId);
    List<OrdenItem> findByProductoId(Long productoId);
}
