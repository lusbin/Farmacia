package org.example.unit.creacional.singleton;


import org.example.patron_de_diseno.creacional.singleton.FarmaciaConfigSingleton;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FarmaciaConfigSingletonTest {

    @Test
    void siempreDevuelveLaMismaInstancia() {
        FarmaciaConfigSingleton s1 = FarmaciaConfigSingleton.getInstance();
        FarmaciaConfigSingleton s2 = FarmaciaConfigSingleton.getInstance();

        // Mismo objeto en memoria
        assertThat(s1).isSameAs(s2);
    }

    @Test
    void recetaSigueVigente_CuandoFechaDentroDeLos30Dias() {
        FarmaciaConfigSingleton config = FarmaciaConfigSingleton.getInstance();

        LocalDate hace10Dias = LocalDate.now().minusDays(10);

        boolean vigente = config.recetaSigueVigente(hace10Dias);

        assertThat(vigente).isTrue();
    }

    @Test
    void recetaNoSigueVigente_CuandoFechaEsMasAntiguaQue30Dias() {
        FarmaciaConfigSingleton config = FarmaciaConfigSingleton.getInstance();

        LocalDate hace31Dias = LocalDate.now().minusDays(31);

        boolean vigente = config.recetaSigueVigente(hace31Dias);

        assertThat(vigente).isFalse();
    }

    @Test
    void recetaNoSigueVigente_CuandoFechaEsNull() {
        FarmaciaConfigSingleton config = FarmaciaConfigSingleton.getInstance();

        boolean vigente = config.recetaSigueVigente(null);

        assertThat(vigente).isFalse();
    }

    @Test
    void stockMinimoDefault_EsCincoYNoNegativo() {
        FarmaciaConfigSingleton config = FarmaciaConfigSingleton.getInstance();

        BigDecimal stockMinimo = config.getStockMinimoDefault();

        assertThat(stockMinimo)
                .isEqualByComparingTo("5")
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }
}