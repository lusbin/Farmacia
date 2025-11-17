package org.example.patron_de_diseno.estructural.adapter;

import java.math.BigDecimal;

public interface PasarelaPagoAdapter {

    PagoExternoResponse procesarPago(PagoExternoRequest request);


    /*aqui implemento inner classes dentro de la interface*/
    // DTO genérico de request
    class PagoExternoRequest {
        private final BigDecimal monto;
        private final String moneda;
        private final String referencia;
        private final String metodo;

        public PagoExternoRequest(BigDecimal monto, String moneda, String referencia, String metodo) {
            this.monto = monto;
            this.moneda = moneda;
            this.referencia = referencia;
            this.metodo = metodo;
        }

        public BigDecimal getMonto() { return monto; }
        public String getMoneda() { return moneda; }
        public String getReferencia() { return referencia; }
        public String getMetodo() { return metodo; }
    }

    // DTO genérico de response
    class PagoExternoResponse {
        private final boolean aprobado;
        private final String transaccionId;
        private final String mensaje;

        public PagoExternoResponse(boolean aprobado, String transaccionId, String mensaje) {
            this.aprobado = aprobado;
            this.transaccionId = transaccionId;
            this.mensaje = mensaje;
        }

        public boolean isAprobado() { return aprobado; }
        public String getTransaccionId() { return transaccionId; }
        public String getMensaje() { return mensaje; }
    }
}
