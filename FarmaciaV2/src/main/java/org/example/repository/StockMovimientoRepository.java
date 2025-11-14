package org.example.repository;

import org.example.model.StockMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockMovimientoRepository extends JpaRepository<StockMovimiento, Long> {
    List<StockMovimiento> findByLoteId(Long loteId);
    List<StockMovimiento> findByFechaBetween(LocalDate desde, LocalDate hasta);
    List<StockMovimiento> findByTipo(String tipo); // IN, OUT, AJUSTE
}
