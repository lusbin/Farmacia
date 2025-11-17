package org.example.patron_de_diseno.comportamental.Strategy;

import org.example.model.Paciente;

import java.time.LocalDate;
import java.time.Period;

public class EstrategiaContextoClinicoFactory {

    public static EstrategiaContextoClinico obtenerEstrategia(Paciente paciente) {

        if (paciente == null || paciente.getFechaNacimiento() == null) {
            // si no sabemos edad, usamos adulto por defecto
            return new EstrategiaAdulto();
        }

        int edad = Period.between(paciente.getFechaNacimiento(), LocalDate.now()).getYears();

        if (edad < 12) {
            return new EstrategiaPediatrica();
        }
        if (edad >= 65) {
            return new EstrategiaGeriatrica();
        }
        return new EstrategiaAdulto();
    }
}
