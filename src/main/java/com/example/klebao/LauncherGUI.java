package com.example.klebao;

import com.example.klebao.compiler.AnalyzerSINT;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/** Arquivo de configuração da interface gráfica do usuário (GUI) para o projeto.
 * @author Brian Schultz EQ03 * @version 1.0 * @since 2026 */
public class LauncherGUI extends JFrame {

    private File selectedFile;

    public LauncherGUI() {
        setTitle("Klebão2026-1 - Analisador Léxico e Tabela De Símbolos");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("<html><div style='text-align:center;'>" + "<h2 " +
                                        "style='color:#64B4FF;'>Analisador Léxico & Tabela De " +
                                        "Símbolos</h2>" + "<b style='text-align:center; " +
                                        "font-weight:bold; font-size:13px; margin-bottom:0; " +
                                        "padding-bottom:0;'>Código da Equipe:</b> EQ03<br>" + "<b" +
                                        " style='text-align:center; font-weight:bold; " +
                                        "font-size:13px; margin-bottom:0; padding-bottom:0;" +
                                        "'>Componentes:</b><br>" + "Brian Friedrich dos Santos " +
                                        "Schultz; brian.schultz@ucsal.edu.br; 71986300394<br>" +
                                        "Guilherme Ferreira Sampaio; guilhermeferreira.sampaio@ucsal.edu.br;" +
                                        " " +
                                        " <br>" + "Samuel Pereira dos Santos Santana; samuel.santana@ucsal.edu.br<br>" +
                                        "Olivier Teles Leal Araujo; olivier.araujo@ucsal.edu" +
                                                                     ".br" + "</div></html>");
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JLabel instructionsLabel = new JLabel("<html>" + "<div style='text-align:center; " +
                                              "font-weight:bold; font-size:13px; margin-bottom:0;" +
                                              " padding-bottom:0;'>Instruções:</div>" + "<div " +
                                              "style='margin-top:4px;'>" + "1 - Clique em " +
                                              "'Escolher Arquivo' para selecionar um " + "arquivo" +
                                              " de código-fonte com extensão .261<br>" + "2 - " +
                                              "Após selecionar, clique em 'Executar Análise' para" +
                                              " iniciar o processamento.<br>" + " 3 - Os arquivos de saída (.LEX e .TAB) serão gerados na pasta /output do projeto." + "</div>" + "</html>");

        JLabel filePathLabel = new JLabel(" ");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        JButton chooseButton = new JButton("Escolher Arquivo .261");
        JButton runButton = new JButton("Executar Análise");

        filePathLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(filePathLabel);
        buttonsPanel.add(chooseButton);
        buttonsPanel.add(runButton);
        instructionsPanel.add(instructionsLabel);
        centerPanel.add(instructionsPanel);
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
                    AnalyzerSINT.analyze(path);
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
