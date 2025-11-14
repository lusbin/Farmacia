package org.example.repository;

import org.example.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByProductoId(Long productoId);
    List<Lote> findByFechaVencimientoBefore(LocalDate fecha);
    List<Lote> findByProductoIdOrderByFechaVencimientoAsc(Long productoId);


}
