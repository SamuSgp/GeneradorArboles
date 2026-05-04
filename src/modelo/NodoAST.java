package modelo;

import java.util.ArrayList;
import java.util.List;

public class NodoAST {

    private String tipo;
    private String valor;
    private List<NodoAST> hijos;

    public NodoAST(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(NodoAST hijo) {
        hijos.add(hijo);
    }

    public boolean esHoja() {
        return hijos.isEmpty();
    }

    public String getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public List<NodoAST> getHijos() {
        return hijos;
    }

    @Override
    public String toString() {
        if (valor != null && !valor.isEmpty()) {
            return tipo + "(" + valor + ")";
        }
        return tipo;
    }
}