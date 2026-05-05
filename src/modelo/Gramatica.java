package modelo;

import java.util.ArrayList;
import java.util.List;

public class Gramatica {

    private List<ReglaProduccion> reglas;
    private String simboloInicial;

    public Gramatica() {
        this.reglas = new ArrayList<>();
    }

    
    public void cargarDesdeTexto(String texto) {
        reglas.clear();
        String[] lineas = texto.trim().split("\n");

        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;

            // Separa lado izquierdo y lado derecho por "->"
            String[] partes = linea.split("->");
            if (partes.length != 2) continue;

            String noTerminal = partes[0].trim();
            String ladoDerecho = partes[1].trim();

            ReglaProduccion regla = new ReglaProduccion(noTerminal);

            // Separa alternativas por "|"
            String[] alternativas = ladoDerecho.split("\\|");
            for (String alternativa : alternativas) {
                regla.agregarProduccion(alternativa.trim());
            }

            reglas.add(regla);

            // El símbolo inicial es el no-terminal de la primera regla
            if (i == 0) {
                simboloInicial = noTerminal;
            }
        }
    }

    public ReglaProduccion buscarRegla(String noTerminal) {
        for (ReglaProduccion regla : reglas) {
            if (regla.getNoTerminalIzquierdo().equals(noTerminal)) {
                return regla;
            }
        }
        return null;
    }

    public List<ReglaProduccion> getReglas() {
        return reglas;
    }

    public String getSimboloInicial() {
        return simboloInicial;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ReglaProduccion regla : reglas) {
            sb.append(regla.toString()).append("\n");
        }
        return sb.toString();
    }
}