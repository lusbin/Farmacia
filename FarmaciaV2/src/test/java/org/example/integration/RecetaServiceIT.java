package org.example.integration;


import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback  // se limpia al terminar la prueba
class RecetaServiceIT {

    @Autowired
    RecetaService recetaService;

    @Autowired
    PacienteRepository pacienteRepository;

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    ProductoRepository productoRepository;

    @Autowired
    RecetaRepository recetaRepository;

    @Test
    void crearReceta_integrationTest_guardadoCorrecto() {

        // GIVEN - datos reales en la BD H2
        Paciente paciente = pacienteRepository.save(
                Paciente.builder().nombre("Juan").build()
        );

        Medico medico = medicoRepository.save(
                Medico.builder().nombre("Dra. Pérez").build()
        );

        Producto producto = productoRepository.save(
                Producto.builder()
                        .nombre("Ibuprofeno")
                        .esOtc(true)
                        .build()
        );

        RecetaItemDTO itemDTO = RecetaItemDTO.builder()
                .productoId(producto.getId())
                .cantidad(2)
                .dosis("500 mg")
                .frecuencia("cada 12h")
                .duracionDias(5)
                .build();

        RecetaCreateDTO dto = RecetaCreateDTO.builder()
                .pacienteId(paciente.getId())
                .medicoId(medico.getId())
                .fechaEmision(LocalDate.now())
                .validezDias(10)
                .items(List.of(itemDTO))
                .build();

        // WHEN - llamamos al servicio real
        Receta receta = recetaService.crearReceta(dto);

        // THEN - validamos información realmente persistida
        Receta recetaBD = recetaRepository.findById(receta.getId()).orElseThrow();

        assertThat(recetaBD.getPaciente().getNombre()).isEqualTo("Juan");
        assertThat(recetaBD.getMedico().getNombre()).isEqualTo("Dra. Pérez");
        assertThat(recetaBD.getItems()).hasSize(1);

        RecetaItem itemBD = recetaBD.getItems().get(0);

        assertThat(itemBD.getProducto().getNombre()).isEqualTo("Ibuprofeno");
        assertThat(itemBD.getCantidad()).isEqualTo(2);
    }
}