package com.example.klebao_static_checker;

import com.example.klebao_static_checker.compiler.AnalyzerSINT;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LauncherGUI extends JFrame {

    private File selectedFile;

    public LauncherGUI() {
        setTitle("Klebão - Analisador Léxico & Tabela De Símbolos");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        JLabel headerLabel = new JLabel("<html><div style='text-align:center;'>"
                                      + "<h2 style='color:blue;'>Analisador Léxico & Tabela De Símbolos</h2>"
                                      + "<b style='text-align:center; font-weight:bold; font-size:13px; margin-bottom:0; padding-bottom:0;'>Código da Equipe:</b> EQ03<br>"
                                      + "<b style='text-align:center; font-weight:bold; font-size:13px; margin-bottom:0; padding-bottom:0;'>Componentes:</b><br>"
                                      + "Brian Friedrich dos Santos Schultz; brianschultz320@gmail.com; 71986300394<br>"
                                      + "Guilherme Ferreira Sampaio<br>"
                                      + "Samuel Pereira dos Santos Santana<br>"
                                      + "Olivier Teles Leal Araujo"
                                      + "</div></html>");
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));  // Alinha totalmente à esquerda, sem margem lateral

        JLabel instructionsLabel = new JLabel("<html>"
                                       + "<div style='text-align:center; font-weight:bold; font-size:13px; margin-bottom:0; padding-bottom:0;'>Instruções:</div>"
                                       + "<div style='margin-top:4px;'>"
                                       + "- Clique em 'Escolher Arquivo' para selecionar um " +
                                              "arquivo de código-fonte com extensão .261.<br>"
                                       + "- Após selecionar, clique em 'Executar Análise' para iniciar o processamento.<br>"
                                       + "- Os arquivos de saída (.LEX e .TAB) serão gerados na pasta /output do projeto."
                                       + "</div>"
                                       + "</html>");


        instructionsPanel.add(instructionsLabel);
        centerPanel.add(instructionsPanel);

        JLabel filePathLabel = new JLabel(" ");
        filePathLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(filePathLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton chooseButton = new JButton("Escolher Arquivo .261");
        JButton runButton = new JButton("Executar Análise");

        buttonsPanel.add(chooseButton);
        buttonsPanel.add(runButton);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(buttonsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        chooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file.getName().endsWith(".261")) {
                    selectedFile = file;
                    filePathLabel.setText("Arquivo selecionado: " + file.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(this, "Erro: O arquivo deve ter extensão .261", "Extensão Inválida", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        runButton.addActionListener(e -> {
            if (selectedFile != null) {
                try {
                    String path = selectedFile.getAbsolutePath();
                    AnalyzerSINT.main(new String[]{path});

                    JOptionPane.showMessageDialog(this, "Análise concluída!\nArquivos .LEX e .TAB gerados em /output.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro durante a análise:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um arquivo .261 antes de executar.", "Arquivo não selecionado", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

}
