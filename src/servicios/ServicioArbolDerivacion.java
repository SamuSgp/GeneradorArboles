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
        String objetivo = expresionObjetivo.trim().replaceAll("\\s+", " ");

        ServicioDerivacion sd = new ServicioDerivacion(gramatica);
        List<String> pasos;
        if (izquierda) {
            pasos = sd.derivarIzquierda(objetivo);
        } else {
            pasos = sd.derivarDerecha(objetivo);
        }

        if (pasos.isEmpty()) return new NodoArbol(gramatica.getSimboloInicial());

        NodoArbol raiz = new NodoArbol(gramatica.getSimboloInicial());

        for (int i = 0; i < pasos.size() - 1; i++) {
            String[] anterior = pasos.get(i).trim().split("\\s+");
            String[] siguiente = pasos.get(i + 1).trim().split("\\s+");

            // Encontrar inicio de diferencia desde izquierda
            int posInicio = -1;
            for (int j = 0; j < anterior.length; j++) {
                if (j >= siguiente.length || !anterior[j].equals(siguiente[j])) {
                    posInicio = j;
                    break;
                }
            }
            if (posInicio == -1) posInicio = anterior.length - 1;

            // Encontrar fin de diferencia desde derecha
            int finAnterior = anterior.length - 1;
            int finSiguiente = siguiente.length - 1;
            while (finAnterior > posInicio && finSiguiente > posInicio &&
                   anterior[finAnterior].equals(siguiente[finSiguiente])) {
                finAnterior--;
                finSiguiente--;
            }

            // El símbolo reemplazado debe ser un no-terminal.
            // Si posInicio apunta a un terminal, retroceder hasta el no-terminal más cercano
            int posNoTerminal = posInicio;
            while (posNoTerminal > 0 && gramatica.buscarRegla(anterior[posNoTerminal]) == null) {
                posNoTerminal--;
            }

            // Recalcular finSiguiente según la posición real del no-terminal
            int desplazamiento = posInicio - posNoTerminal;
            int finSiguienteAjustado = finSiguiente + desplazamiento;
            if (finSiguienteAjustado >= siguiente.length) {
                finSiguienteAjustado = siguiente.length - 1;
            }

            String simboloReemplazado = anterior[posNoTerminal];

            int cantNuevos = finSiguienteAjustado - posNoTerminal + 1;
            if (cantNuevos <= 0) continue;

            String[] nuevosSimbolos = new String[cantNuevos];
            for (int k = 0; k < cantNuevos && (posNoTerminal + k) < siguiente.length; k++) {
                nuevosSimbolos[k] = siguiente[posNoTerminal + k];
            }

            if (izquierda) {
                expandirPrimerNodo(raiz, simboloReemplazado, nuevosSimbolos);
            } else {
                expandirUltimoNodo(raiz, simboloReemplazado, nuevosSimbolos);
            }
        }

        return raiz;
    }

    private boolean expandirPrimerNodo(NodoArbol nodo, String simbolo, String[] hijos) {
        if (nodo.esHoja() && nodo.getSimbolo().equals(simbolo)) {
            for (String h : hijos) {
                nodo.agregarHijo(new NodoArbol(h));
            }
            return true;
        }
        for (NodoArbol hijo : nodo.getHijos()) {
            if (expandirPrimerNodo(hijo, simbolo, hijos)) return true;
        }
        return false;
    }

    private boolean expandirUltimoNodo(NodoArbol nodo, String simbolo, String[] hijos) {
        List<NodoArbol> hijosLista = nodo.getHijos();
        for (int i = hijosLista.size() - 1; i >= 0; i--) {
            if (expandirUltimoNodo(hijosLista.get(i), simbolo, hijos)) return true;
        }
        if (nodo.esHoja() && nodo.getSimbolo().equals(simbolo)) {
            for (String h : hijos) {
                nodo.agregarHijo(new NodoArbol(h));
            }
            return true;
        }
        return false;
    }
}