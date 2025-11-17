package org.example.unit.comportamental.state;

import org.example.model.Orden;
import org.example.repository.OrdenRepository;
import org.example.service.OrdenStateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenStateServiceTest {

    @Mock
    OrdenRepository ordenRepository;

    @InjectMocks
    OrdenStateService ordenStateService;

    private Orden buildOrdenCreada() {
        Orden o = new Orden();
        o.setId(1L);
        o.setEstado("CREADA");
        o.setTotalNeto(new BigDecimal("150.00"));
        return o;
    }

    @Test
    void aprobarOrden_cambiaEstadoDeCreadaAAprobadaYGuarda() {
        // given
        Orden orden = buildOrdenCreada();
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Orden result = ordenStateService.aprobarOrden(1L);

        // then
        ArgumentCaptor<Orden> captor = ArgumentCaptor.forClass(Orden.class);
        verify(ordenRepository).save(captor.capture());

        Orden enviada = captor.getValue();
        assertThat(enviada.getEstado()).isEqualTo("APROBADA");
        assertThat(result.getEstado()).isEqualTo("APROBADA");
    }

    @Test
    void entregarOrden_cambiaEstadoDeAprobadaAEntregadaYGuarda() {
        // given
        Orden orden = buildOrdenCreada();
        orden.setEstado("APROBADA");
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Orden result = ordenStateService.entregarOrden(1L);

        // then
        assertThat(result.getEstado()).isEqualTo("ENTREGADA");
    }
}
