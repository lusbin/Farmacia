package org.example.unit.comportamental.Strategy;

import org.example.dto.RecetaCreateDTO;
import org.example.dto.RecetaItemDTO;
import org.example.model.*;
import org.example.repository.MedicoRepository;
import org.example.repository.PacienteRepository;
import org.example.repository.ProductoRepository;
import org.example.repository.RecetaRepository;
import org.example.service.RecetaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.patron_de_diseno.comportamental.Strategy.EstrategiaPediatrica;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Estos tests comprueban la integración del patrón Strategy
 * dentro de RecetaService: la dosis del RecetaItem se ajusta
 * según el contexto clínico (pediátrico / geriátrico / adulto).
 */
@ExtendWith(MockitoExtension.class)
class RecetaServiceStrategyTest {

    @Mock
    RecetaRepository recetaRepository;

    @Mock
    PacienteRepository pacienteRepository;

    @Mock
    MedicoRepository medicoRepository;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    RecetaService recetaService;

    /**
     * Paciente de 5 años -> se debería usar EstrategiaPediatrica,
     * y la dosis final debe contener la marca de ajuste pediátrico.
     */
    @Test
    void crearReceta_pacientePediatrico_dosisSeAjustaConEstrategiaPediatrica() {
        // ------- GIVEN -------
        LocalDate fechaEmision = LocalDate.now().minusDays(2);

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
                .observaciones("prueba pediátrica")
                .items(List.of(itemDTO))
                .build();

        // Paciente niño (~5 años)
        Paciente paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNombre("Niño Test");
        paciente.setFechaNacimiento(LocalDate.now().minusYears(5));
        paciente.setAlergias("Ninguna");

        Medico medico = new Medico();
        medico.setId(2L);

        Producto producto = new Producto();
        producto.setId(100L);
        producto.setPrincipioActivo("Paracetamol");
        producto.setEsControlado(false);
        producto.setEsOtc(true);

        // El repo devuelve lo mismo que recibe, con id simulada
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        when(recetaRepository.save(any(Receta.class))).thenAnswer(inv -> {
            Receta r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        // ------- WHEN -------
        Receta result = recetaService.crearReceta(dto);

        // ------- THEN -------
        ArgumentCaptor<Receta> captor = ArgumentCaptor.forClass(Receta.class);
        verify(recetaRepository).save(captor.capture());

        Receta enviada = captor.getValue();
        assertThat(enviada.getItems()).hasSize(1);
        RecetaItem item = enviada.getItems().get(0);

        // La dosis base era "500 mg", StrategyPediatrica añade sufijos
        assertThat(item.getDosis())
                .contains("500 mg")
                .containsIgnoringCase("pediátrica");

        // sanity check general
        assertThat(result.getId()).isEqualTo(10L);
    }

    /**
     * Paciente de 80 años -> se usa EstrategiaGeriatrica,
     * la dosis debe contener la marca de ajuste geriátrico.
     */
    @Test
    void crearReceta_pacienteGeriatrico_dosisSeAjustaConEstrategiaGeriatrica() {
        // ------- GIVEN -------
        LocalDate fechaEmision = LocalDate.now().minusDays(1);

        RecetaItemDTO itemDTO = RecetaItemDTO.builder()
                .productoId(200L)
                .cantidad(1)
                .dosis("20 mg")
                .frecuencia("cada 12h")
                .duracionDias(5)
                .build();

        RecetaCreateDTO dto = RecetaCreateDTO.builder()
                .pacienteId(2L)
                .medicoId(3L)
                .fechaEmision(fechaEmision)
                .validezDias(10)
                .observaciones("prueba geriátrica")
                .items(List.of(itemDTO))
                .build();

        // Paciente adulto mayor (~80 años)
        Paciente paciente = new Paciente();
        paciente.setId(2L);
        paciente.setNombre("Abuelo Test");
        paciente.setFechaNacimiento(LocalDate.now().minusYears(80));
        paciente.setAlergias("Ninguna");

        Medico medico = new Medico();
        medico.setId(3L);

        Producto producto = new Producto();
        producto.setId(200L);
        producto.setPrincipioActivo("OtroPrincipio");
        producto.setEsControlado(false);
        producto.setEsOtc(false);

        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(3L)).thenReturn(Optional.of(medico));
        when(productoRepository.findById(200L)).thenReturn(Optional.of(producto));
        when(recetaRepository.save(any(Receta.class))).thenAnswer(inv -> {
            Receta r = inv.getArgument(0);
            r.setId(20L);
            return r;
        });

        // ------- WHEN -------
        Receta result = recetaService.crearReceta(dto);

        // ------- THEN -------
        ArgumentCaptor<Receta> captor = ArgumentCaptor.forClass(Receta.class);
        verify(recetaRepository).save(captor.capture());

        Receta enviada = captor.getValue();
        assertThat(enviada.getItems()).hasSize(1);
        RecetaItem item = enviada.getItems().get(0);

        // La dosis debe reflejar la estrategia geriátrica
        assertThat(item.getDosis())
                .contains("20 mg")
                .containsIgnoringCase("geriátrica");

        assertThat(result.getId()).isEqualTo(20L);
    }

    /**
     * Prueba directa de EstrategiaPediatrica.calcularPrecioUnitario:
     *  - MOSTRADOR -> 10% de descuento  (0.90)
     *  - ONLINE    -> 15% de descuento  (0.85)
     *  - precioBase null -> BigDecimal.ZERO
     */
    @Test
    void estrategiaPediatrica_calcularPrecioUnitario_aplicaDescuentosPorCanal() {
        // given
        EstrategiaPediatrica estrategia = new EstrategiaPediatrica();
        Producto producto = new Producto();   // no se usa en la lógica actual
        Paciente paciente = new Paciente();   // tampoco se usa para el precio
        BigDecimal precioBase = new BigDecimal("100.00");

        // when
        BigDecimal precioMostrador = estrategia.calcularPrecioUnitario(
                producto,
                precioBase,
                "MOSTRADOR",
                paciente
        );

        BigDecimal precioOnline = estrategia.calcularPrecioUnitario(
                producto,
                precioBase,
                "ONLINE",
                paciente
        );

        BigDecimal precioNull = estrategia.calcularPrecioUnitario(
                producto,
                null,          // precioBase null
                "ONLINE",
                paciente
        );

        // then
        // MOSTRADOR: 100 * 0.90 = 90.00
        assertThat(precioMostrador).isEqualByComparingTo("90.00");

        // ONLINE: 100 * 0.85 = 85.00
        assertThat(precioOnline).isEqualByComparingTo("85.00");

        // precioBase null -> BigDecimal.ZERO
        assertThat(precioNull).isEqualByComparingTo(BigDecimal.ZERO);
    }




}
