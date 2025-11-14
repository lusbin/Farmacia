package org.example.repository;

import org.example.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByPacienteId(Long pacienteId);
    List<Receta> findByMedicoId(Long medicoId);
    List<Receta> findByFechaEmisionBetween(LocalDate desde, LocalDate hasta);
}