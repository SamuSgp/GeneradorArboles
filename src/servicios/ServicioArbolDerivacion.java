package servicios;

import modelo.Gramatica;
import modelo.NodoArbol;

import java.util.List;

public class ServicioArbolDerivacion {

    private Gramatica gramatica;

    public ServicioArbolDerivacion(Gramatica gramatica) {
        this.gramatica = gramatica;
    }

    public NodoArbol construir(String expresionObjetivo, boolean izquierda) {
        ServicioDerivacion servicioDerivacion = new ServicioDerivacion(gramatica);

        List<String> pasos;
        if (izquierda) {
            pasos = servicioDerivacion.derivarIzquierda(expresionObjetivo);
        } else {
            pasos = servicioDerivacion.derivarDerecha(expresionObjetivo);
        }

        NodoArbol raiz = new NodoArbol(gramatica.getSimboloInicial());
        construirRecursivo(raiz, expresionObjetivo);
        return raiz;
    }

    // Expande recursivamente cada nodo no-terminal
    private void construirRecursivo(NodoArbol nodo, String objetivo) {
        String simbolo = nodo.getSimbolo();

        if (gramatica.buscarRegla(simbolo) == null) {
            // Es terminal, no se expande
            return;
        }

        String[] produccion = elegirProduccionParaObjetivo(simbolo, objetivo);
        if (produccion == null) return;

        for (String s : produccion) {
            NodoArbol hijo = new NodoArbol(s);
            nodo.agregarHijo(hijo);
        }

        // Distribuir el objetivo entre los hijos no-terminales
        distribuirYExpandir(nodo.getHijos(), objetivo);
    }

    private void distribuirYExpandir(List<NodoArbol> hijos, String objetivo) {
        String[] simbolosObjetivo = objetivo.trim().split("\\s+");
        int pos = 0;

        for (NodoArbol hijo : hijos) {
            if (gramatica.buscarRegla(hijo.getSimbolo()) == null) {
                // Es terminal, avanzar posición
                pos++;
            } else {
                // Es no-terminal, calcular cuántos terminales le corresponden
                StringBuilder subObjetivo = new StringBuilder();
                int terminalesNecesarios = contarTerminalesEsperados(hijo.getSimbolo(), simbolosObjetivo, pos);

                for (int i = pos; i < pos + terminalesNecesarios && i < simbolosObjetivo.length; i++) {
                    subObjetivo.append(simbolosObjetivo[i]).append(" ");
                }

                construirRecursivo(hijo, subObjetivo.toString().trim());
                pos += terminalesNecesarios;
            }
        }
    }

    // Estima cuántos terminales del objetivo consume este no-terminal
    private int contarTerminalesEsperados(String noTerminal, String[] objetivoSimbolos, int desde) {
        if (desde >= objetivoSimbolos.length) return 0;

        // Para no-terminales conocidos de expresiones aritméticas
        // se hace una estimación básica según el contexto
        int restantes = objetivoSimbolos.length - desde;

        // Si es el último no-terminal, toma todo lo que queda
        return restantes > 0 ? restantes : 1;
    }

    private String[] elegirProduccionParaObjetivo(String noTerminal, String objetivo) {
        if (gramatica.buscarRegla(noTerminal) == null) return null;

        List<String[]> producciones = gramatica.buscarRegla(noTerminal).getProducciones();
        String[] objetivoSimbolos = objetivo.trim().split("\\s+");

        for (String[] produccion : producciones) {
            if (produccionCoincideConObjetivo(produccion, objetivoSimbolos)) {
                return produccion;
            }
        }

        // Fallback: primera producción
        return producciones.get(0);
    }

    private boolean produccionCoincideConObjetivo(String[] produccion, String[] objetivo) {
        // Verificar si los terminales de la producción están en el objetivo
        for (String simbolo : produccion) {
            if (gramatica.buscarRegla(simbolo) == null) {
                // Es terminal, verificar si está en el objetivo
                boolean encontrado = false;
                for (String s : objetivo) {
                    if (s.equals(simbolo)) {
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) return false;
            }
        }
        return true;
    }
}