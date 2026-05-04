package servicios;

import modelo.NodoArbol;
import modelo.NodoAST;

public class ServicioAST {

    public NodoAST construir(NodoArbol raizArbol) {
        return convertir(raizArbol);
    }

    private NodoAST convertir(NodoArbol nodo) {
        if (nodo == null) return null;

        // Si es hoja (terminal)
        if (nodo.esHoja()) {
            return crearNodoTerminal(nodo.getSimbolo());
        }

        // Si tiene un solo hijo, no agrega información nueva, se salta
        if (nodo.getHijos().size() == 1) {
            return convertir(nodo.getHijos().get(0));
        }

        // Si tiene tres hijos y el del medio es un operador: E -> E + T
        if (nodo.getHijos().size() == 3) {
            NodoArbol hijoIzq = nodo.getHijos().get(0);
            NodoArbol hijoMedio = nodo.getHijos().get(1);
            NodoArbol hijoDer = nodo.getHijos().get(2);

            if (esOperador(hijoMedio.getSimbolo())) {
                NodoAST operacion = new NodoAST("Operacion", hijoMedio.getSimbolo());
                operacion.agregarHijo(convertir(hijoIzq));
                operacion.agregarHijo(convertir(hijoDer));
                return operacion;
            }

            // Caso: F -> ( E ) — se salta los paréntesis y devuelve solo el centro
            if (hijoIzq.getSimbolo().equals("(") && hijoDer.getSimbolo().equals(")")) {
                return convertir(hijoMedio);
            }
        }

        // Caso general: nodo con varios hijos que no es operador ni paréntesis
        NodoAST nodoAST = new NodoAST(nodo.getSimbolo(), "");
        for (NodoArbol hijo : nodo.getHijos()) {
            NodoAST hijoAST = convertir(hijo);
            if (hijoAST != null) {
                nodoAST.agregarHijo(hijoAST);
            }
        }
        return nodoAST;
    }

    private NodoAST crearNodoTerminal(String simbolo) {
        if (esOperador(simbolo)) {
            return new NodoAST("Operador", simbolo);
        }
        if (esNumero(simbolo)) {
            return new NodoAST("Numero", simbolo);
        }
        // Variable o identificador
        return new NodoAST("Identificador", simbolo);
    }

    private boolean esOperador(String simbolo) {
        return simbolo.equals("+") || simbolo.equals("-") ||
               simbolo.equals("*") || simbolo.equals("/");
    }

    private boolean esNumero(String simbolo) {
        try {
            Double.parseDouble(simbolo);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}