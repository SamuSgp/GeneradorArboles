package interfaz;

import modelo.NodoArbol;
import modelo.NodoAST;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelArbol extends JPanel {

    private NodoArbol raizArbol;
    private NodoAST raizAST;
    private boolean mostrandoAST;

    private static final int ANCHO_NODO = 40;
    private static final int ALTO_NIVEL = 60;
    private static final int MARGEN = 20;

    public PanelArbol() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Árbol"));
    }

    public void mostrarArbolDerivacion(NodoArbol raiz) {
        this.raizArbol = raiz;
        this.raizAST = null;
        this.mostrandoAST = false;
        setBorder(BorderFactory.createTitledBorder("Árbol de Derivación"));
        repaint();
    }

    public void mostrarAST(NodoAST raiz) {
        this.raizAST = raiz;
        this.raizArbol = null;
        this.mostrandoAST = true;
        setBorder(BorderFactory.createTitledBorder("Árbol Sintáctico Abstracto (AST)"));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (mostrandoAST && raizAST != null) {
            int anchoTotal = calcularAnchoAST(raizAST) * (ANCHO_NODO + MARGEN);
            int x = Math.max(getWidth() / 2, anchoTotal / 2);
            dibujarAST(g2, raizAST, x, 40, getWidth());
        } else if (!mostrandoAST && raizArbol != null) {
            int anchoTotal = calcularAnchoArbol(raizArbol) * (ANCHO_NODO + MARGEN);
            int x = Math.max(getWidth() / 2, anchoTotal / 2);
            dibujarArbol(g2, raizArbol, x, 40, getWidth());
        }
    }

    // ---- Dibujo del árbol de derivación ----

    private void dibujarArbol(Graphics2D g, NodoArbol nodo, int x, int y, int anchoDisponible) {
        dibujarNodoArbol(g, nodo.getSimbolo(), x, y);

        List<NodoArbol> hijos = nodo.getHijos();
        if (hijos.isEmpty()) return;

        int anchoHijo = anchoDisponible / hijos.size();
        int xInicio = x - anchoDisponible / 2 + anchoHijo / 2;

        for (int i = 0; i < hijos.size(); i++) {
            int xHijo = xInicio + i * anchoHijo;
            int yHijo = y + ALTO_NIVEL;

            g.setColor(Color.DARK_GRAY);
            g.drawLine(x, y + ANCHO_NODO / 2, xHijo, yHijo - ANCHO_NODO / 2);

            dibujarArbol(g, hijos.get(i), xHijo, yHijo, anchoHijo);
        }
    }

    private void dibujarNodoArbol(Graphics2D g, String texto, int x, int y) {
        int radio = ANCHO_NODO / 2;
        g.setColor(new Color(173, 216, 230));
        g.fillOval(x - radio, y - radio, ANCHO_NODO, ANCHO_NODO);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x - radio, y - radio, ANCHO_NODO, ANCHO_NODO);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        int tx = x - fm.stringWidth(texto) / 2;
        int ty = y + fm.getAscent() / 2 - 1;
        g.drawString(texto, tx, ty);
    }

    // ---- Dibujo del AST ----

    private void dibujarAST(Graphics2D g, NodoAST nodo, int x, int y, int anchoDisponible) {
        String etiqueta = nodo.getValor().isEmpty() ? nodo.getTipo() : nodo.getValor();
        dibujarNodoAST(g, etiqueta, nodo.getTipo(), x, y);

        List<NodoAST> hijos = nodo.getHijos();
        if (hijos.isEmpty()) return;

        int anchoHijo = anchoDisponible / hijos.size();
        int xInicio = x - anchoDisponible / 2 + anchoHijo / 2;

        for (int i = 0; i < hijos.size(); i++) {
            int xHijo = xInicio + i * anchoHijo;
            int yHijo = y + ALTO_NIVEL;

            g.setColor(Color.DARK_GRAY);
            g.drawLine(x, y + ANCHO_NODO / 2, xHijo, yHijo - ANCHO_NODO / 2);

            dibujarAST(g, hijos.get(i), xHijo, yHijo, anchoHijo);
        }
    }

    private void dibujarNodoAST(Graphics2D g, String texto, String tipo, int x, int y) {
        int radio = ANCHO_NODO / 2;
        Color color = obtenerColorPorTipo(tipo);

        g.setColor(color);
        g.fillOval(x - radio, y - radio, ANCHO_NODO, ANCHO_NODO);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x - radio, y - radio, ANCHO_NODO, ANCHO_NODO);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        int tx = x - fm.stringWidth(texto) / 2;
        int ty = y + fm.getAscent() / 2 - 1;
        g.drawString(texto, tx, ty);
    }

    private Color obtenerColorPorTipo(String tipo) {
        switch (tipo) {
            case "Operacion": return new Color(255, 200, 100);
            case "Numero":    return new Color(144, 238, 144);
            case "Identificador": return new Color(255, 182, 193);
            default:          return new Color(200, 200, 200);
        }
    }

    // ---- Cálculo de anchos para centrar nodos ----

    private int calcularAnchoArbol(NodoArbol nodo) {
        if (nodo.getHijos().isEmpty()) return 1;
        int total = 0;
        for (NodoArbol hijo : nodo.getHijos()) {
            total += calcularAnchoArbol(hijo);
        }
        return total;
    }

    private int calcularAnchoAST(NodoAST nodo) {
        if (nodo.getHijos().isEmpty()) return 1;
        int total = 0;
        for (NodoAST hijo : nodo.getHijos()) {
            total += calcularAnchoAST(hijo);
        }
        return total;
    }
}