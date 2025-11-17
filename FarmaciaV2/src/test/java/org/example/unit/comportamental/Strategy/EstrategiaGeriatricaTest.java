package org.example.unit.comportamental.Strategy;

import org.example.model.Paciente;
import org.example.model.Producto;
import org.example.patron_de_diseno.comportamental.Strategy.EstrategiaGeriatrica;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EstrategiaGeriatricaTest {

    private final EstrategiaGeriatrica estrategia = new EstrategiaGeriatrica();
    private final Producto productoDummy = new Producto(); // no usamos sus campos aquí

    // ---- calcularPrecioUnitario ----

    @Test
    void calcularPrecioUnitario_retornaCeroCuandoPrecioBaseEsNull() {
        BigDecimal result = estrategia.calcularPrecioUnitario(
                productoDummy,
                null,
                "ONLINE",
                null
        );

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calcularPrecioUnitario_aplicaDescuentoCincoPorciento() {
        BigDecimal base = new BigDecimal("100.00");

        BigDecimal result = estrategia.calcularPrecioUnitario(
                productoDummy,
                base,
                "ONLINE",
                null
        );

        // 100 * 0.95 = 95
        assertThat(result).isEqualByComparingTo("95.00");
    }

    // ---- ajustarDosis / calcularEdad ----

    @Test
    void ajustarDosis_retornaMensajeCuandoDosisEsNull() {
        Paciente paciente = new Paciente();
        paciente.setFechaNacimiento(LocalDate.now().minusYears(80)); // edad alta solo por cubrir rama

        String dosis = estrategia.ajustarDosis(
                productoDummy,
                null,
                paciente
        );

        assertThat(dosis).containsIgnoringCase("no definida");
    }

    @Test
    void ajustarDosis_conPacienteMuyMayorYMg_agregaAdvertenciaRenal() {
        Paciente paciente = new Paciente();
        paciente.setFechaNacimiento(LocalDate.now().minusYears(80)); // >= 75

        String dosis = estrategia.ajustarDosis(
                productoDummy,
                "20 mg",
                paciente
        );

        assertThat(dosis)
                .contains("20 mg")
                .containsIgnoringCase("reducción 25%")
                .containsIgnoringCase("geriátrica");
    }

    @Test
    void ajustarDosis_conPacienteMasJovenSoloAgregaSufijoGeriatrico() {
        Paciente paciente = new Paciente();
        paciente.setFechaNacimiento(LocalDate.now().minusYears(60)); // < 75

        String dosis = estrategia.ajustarDosis(
                productoDummy,
                "10 mg",
                paciente
        );

        assertThat(dosis)
                .contains("10 mg")
                .doesNotContain("reducción 25%")
                .containsIgnoringCase("geriátrica");
    }

    @Test
    void ajustarDosis_conPacienteNull_usaEdadCeroYSoloSufijo() {
        String dosis = estrategia.ajustarDosis(
                productoDummy,
                "5 mg",
                null   // dispara rama paciente == null en calcularEdad
        );

        assertThat(dosis)
                .contains("5 mg")
                .doesNotContain("reducción 25%")
                .containsIgnoringCase("geriátrica");
    }

    @Test
    void ajustarDosis_conFechaNacimientoNull_usaEdadCeroYSoloSufijo() {
        Paciente paciente = new Paciente();
        paciente.setFechaNacimiento(null); // dispara rama fechaNacimiento == null

        String dosis = estrategia.ajustarDosis(
                productoDummy,
                "5 mg",
                paciente
        );

        assertThat(dosis)
                .contains("5 mg")
                .doesNotContain("reducción 25%")
                .containsIgnoringCase("geriátrica");
    }
}
