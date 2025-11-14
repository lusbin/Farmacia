package org.example.repository;

import org.example.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByEstado(String estado);
    List<Orden> findByPacienteId(Long pacienteId);

    // Para tests: traer orden con items en un solo query (evita N+1)
    @Query("select o from Orden o left join fetch o.items where o.id = :id")
    Orden findWithItemsById(Long id);
}
