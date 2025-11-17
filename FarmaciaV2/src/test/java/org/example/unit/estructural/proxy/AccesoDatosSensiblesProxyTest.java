package org.example.unit.estructural.proxy;


import org.example.model.LibroControl;
import org.example.model.Receta;
import org.example.patron_de_diseno.estructural.Proxy.AccesoDatosSensibles;
import org.example.patron_de_diseno.estructural.Proxy.AccesoDatosSensiblesProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas UNITARIAS del Proxy:
 * - Verifica que roles permitidos delegan al target.
 * - Verifica que roles NO permitidos lanzan SecurityException.
 */
@ExtendWith(MockitoExtension.class)
class AccesoDatosSensiblesProxyTest {

    @Mock
    AccesoDatosSensibles targetReal; // este simula la implementación real

    // helper para crear el proxy usando el mock
    private AccesoDatosSensibles buildProxy() {
        return new AccesoDatosSensiblesProxy(targetReal);
    }

    @Test
    void obtenerReceta_conRolFarmaceutico_delegaAlTarget() {
        // given
        AccesoDatosSensibles proxy = buildProxy();
        Long recetaId = 10L;
        String rol = "FARMACEUTICO";

        Receta recetaMock = new Receta();
        recetaMock.setId(recetaId);

        when(targetReal.obtenerRecetaPorId(recetaId, rol)).thenReturn(recetaMock);

        // when
        Receta resultado = proxy.obtenerRecetaPorId(recetaId, rol);

        // then
        assertThat(resultado).isSameAs(recetaMock);
        verify(targetReal, times(1))
                .obtenerRecetaPorId(recetaId, rol);
    }

    @Test
    void obtenerLibroControl_conRolMedico_delegaAlTarget() {
        // given
        AccesoDatosSensibles proxy = buildProxy();
        Long libroId = 5L;
        String rol = "MEDICO";

        LibroControl libroMock = new LibroControl();
        libroMock.setId(libroId);

        when(targetReal.obtenerLibroControlPorId(libroId, rol)).thenReturn(libroMock);

        // when
        LibroControl resultado = proxy.obtenerLibroControlPorId(libroId, rol);

        // then
        assertThat(resultado).isSameAs(libroMock);
        verify(targetReal, times(1))
                .obtenerLibroControlPorId(libroId, rol);
    }

    @Test
    void obtenerReceta_conRolNoAutorizado_lanzaSecurityException() {
        // given
        AccesoDatosSensibles proxy = buildProxy();
        Long recetaId = 10L;
        String rol = "CAJERO"; // no permitido según nuestras reglas

        // when / then
        assertThatThrownBy(() -> proxy.obtenerRecetaPorId(recetaId, rol))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Acceso denegado");

        // y MUY importante: nunca debe llamar al target real
        verifyNoInteractions(targetReal);
    }

    @Test
    void obtenerLibroControl_sinRol_lanzaSecurityException() {
        // given
        AccesoDatosSensibles proxy = buildProxy();
        Long libroId = 5L;
        String rol = null;

        // when / then
        assertThatThrownBy(() -> proxy.obtenerLibroControlPorId(libroId, rol))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("rol no informado");

        verifyNoInteractions(targetReal);
    }

    @Test
    void obtenerReceta_conRolAdmin_tambienEsPermitido() {
        // given
        AccesoDatosSensibles proxy = buildProxy();
        Long recetaId = 99L;
        String rol = "ADMIN";

        Receta recetaMock = new Receta();
        recetaMock.setId(recetaId);

        when(targetReal.obtenerRecetaPorId(recetaId, rol)).thenReturn(recetaMock);

        // when
        Receta resultado = proxy.obtenerRecetaPorId(recetaId, rol);

        // then
        assertThat(resultado.getId()).isEqualTo(recetaId);
        verify(targetReal, times(1))
                .obtenerRecetaPorId(recetaId, rol);
    }
}