package org.example.service;

import org.example.model.Medico;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.patron_de_diseno.comportamental.Observer.*;
import org.springframework.stereotype.Service;

/**
 * Service de aplicación que ORQUESTA el uso del patrón Observer
 * para alertas de stock bajo.
 *
 * No sabe cómo se persiste el stock ni cómo se calculan movimientos:
 * solo recibe los datos ya calculados (producto + stockActual + umbral)
 * y dispara notificaciones a observadores (usuarios / médicos).
 */
@Service
public class AlertaStockService {

    /**
     * Evalúa el stock de un producto y, si está por debajo o igual
     * al umbral mínimo, crea un MonitorStockProducto (Subject)
     * y notifica a los observadores registrados (Usuario, Medico).
     *
     * @param producto     Producto al que pertenece el stock.
     * @param stockActual  Stock actual del producto (ya calculado desde Lote/StockMovimiento).
     * @param umbralMinimo Umbral mínimo a partir del cual se dispara la alerta.
     * @param usuario      Usuario interno a notificar (puede ser null).
     * @param medico       Médico a notificar (puede ser null).
     */
    public void evaluarYNotificarStockBajo(Producto producto,
                                           int stockActual,
                                           int umbralMinimo,
                                           Usuario usuario,
                                           Medico medico) {

        // Protección básica: si no hay producto, no tiene sentido monitorear nada
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null al evaluar stock.");
        }

        // 1) Crear el SUBJECT del patrón Observer:
        //    representa el monitor de stock del producto.
        MonitorStockProducto monitor =
                new MonitorStockProducto(producto, stockActual, umbralMinimo);

        // 2) Crear y registrar OBSERVERS concretos:
        //    - ObservadorAlertaUsuario (usuarios internos)
        //    - ObservadorAlertaMedico (médicos)
        //    Si alguno viene null, simplemente no se registra.
        if (usuario != null) {
            monitor.agregarObservador(new ObservadorAlertaUsuario(usuario));
        }
        if (medico != null) {
            monitor.agregarObservador(new ObservadorAlertaMedico(medico));
        }

        // 3) Actualizar el stock en el subject:
        //    Si stockActual <= umbralMinimo, el MonitorStockProducto
        //    construye un EventoAlerta y se lo envía a TODOS los observers.
        monitor.actualizarStock(stockActual);

        // Nota: no devolvemos nada porque este service solo orquesta la notificación.
        // Si en el futuro quieres testear las alertas recibidas, puedes hacerlo
        // directamente instanciando MonitorStockProducto y ObservadorAlertaXxx en tests unitarios.
    }
}
