package org.example.repository;

import org.example.model.LibroControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LibroControlRepository extends JpaRepository<LibroControl, Long> {
    List<LibroControl> findByFechaBetween(LocalDate desde, LocalDate hasta);
    List<LibroControl> findByProductoId(Long productoId);
    List<LibroControl> findByPacienteId(Long pacienteId);
}
