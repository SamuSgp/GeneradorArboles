package servicios;

import modelo.Gramatica;
import modelo.ReglaProduccion;

import java.util.ArrayList;
import java.util.List;

public class ServicioDerivacion {

    private Gramatica gramatica;

    public ServicioDerivacion(Gramatica gramatica) {
        this.gramatica = gramatica;
    }

    // Devuelve cada paso de la derivación como un String
    public List<String> derivarIzquierda(String expresionObjetivo) {
        List<String> pasos = new ArrayList<>();
        String actual = gramatica.getSimboloInicial();
        pasos.add(actual);

        for (int intento = 0; intento < 100; intento++) {
            String noTerminal = primerNoTerminal(actual);
            if (noTerminal == null) break;

            ReglaProduccion regla = gramatica.buscarRegla(noTerminal);
            if (regla == null) break;

            String produccionElegida = elegirProduccion(regla, actual, expresionObjetivo, true);
            if (produccionElegida == null) break;

            actual = reemplazarPrimero(actual, noTerminal, produccionElegida);
            pasos.add(actual);

            if (actual.equals(expresionObjetivo)) break;
        }

        return pasos;
    }

    public List<String> derivarDerecha(String expresionObjetivo) {
        List<String> pasos = new ArrayList<>();
        String actual = gramatica.getSimboloInicial();
        pasos.add(actual);

        for (int intento = 0; intento < 100; intento++) {
            String noTerminal = ultimoNoTerminal(actual);
            if (noTerminal == null) break;

            ReglaProduccion regla = gramatica.buscarRegla(noTerminal);
            if (regla == null) break;

            String produccionElegida = elegirProduccion(regla, actual, expresionObjetivo, false);
            if (produccionElegida == null) break;

            actual = reemplazarUltimo(actual, noTerminal, produccionElegida);
            pasos.add(actual);

            if (actual.equals(expresionObjetivo)) break;
        }

        return pasos;
    }

    // Busca el primer no-terminal en la cadena actual
    private String primerNoTerminal(String cadena) {
        String[] simbolos = cadena.trim().split("\\s+");
        for (String simbolo : simbolos) {
            if (gramatica.buscarRegla(simbolo) != null) {
                return simbolo;
            }
        }
        return null;
    }

    // Busca el último no-terminal en la cadena actual
    private String ultimoNoTerminal(String cadena) {
        String[] simbolos = cadena.trim().split("\\s+");
        String ultimo = null;
        for (String simbolo : simbolos) {
            if (gramatica.buscarRegla(simbolo) != null) {
                ultimo = simbolo;
            }
        }
        return ultimo;
    }

    // Elige la producción que más se acerque a la expresión objetivo
    private String elegirProduccion(ReglaProduccion regla, String actual,
                                    String objetivo, boolean izquierda) {
        List<String[]> producciones = regla.getProducciones();
        String noTerminal = regla.getNoTerminalIzquierdo();

        for (String[] produccion : producciones) {
            String produccionTexto = String.join(" ", produccion);
            String resultado;

            if (izquierda) {
                resultado = reemplazarPrimero(actual, noTerminal, produccionTexto);
            } else {
                resultado = reemplazarUltimo(actual, noTerminal, produccionTexto);
            }

            if (objetivo.startsWith(extraerTerminalesIzquierda(resultado)) ||
                objetivo.endsWith(extraerTerminalesDerecha(resultado)) ||
                objetivo.contains(extraerTerminalesCentro(resultado))) {
                return produccionTexto;
            }
        }

        // Si ninguna coincide bien, devuelve la primera como fallback
        return String.join(" ", producciones.get(0));
    }

    // Reemplaza la primera ocurrencia del no-terminal en la cadena
    private String reemplazarPrimero(String cadena, String noTerminal, String reemplazo) {
        String[] simbolos = cadena.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        boolean reemplazado = false;

        for (String simbolo : simbolos) {
            if (!reemplazado && simbolo.equals(noTerminal)) {
                sb.append(reemplazo);
                reemplazado = true;
            } else {
                sb.append(simbolo);
            }
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    // Reemplaza la última ocurrencia del no-terminal en la cadena
    private String reemplazarUltimo(String cadena, String noTerminal, String reemplazo) {
        String[] simbolos = cadena.trim().split("\\s+");
        int ultimaPos = -1;

        for (int i = simbolos.length - 1; i >= 0; i--) {
            if (simbolos[i].equals(noTerminal)) {
                ultimaPos = i;
                break;
            }
        }

        if (ultimaPos == -1) return cadena;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < simbolos.length; i++) {
            if (i == ultimaPos) {
                sb.append(reemplazo);
            } else {
                sb.append(simbolos[i]);
            }
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    private String extraerTerminalesIzquierda(String cadena) {
        String[] simbolos = cadena.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String simbolo : simbolos) {
            if (gramatica.buscarRegla(simbolo) != null) break;
            sb.append(simbolo).append(" ");
        }
        return sb.toString().trim();
    }

    private String extraerTerminalesDerecha(String cadena) {
        String[] simbolos = cadena.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = simbolos.length - 1; i >= 0; i--) {
            if (gramatica.buscarRegla(simbolos[i]) != null) break;
            sb.insert(0, simbolos[i] + " ");
        }
        return sb.toString().trim();
    }

    private String extraerTerminalesCentro(String cadena) {
        String[] simbolos = cadena.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String simbolo : simbolos) {
            if (gramatica.buscarRegla(simbolo) == null) {
                sb.append(simbolo).append(" ");
            }
        }
        return sb.toString().trim();
    }
}