package servicios;

import modelo.NodoArbol;
import modelo.NodoAST;

public class ServicioAST {

    public NodoAST construir(NodoArbol raizArbol) {
        return convertir(raizArbol);
    }

    private NodoAST convertir(NodoArbol nodo) {
        if (nodo == null) return null;

        
        if (nodo.esHoja()) {
            return crearNodoTerminal(nodo.getSimbolo());
        }

        
        if (nodo.getHijos().size() == 1) {
            return convertir(nodo.getHijos().get(0));
        }

        
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

            
            if (hijoIzq.getSimbolo().equals("(") && hijoDer.getSimbolo().equals(")")) {
                return convertir(hijoMedio);
            }
        }

        
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