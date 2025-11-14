package org.example.repository;

import org.example.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByFolio(String folio);
    List<Factura> findByEstado(String estado);
    List<Factura> findByFechaEmisionBetween(LocalDate desde, LocalDate hasta);
    List<Factura> findByOrdenId(Long ordenId);
}
