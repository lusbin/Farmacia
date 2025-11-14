package org.example.repository;

import org.example.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByNit(String nit);
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByNit(String nit);
}
