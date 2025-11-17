package org.example.unit.creacional.factory_method;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class RecetaServiceTestFactory {


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
    void crearReceta_conDatosValidos_usaSingletonYFactoryYGuardaEnRepositorio() {
        // given
        LocalDate fechaEmision = LocalDate.now().minusDays(5); // dentro de los 30 días

        RecetaItemDTO itemDTO = RecetaItemDTO.builder()
                .productoId(100L)
                .cantidad(1)
                .dosis("500 mg")
                .frecuencia("cada 8h")
                .duracionDias(7)          // <= 30 -> válido para controlados
                .build();

        RecetaCreateDTO dto = RecetaCreateDTO.builder()
                .pacienteId(1L)           // requerido por validador de controlados
                .medicoId(2L)             // requerido por validador de controlados
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
        producto.setEsControlado(true);   // <- activamos el validador de CONTROLADOS
        producto.setEsOtc(false);

        Receta recetaGuardada = new Receta();
        recetaGuardada.setId(99L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaGuardada);

        // when
        Receta result = recetaService.crearReceta(dto);

        // then
        assertThat(result).isSameAs(recetaGuardada);

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
}
