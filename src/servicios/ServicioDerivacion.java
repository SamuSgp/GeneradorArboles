package servicios;

import modelo.Gramatica;
import modelo.ReglaProduccion;

import java.util.ArrayList;
import java.util.List;

public class ServicioDerivacion {

    private Gramatica gramatica;
    private String[] simbolosObjetivo;
    private int posicionActual;

    public ServicioDerivacion(Gramatica gramatica) {
        this.gramatica = gramatica;
    }

    public List<String> derivarIzquierda(String expresionObjetivo) {
        simbolosObjetivo = expresionObjetivo.trim().split("\\s+");
        posicionActual = 0;

        List<String> pasos = new ArrayList<>();
        String actual = gramatica.getSimboloInicial();
        pasos.add(actual);

        for (int intento = 0; intento < 200; intento++) {
            String noTerminal = primerNoTerminal(actual);
            if (noTerminal == null) break;

            ReglaProduccion regla = gramatica.buscarRegla(noTerminal);
            if (regla == null) break;

            String produccionElegida = elegirProduccion(regla, actual);
            if (produccionElegida == null) break;

            String siguiente = reemplazarPrimero(actual, noTerminal, produccionElegida);
            if (siguiente.equals(actual)) break;
            actual = siguiente;
            pasos.add(actual);

            if (actual.equals(expresionObjetivo)) break;
        }

        return pasos;
    }

    public List<String> derivarDerecha(String expresionObjetivo) {
        simbolosObjetivo = expresionObjetivo.trim().split("\\s+");
        posicionActual = simbolosObjetivo.length - 1;

        List<String> pasos = new ArrayList<>();
        String actual = gramatica.getSimboloInicial();
        pasos.add(actual);

        for (int intento = 0; intento < 200; intento++) {
            String noTerminal = ultimoNoTerminal(actual);
            if (noTerminal == null) break;

            ReglaProduccion regla = gramatica.buscarRegla(noTerminal);
            if (regla == null) break;

            String produccionElegida = elegirProduccionDerecha(regla, actual);
            if (produccionElegida == null) break;

            String siguiente = reemplazarUltimo(actual, noTerminal, produccionElegida);
            if (siguiente.equals(actual)) break;
            actual = siguiente;
            pasos.add(actual);

            if (actual.equals(expresionObjetivo)) break;
        }

        return pasos;
    }

    private String elegirProduccion(ReglaProduccion regla, String actual) {
        List<String[]> producciones = regla.getProducciones();
        String noTerminal = regla.getNoTerminalIzquierdo();

        int operadoresActual = contarOperadores(actual);
        int totalOperadores = contarOperadores(String.join(" ", simbolosObjetivo));
        int operadoresFaltantes = totalOperadores - operadoresActual;

        boolean siguienteEsParentesis = posicionActual < simbolosObjetivo.length &&
                                        simbolosObjetivo[posicionActual].equals("(");

        for (String[] produccion : producciones) {
            String produccionTexto = String.join(" ", produccion);
            boolean tieneOperador = contieneOperador(produccion);
            boolean tieneParentesis = contieneParentesis(produccionTexto);
            boolean tieneRecursion = contieneNoTerminal(produccion, noTerminal);

            if (tieneOperador && !tieneParentesis && operadoresFaltantes > 0) {
                // Para derivación izquierda con asociatividad izquierda,
                // el operador que corresponde es el de la posición actual de expansión
                int indiceOperador = operadoresFaltantes - 1;
                String operadorObjetivo = obtenerOperadorEnPosicion(indiceOperador);
                if (operadorObjetivo != null &&
                    produccionTexto.contains(operadorObjetivo) &&
                    esOperadorDelNivel(noTerminal, operadorObjetivo)) {
                    return produccionTexto;
                }
            }

            if (tieneParentesis && siguienteEsParentesis) {
                return produccionTexto;
            }

            if (!tieneOperador && !tieneParentesis && !tieneRecursion && operadoresFaltantes <= 0) {
                if (posicionActual < simbolosObjetivo.length) {
                    while (posicionActual < simbolosObjetivo.length &&
                           esOperador(simbolosObjetivo[posicionActual])) {
                        posicionActual++;
                    }
                    if (posicionActual < simbolosObjetivo.length) {
                        String terminalObjetivo = simbolosObjetivo[posicionActual];
                        posicionActual++;
                        return terminalObjetivo;
                    }
                }
                return produccionTexto;
            }
        }

        for (String[] produccion : producciones) {
            if (!contieneNoTerminal(produccion, noTerminal) &&
                !contieneParentesis(String.join(" ", produccion))) {
                return String.join(" ", produccion);
            }
        }

        return String.join(" ", producciones.get(producciones.size() - 1));
    }

    private String elegirProduccionDerecha(ReglaProduccion regla, String actual) {
        List<String[]> producciones = regla.getProducciones();
        String noTerminal = regla.getNoTerminalIzquierdo();

        int operadoresActual = contarOperadores(actual);
        int totalOperadores = contarOperadores(String.join(" ", simbolosObjetivo));
        int operadoresFaltantes = totalOperadores - operadoresActual;
        boolean objetivoTieneParentesis = contieneParentesis(String.join(" ", simbolosObjetivo));

        for (String[] produccion : producciones) {
            String produccionTexto = String.join(" ", produccion);
            boolean tieneOperador = contieneOperador(produccion);
            boolean tieneParentesis = contieneParentesis(produccionTexto);
            boolean tieneRecursion = contieneNoTerminal(produccion, noTerminal);

            if (tieneOperador && !tieneParentesis && operadoresFaltantes > 0) {
                int indiceOperador = operadoresFaltantes - 1;
                String operadorObjetivo = obtenerOperadorEnPosicion(indiceOperador);
                if (operadorObjetivo != null &&
                    produccionTexto.contains(operadorObjetivo) &&
                    esOperadorDelNivel(noTerminal, operadorObjetivo)) {
                    return produccionTexto;
                }
            }

            if (tieneParentesis && objetivoTieneParentesis && operadoresFaltantes >= 0) {
                return produccionTexto;
            }

            if (!tieneOperador && !tieneParentesis && !tieneRecursion && operadoresFaltantes <= 0) {
                while (posicionActual >= 0 &&
                       esOperador(simbolosObjetivo[posicionActual])) {
                    posicionActual--;
                }
                if (posicionActual >= 0) {
                    String terminalObjetivo = simbolosObjetivo[posicionActual];
                    posicionActual--;
                    return terminalObjetivo;
                }
                return produccionTexto;
            }
        }

        for (String[] produccion : producciones) {
            if (!contieneNoTerminal(produccion, noTerminal) &&
                !contieneParentesis(String.join(" ", produccion))) {
                return String.join(" ", produccion);
            }
        }

        return String.join(" ", producciones.get(producciones.size() - 1));
    }

    private String obtenerOperadorEnPosicion(int posicion) {
        List<String> operadores = new ArrayList<>();
        int profundidad = 0;
        for (String s : simbolosObjetivo) {
            if (s.equals("(")) { profundidad++; continue; }
            if (s.equals(")")) { profundidad--; continue; }
            if (profundidad == 0 && esOperador(s)) {
                operadores.add(s);
            }
        }
        if (posicion >= 0 && posicion < operadores.size()) {
            return operadores.get(posicion);
        }
        return null;
    }

    private boolean esOperadorDelNivel(String noTerminal, String operador) {
        if (noTerminal.equals("E")) {
            return operador.equals("+") || operador.equals("-");
        }
        if (noTerminal.equals("T")) {
            return operador.equals("*") || operador.equals("/");
        }
        return false;
    }

    private boolean contieneOperador(String[] produccion) {
        for (String s : produccion) {
            if (esOperador(s)) return true;
        }
        return false;
    }

    private boolean esOperador(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    private boolean contieneParentesis(String texto) {
        return texto.contains("(") || texto.contains(")");
    }

    private boolean contieneNoTerminal(String[] produccion, String noTerminal) {
        for (String s : produccion) {
            if (s.equals(noTerminal)) return true;
        }
        return false;
    }

    private int contarOperadores(String cadena) {
        int count = 0;
        int profundidad = 0;
        for (String s : cadena.trim().split("\\s+")) {
            if (s.equals("(")) { profundidad++; continue; }
            if (s.equals(")")) { profundidad--; continue; }
            if (profundidad == 0 && esOperador(s)) count++;
        }
        return count;
    }

    private String primerNoTerminal(String cadena) {
        for (String simbolo : cadena.trim().split("\\s+")) {
            if (gramatica.buscarRegla(simbolo) != null) return simbolo;
        }
        return null;
    }

    private String ultimoNoTerminal(String cadena) {
        String ultimo = null;
        for (String simbolo : cadena.trim().split("\\s+")) {
            if (gramatica.buscarRegla(simbolo) != null) ultimo = simbolo;
        }
        return ultimo;
    }

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
            if (i == ultimaPos) sb.append(reemplazo);
            else sb.append(simbolos[i]);
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}