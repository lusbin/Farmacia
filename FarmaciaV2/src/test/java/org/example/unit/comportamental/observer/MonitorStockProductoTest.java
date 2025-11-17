package org.example.unit.comportamental.observer;

import org.example.model.Medico;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.patron_de_diseno.comportamental.Observer.EventoAlerta;
import org.example.patron_de_diseno.comportamental.Observer.MonitorStockProducto;
import org.example.patron_de_diseno.comportamental.Observer.ObservadorAlertaMedico;
import org.example.patron_de_diseno.comportamental.Observer.ObservadorAlertaUsuario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias del patrón Observer aplicado al monitoreo de stock.
 */
class MonitorStockProductoTest {

    private Usuario buildUsuario() {
        return Usuario.builder()
                .id(10L)
                .username("operador1")
                .nombreCompleto("Operador Farmacia")
                .email("operador@farmacia.com")
                .estado("ACTIVO")
                .build();
    }

    private Medico buildMedico() {
        return Medico.builder()
                .id(20L)
                .nombre("Dr. House")
                .registroProfesional("REG-123")
                .especialidad("Medicina Interna")
                .creadoEn(LocalDateTime.now())
                .build();
    }

    private Producto buildProducto() {
        return Producto.builder()
                .id(1L)
                .creadoEn(LocalDateTime.now())
                .sku("PROD-001")
                .nombre("Amoxicilina 500mg")
                .principioActivo("Amoxicilina")
                .unidad("cápsula")
                .presentacion("Caja x 10")
                .ivaPorcentaje(new BigDecimal("19"))
                .esControlado(false)
                .esOtc(true)
                .build();
    }

    @Test
    void cuandoStockEstaPorEncimaDelUmbral_noSeEnviaNingunaAlerta() {
        // given
        Producto producto = buildProducto();

        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("cajero1")
                .nombreCompleto("Cajero 1")
                .build();

        ObservadorAlertaUsuario obsUsuario = new ObservadorAlertaUsuario(usuario);

        // stock inicial 50, umbral 10
        MonitorStockProducto monitor = new MonitorStockProducto(producto, 50, 10);
        monitor.agregarObservador(obsUsuario);

        // when: bajamos el stock a 30 (sigue > 10)
        monitor.actualizarStock(30);

        // then
        assertThat(obsUsuario.getAlertasRecibidas()).isEmpty();
    }

    @Test
    void cuandoStockCaePorDebajoDelUmbral_observadoresRecibenAlerta() {
        // given
        Producto producto = buildProducto();

        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("cajero1")
                .nombreCompleto("Cajero 1")
                .build();

        Medico medico = Medico.builder()
                .id(20L)
                .nombre("Dr. House")
                .registroProfesional("REG-123")
                .especialidad("Interna")
                .build();

        ObservadorAlertaUsuario obsUsuario = new ObservadorAlertaUsuario(usuario);
        ObservadorAlertaMedico obsMedico = new ObservadorAlertaMedico(medico);

        // stock inicial 15, umbral 10
        MonitorStockProducto monitor = new MonitorStockProducto(producto, 15, 10);
        monitor.agregarObservador(obsUsuario);
        monitor.agregarObservador(obsMedico);

        // when: el stock se reduce a 5 (<= 10) -> debe dispararse alerta
        monitor.actualizarStock(5);

        // then: ambos observers deben haber recibido UNA alerta
        assertThat(obsUsuario.getAlertasRecibidas()).hasSize(1);
        assertThat(obsMedico.getAlertasRecibidas()).hasSize(1);

        EventoAlerta alertaUsuario = obsUsuario.getAlertasRecibidas().get(0);
        EventoAlerta alertaMedico = obsMedico.getAlertasRecibidas().get(0);

        assertThat(alertaUsuario.getTipo())
                .isEqualTo(EventoAlerta.TipoAlerta.STOCK_BAJO);
        assertThat(alertaUsuario.getProducto()).isEqualTo(producto);
        assertThat(alertaUsuario.getMensaje())
                .contains("Stock bajo")
                .contains(producto.getNombre());

        assertThat(alertaMedico.getTipo())
                .isEqualTo(EventoAlerta.TipoAlerta.STOCK_BAJO);
        assertThat(alertaMedico.getProducto()).isEqualTo(producto);
    }
    @Test
    void cuandoStockCaePorDebajoDelUmbral_notificaAUsuarioYMedico() {
        // GIVEN
        Producto producto = buildProducto();
        Usuario usuario = buildUsuario();
        Medico medico = buildMedico();

        int stockInicial = 20;
        int umbralMinimo = 5;

        // Subject
        MonitorStockProducto monitor =
                new MonitorStockProducto(producto, stockInicial, umbralMinimo);

        // Observers
        ObservadorAlertaUsuario obsUsuario = new ObservadorAlertaUsuario(usuario);
        ObservadorAlertaMedico obsMedico = new ObservadorAlertaMedico(medico);

        monitor.agregarObservador(obsUsuario);
        monitor.agregarObservador(obsMedico);

        // WHEN: el stock baja a un valor ≤ umbral
        monitor.actualizarStock(4);

        // THEN: ambos observers reciben una alerta
        assertThat(obsUsuario.getAlertasRecibidas())
                .hasSize(1);
        assertThat(obsMedico.getAlertasRecibidas())
                .hasSize(1);

        EventoAlerta alertaUsuario = obsUsuario.getAlertasRecibidas().get(0);
        EventoAlerta alertaMedico = obsMedico.getAlertasRecibidas().get(0);

        // Tipo de alerta
        assertThat(alertaUsuario.getTipo())
                .isEqualTo(EventoAlerta.TipoAlerta.STOCK_BAJO);
        assertThat(alertaMedico.getTipo())
                .isEqualTo(EventoAlerta.TipoAlerta.STOCK_BAJO);

        // Mismo producto en el evento
        assertThat(alertaUsuario.getProducto()).isEqualTo(producto);
        assertThat(alertaMedico.getProducto()).isEqualTo(producto);

        // Mensaje contiene info de stock bajo
        assertThat(alertaUsuario.getMensaje())
                .contains("Stock bajo")
                .contains(producto.getNombre());
        assertThat(alertaMedico.getMensaje())
                .contains("Stock bajo")
                .contains(producto.getNombre());
    }

    @Test
    void cuandoStockSeMantienePorEncimaDelUmbral_noNotificaAObservadores() {
        // GIVEN
        Producto producto = buildProducto();
        Usuario usuario = buildUsuario();

        int stockInicial = 20;
        int umbralMinimo = 5;

        MonitorStockProducto monitor =
                new MonitorStockProducto(producto, stockInicial, umbralMinimo);

        ObservadorAlertaUsuario obsUsuario = new ObservadorAlertaUsuario(usuario);
        monitor.agregarObservador(obsUsuario);

        // WHEN: actualizamos stock pero sigue > umbral
        monitor.actualizarStock(10);

        // THEN: no se disparan alertas
        assertThat(obsUsuario.getAlertasRecibidas())
                .isEmpty();
    }


}
