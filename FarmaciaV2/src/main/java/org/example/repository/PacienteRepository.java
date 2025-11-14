package org.example.repository;

import org.example.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByDocIdentidad(String docIdentidad);
    boolean existsByDocIdentidad(String docIdentidad);
}