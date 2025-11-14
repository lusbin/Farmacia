package org.example.repository;

import org.example.model.AuditoriaAcceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaAccesoRepository extends JpaRepository<AuditoriaAcceso, Long> {
    List<AuditoriaAcceso> findByUsuarioId(Long usuarioId);
    List<AuditoriaAcceso> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
}
