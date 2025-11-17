package org.example.unit.creacional.singleton;


import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    RecetaRepository recetaRepository;

    @Mock
    PacienteRepository pacienteRepository;

    @Mock
    MedicoRepository medicoRepository;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    RecetaService recetaService;   // usa los mocks

    @Test
    void crearReceta_conDatosValidos_usaSingletonYGuardaEnRepositorio() {
        // given
        LocalDate fechaEmision = LocalDate.now().minusDays(5); // dentro de los 30 días

        RecetaItemDTO itemDTO = RecetaItemDTO.builder()
                .productoId(100L)
                .cantidad(1)
                .dosis("500 mg")
                .frecuencia("cada 8h")
                .duracionDias(7)
                .build();

        RecetaCreateDTO dto = RecetaCreateDTO.builder()
                .pacienteId(1L)
                .medicoId(2L)
                .fechaEmision(fechaEmision)
                .validezDias(10)
                .observaciones("Tomar con alimentos")
                .items(List.of(itemDTO))
                .build();

        Paciente paciente = new Paciente();
        paciente.setId(1L);

        Medico medico = new Medico();
        medico.setId(2L);

        Producto producto = new Producto();
        producto.setId(100L);

        Receta recetaGuardada = new Receta();
        recetaGuardada.setId(99L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaGuardada);

        // when
        Receta result = recetaService.crearReceta(dto);

        // then
        // se devolvió lo que devolvió el repo
        assertThat(result).isSameAs(recetaGuardada);

        // capturamos la receta que se intentó guardar
        ArgumentCaptor<Receta> captor = ArgumentCaptor.forClass(Receta.class);
        verify(recetaRepository).save(captor.capture());

        Receta enviadaAlRepo = captor.getValue();
        assertThat(enviadaAlRepo.getPaciente()).isSameAs(paciente);
        assertThat(enviadaAlRepo.getMedico()).isSameAs(medico);
        assertThat(enviadaAlRepo.getItems()).hasSize(1);
        RecetaItem item = enviadaAlRepo.getItems().get(0);
        assertThat(item.getProducto()).isSameAs(producto);
        assertThat(item.getCantidad()).isEqualTo(1);
        assertThat(item.getDosis()).isEqualTo("500 mg");
        assertThat(item.getFrecuencia()).isEqualTo("cada 8h");
        assertThat(item.getDuracionDias()).isEqualTo(7);
        assertThat(item.getReceta()).isSameAs(enviadaAlRepo);  // relación bidireccional
    }
/*Singleton de prueba*/
    @Test
    void crearReceta_conFechaVencida_lanzaExcepcionPorReglaDelSingleton() {
        // given: receta emitida hace 40 días -> no debería estar vigente
        RecetaCreateDTO dto = RecetaCreateDTO.builder()
                .medicoId(2L)   // para que no falle antes por médico null
                .fechaEmision(LocalDate.now().minusDays(40))
                .items(List.of()) // lista vacía, no importa para esta prueba
                .build();

        // when / then
        assertThatThrownBy(() -> recetaService.crearReceta(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vencida");  // opcional, pero describe regla sanitaria

        // y muy importante: nunca debería intentar guardar en BD
        verifyNoInteractions(recetaRepository);
    }



}

