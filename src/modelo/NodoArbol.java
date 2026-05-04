package modelo;

import java.util.ArrayList;
import java.util.List;

public class NodoArbol {

    private String simbolo;
    private List<NodoArbol> hijos;

    public NodoArbol(String simbolo) {
        this.simbolo = simbolo;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(NodoArbol hijo) {
        hijos.add(hijo);
    }

    public boolean esHoja() {
        return hijos.isEmpty();
    }

    public String getSimbolo() {
        return simbolo;
    }

    public List<NodoArbol> getHijos() {
        return hijos;
    }

    @Override
    public String toString() {
        return simbolo;
    }
}