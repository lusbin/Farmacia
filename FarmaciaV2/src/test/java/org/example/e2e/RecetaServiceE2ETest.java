package org.example.e2e;

import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")   // usa tu application-test.yml con H2
@Transactional            // cada test se revierte solo
class RecetaServiceE2ETest {

    @Autowired RecetaService recetaService;

    @Autowired PacienteRepository pacienteRepository;
    @Autowired MedicoRepository medicoRepository;
    @Autowired ProductoRepository productoRepository;
    @Autowired RecetaRepository recetaRepository;

    /**
     * Escenario “feliz”:
     * - Paciente pediátrico (5 años, sin alergias)
     * - Producto OTC
     * - Dosis válida y duración dentro de los límites
     *
     * Debe:
     *  - Pasar por Singleton (vigencia ok)
     *  - Pasar Factory Method (validador de producto)
     *  - Pasar Chain of Responsibility (no alergias, dosis ok, etc.)
     *  - Usar Strategy pediátrica y ajustar la dosis (texto con “pediátrica”)
     *  - Guardar Receta + RecetaItem en H2.
     */
    @Test
    void crearReceta_pediatrica_ajustaDosisYGuardaEnH2() {
        // ---------- GIVEN: datos reales en H2 ----------

        // 1) Paciente de 5 años, sin alergias
        Paciente paciente = Paciente.builder()
                .creadoEn(LocalDateTime.now())
                .docIdentidad("CC-PEQUE-1")
                .nombre("Niño Prueba")
                .fechaNacimiento(LocalDate.now().minusYears(5))
                .sexo("M")
                .alergias("Ninguna")
                .build();
        paciente = pacienteRepository.save(paciente);

        // 2) Médico
        Medico medico = Medico.builder()
                .creadoEn(LocalDateTime.now())
                .nombre("Dra. Pediatra")
                .registroProfesional("REG-123")
                .especialidad("Pediatría")
                .build();
        medico = medicoRepository.save(medico);

        // 3) Producto OTC, principio activo Paracetamol
        Producto producto = Producto.builder()
                .creadoEn(LocalDateTime.now())
                .sku("PEDI-PARA-500")
                .nombre("Paracetamol 500mg pediátrico")
                .principioActivo("Paracetamol")
                .unidad("tableta")
                .presentacion("Caja x 10")
                .ivaPorcentaje(new BigDecimal("19"))
                .esControlado(false)
                .esOtc(true)
                .build();
        producto = productoRepository.save(producto);

        // 4) DTO de item de receta
        RecetaItemDTO itemDTO = RecetaItemDTO.builder()
                .productoId(producto.getId())
                .cantidad(1)
                .dosis("500 mg cada 8h")
                .frecuencia("cada 8h")
                .duracionDias(7) // dentro del rango permitido
                .build();

        // 5) DTO de receta
        RecetaCreateDTO recetaDTO = RecetaCreateDTO.builder()
                .pacienteId(paciente.getId())
                .medicoId(medico.getId())
                .fechaEmision(LocalDate.now().minusDays(1)) // dentro de los 30 días del Singleton
                .validezDias(10)
                .observaciones("Tomar con agua")
                .items(List.of(itemDTO))
                .build();

        // ---------- WHEN: se llama al service real ----------

        Receta recetaGuardada = recetaService.crearReceta(recetaDTO);

        // ---------- THEN: se verifica en H2 y en la entidad devuelta ----------

        // La receta se guardó y tiene ID
        assertThat(recetaGuardada.getId()).isNotNull();

        // Cargamos desde el repositorio para asegurar persistencia
        Receta desdeBD = recetaRepository.findById(recetaGuardada.getId())
                .orElseThrow();

        assertThat(desdeBD.getPaciente().getId()).isEqualTo(paciente.getId());
        assertThat(desdeBD.getMedico().getId()).isEqualTo(medico.getId());
        assertThat(desdeBD.getItems()).hasSize(1);

        RecetaItem item = desdeBD.getItems().get(0);

        // Producto correcto
        assertThat(item.getProducto().getId()).isEqualTo(producto.getId());
        assertThat(item.getCantidad()).isEqualTo(1);

        // STRATEGY pediátrica: la dosis debería tener la marca de ajuste pediátrico
        // (según EstrategiaPediatrica: añade sufijo " (ajustada pediátrica)"
        assertThat(item.getDosis())
                .containsIgnoringCase("pediátrica");

        // Chain y Factory no lanzaron excepción -> flujo correcto
    }
    /* Todo de aqui para abajo es las pruebas e2e del patron Chain of Responsibility*/

    /**
     * Escenario de error:
     * - Paciente alérgico al principio activo del producto.
     *
     * Debe:
     *  - Llegar al ValidadorAlergiasPaciente de la Chain of Responsibility
     *  - Lanzar IllegalArgumentException
     *  - NO guardar la receta en H2.
     */
    @Test
    void crearReceta_fallaPorAlergia_noSeGuardaEnH2() {
        // ---------- GIVEN ----------

        // 1) Paciente alérgico a Paracetamol
        Paciente paciente = Paciente.builder()
                .creadoEn(LocalDateTime.now())
                .docIdentidad("CC-ALERG-1")
                .nombre("Paciente Alérgico")
                .fechaNacimiento(LocalDate.now().minusYears(30))
                .sexo("F")
                .alergias("Paracetamol, Polvo")
                .build();
        paciente = pacienteRepository.save(paciente);

        // 2) Médico
        Medico medico = Medico.builder()
                .creadoEn(LocalDateTime.now())
                .nombre("Dr. General")
                .registroProfesional("REG-456")
                .especialidad("Medicina General")
                .build();
        medico = medicoRepository.save(medico);

        // 3) Producto cuyo principio activo es precisamente Paracetamol
        Producto producto = Producto.builder()
                .creadoEn(LocalDateTime.now())
                .sku("PARA-ALERG-500")
                .nombre("Paracetamol 500mg")
                .principioActivo("Paracetamol")
                .unidad("tableta")
                .presentacion("Caja x 20")
                .ivaPorcentaje(new BigDecimal("19"))
                .esControlado(false)
                .esOtc(true)
                .build();
        producto = productoRepository.save(producto);

        RecetaItemDTO itemDTO = RecetaItemDTO.builder()
                .productoId(producto.getId())
                .cantidad(1)
                .dosis("500 mg cada 8h")
                .frecuencia("cada 8h")
                .duracionDias(5)
                .build();

        RecetaCreateDTO recetaDTO = RecetaCreateDTO.builder()
                .pacienteId(paciente.getId())
                .medicoId(medico.getId())
                .fechaEmision(LocalDate.now()) // vigente
                .validezDias(10)
                .observaciones("Tomar después de comer")
                .items(List.of(itemDTO))
                .build();

        long recetasAntes = recetaRepository.count();

        // ---------- WHEN / THEN ----------

        // Esperamos que la Chain (ValidadorAlergiasPaciente) lance IllegalArgumentException
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        recetaService.crearReceta(recetaDTO))
                .isInstanceOf(IllegalArgumentException.class);
        // Si quisieras, puedes filtrar por mensaje:
        // .hasMessageContainingIgnoringCase("alerg");

        // No se debe haber insertado ninguna receta nueva
        long recetasDespues = recetaRepository.count();
        assertThat(recetasDespues).isEqualTo(recetasAntes);
    }
}
