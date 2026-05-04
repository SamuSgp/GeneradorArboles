package interfaz;

import modelo.Gramatica;
import modelo.NodoArbol;
import modelo.NodoAST;
import servicios.ServicioArbolDerivacion;
import servicios.ServicioAST;
import servicios.ServicioDerivacion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private Gramatica gramatica;

    private JTextArea areaGramatica;
    private JTextField campoExpresion;
    private JCheckBox checkIzquierda;
    private JCheckBox checkDerecha;
    private JTextArea areaPasos;
    private PanelArbol panelArbol;
    private JTabbedPane pestanas;

    public VentanaPrincipal() {
        gramatica = new Gramatica();
        inicializarVentana();
        construirInterfaz();
        cargarGramaticaPorDefecto();
    }

    private void inicializarVentana() {
        setTitle("Generador de Árboles Sintácticos - CFG");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void construirInterfaz() {
        add(construirPanelIzquierdo(), BorderLayout.WEST);
        add(construirPanelCentral(), BorderLayout.CENTER);
    }

    private JPanel construirPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Gramática
        JLabel lblGramatica = new JLabel("Gramática (BNF):");
        areaGramatica = new JTextArea(8, 20);
        areaGramatica.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollGramatica = new JScrollPane(areaGramatica);

        JButton btnCargarGramatica = new JButton("Cargar Gramática");
        btnCargarGramatica.addActionListener(e -> cargarGramatica());

        // Expresión
        JLabel lblExpresion = new JLabel("Expresión objetivo:");
        campoExpresion = new JTextField();
        campoExpresion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Opciones de derivación
        JLabel lblOpciones = new JLabel("Opciones de Derivación:");
        checkIzquierda = new JCheckBox("Derivación por la Izquierda", true);
        checkDerecha = new JCheckBox("Derivación por la Derecha", false);

        // Solo una opción a la vez
        checkIzquierda.addActionListener(e -> checkDerecha.setSelected(!checkIzquierda.isSelected()));
        checkDerecha.addActionListener(e -> checkIzquierda.setSelected(!checkDerecha.isSelected()));

        JButton btnGenerar = new JButton("Generar Derivación");
        btnGenerar.setBackground(new Color(70, 130, 180));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generarDerivacion());

        panel.add(lblGramatica);
        panel.add(Box.createVerticalStrut(4));
        panel.add(scrollGramatica);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnCargarGramatica);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblExpresion);
        panel.add(Box.createVerticalStrut(4));
        panel.add(campoExpresion);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblOpciones);
        panel.add(checkIzquierda);
        panel.add(checkDerecha);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnGenerar);

        return panel;
    }

    private JPanel construirPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de pasos de derivación
        areaPasos = new JTextArea();
        areaPasos.setEditable(false);
        areaPasos.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPasos = new JScrollPane(areaPasos);
        scrollPasos.setPreferredSize(new Dimension(0, 150));
        scrollPasos.setBorder(BorderFactory.createTitledBorder("Derivación Paso a Paso"));

        // Panel de árboles con pestañas
        panelArbol = new PanelArbol();
        PanelArbol panelAST = new PanelArbol();

        pestanas = new JTabbedPane();
        pestanas.addTab("Árbol de Derivación", panelArbol);
        pestanas.addTab("AST", panelAST);

        panel.add(scrollPasos, BorderLayout.NORTH);
        panel.add(pestanas, BorderLayout.CENTER);

        return panel;
    }

    private void cargarGramaticaPorDefecto() {
        String gramaticaDefecto =
            "E -> E + T | E - T | T\n" +
            "T -> T * F | T / F | F\n" +
            "F -> ( E ) | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z";
        areaGramatica.setText(gramaticaDefecto);
        gramatica.cargarDesdeTexto(gramaticaDefecto);
    }

    private void cargarGramatica() {
        String texto = areaGramatica.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una gramática válida.");
            return;
        }
        gramatica.cargarDesdeTexto(texto);
        JOptionPane.showMessageDialog(this, "Gramática cargada correctamente.");
    }

    private void generarDerivacion() {
        String expresion = campoExpresion.getText().trim().replaceAll("\\s+", " ");
                if (expresion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una expresión objetivo.");
            return;
        }

        boolean porIzquierda = checkIzquierda.isSelected();

        ServicioDerivacion servicioDerivacion = new ServicioDerivacion(gramatica);
        List<String> pasos;

        if (porIzquierda) {
            pasos = servicioDerivacion.derivarIzquierda(expresion);
        } else {
            pasos = servicioDerivacion.derivarDerecha(expresion);
        }

        // Mostrar pasos
        StringBuilder sb = new StringBuilder();
        sb.append("=> ").append(gramatica.getSimboloInicial()).append("\n");
        for (int i = 1; i < pasos.size(); i++) {
            sb.append("=> ").append(pasos.get(i)).append("\n");
        }
        areaPasos.setText(sb.toString());

        // Construir y mostrar árbol de derivación
        ServicioArbolDerivacion servicioArbol = new ServicioArbolDerivacion(gramatica);
        NodoArbol raizArbol = servicioArbol.construir(expresion, porIzquierda);

        PanelArbol panelDerivacion = (PanelArbol) pestanas.getComponentAt(0);
        panelDerivacion.mostrarArbolDerivacion(raizArbol);

        // Construir y mostrar AST
        ServicioAST servicioAST = new ServicioAST();
        NodoAST raizAST = servicioAST.construir(raizArbol);

        PanelArbol panelASTTab = (PanelArbol) pestanas.getComponentAt(1);
        panelASTTab.mostrarAST(raizAST);
    }
}