package org.example.patron_de_diseno.creacional.singleton;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class FarmaciaConfigSingleton {

    private static final FarmaciaConfigSingleton INSTANCE = new FarmaciaConfigSingleton();

    private final int maxDiasVigenciaReceta;
    private final int maxPsicotropicosPorReceta;
    private final BigDecimal stockMinimoDefault;

    private FarmaciaConfigSingleton() {
        this.maxDiasVigenciaReceta = 30;
        this.maxPsicotropicosPorReceta = 2;
        this.stockMinimoDefault = BigDecimal.valueOf(5);
    }

    public static FarmaciaConfigSingleton getInstance() {
        return INSTANCE;
    }

    public boolean recetaSigueVigente(LocalDate fechaEmision) {
        return fechaEmision != null &&
                !fechaEmision.isBefore(LocalDate.now().minusDays(maxDiasVigenciaReceta));
    }

    public BigDecimal getStockMinimoDefault() {
        return stockMinimoDefault;
    }
}