package modelo;

import java.util.ArrayList;
import java.util.List;

public class ReglaProduccion {

    private String noTerminalIzquierdo;
    private List<String[]> producciones;

    public ReglaProduccion(String noTerminalIzquierdo) {
        this.noTerminalIzquierdo = noTerminalIzquierdo;
        this.producciones = new ArrayList<>();
    }

    public void agregarProduccion(String produccion) {
        // "E + T" -> ["E", "+", "T"]
        String[] simbolos = produccion.trim().split("\\s+");
        producciones.add(simbolos);
    }

    public String getNoTerminalIzquierdo() {
        return noTerminalIzquierdo;
    }

    public List<String[]> getProducciones() {
        return producciones;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < producciones.size(); i++) {
            sb.append(noTerminalIzquierdo).append(" -> ");
            sb.append(String.join(" ", producciones.get(i)));
            if (i < producciones.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}