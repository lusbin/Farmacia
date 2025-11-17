package org.example.patron_de_diseno.comportamental.Observer;

import org.example.model.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * SUBJECT del patrón Observer.
 *
 * Se encarga de monitorear el stock de un Producto agregado
 * y notificar a todos los Observadores cuando el stock
 * cae por debajo de un umbral.
 *
 * (En tu sistema real, el stock viene de Lote/StockMovimiento;
 * aquí lo representamos con un entero para ilustrar el patrón).
 */
public class MonitorStockProducto {

    private final Producto producto;
    private final int umbralMinimo;

    private int stockActual;

    private final List<ObservadorAlerta> observadores = new ArrayList<>();

    public MonitorStockProducto(Producto producto, int stockInicial, int umbralMinimo) {
        this.producto = producto;
        this.stockActual = stockInicial;
        this.umbralMinimo = umbralMinimo;
    }

    // ----- Gestión de observers -----

    public void agregarObservador(ObservadorAlerta observador) {
        observadores.add(observador);
    }

    public void removerObservador(ObservadorAlerta observador) {
        observadores.remove(observador);
    }

    // ----- Actualización de stock -----

    /**
     * Actualiza el stock. Si el stock queda por debajo o igual
     * al umbral, dispara una alerta a todos los observadores.
     */
    public void actualizarStock(int nuevoStock) {
        this.stockActual = nuevoStock;

        if (stockActual <= umbralMinimo) {
            notificarStockBajo();
        }
    }

    private void notificarStockBajo() {
        String msg = "Stock bajo para producto: " + producto.getNombre()
                + " (stock actual=" + stockActual + ", umbral=" + umbralMinimo + ")";

        EventoAlerta evento = new EventoAlerta(
                EventoAlerta.TipoAlerta.STOCK_BAJO,
                msg,
                producto
        );

        for (ObservadorAlerta obs : observadores) {
            obs.notificar(evento);
        }
    }

    public int getStockActual() {
        return stockActual;
    }
}
