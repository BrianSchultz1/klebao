package com.example.klebao_static_checker.compiler;


import com.example.klebao_static_checker.entity.SymbolTableItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public record GeneratorFilesTAB(String teamCode, String[] components, String sourceFileName) {

    public void generateTabFile(Map<String, SymbolTableItem> items) {
        String formattedSourceName = sourceFileName.contains(".")
                ? sourceFileName.substring(0, sourceFileName.lastIndexOf('.'))
                : sourceFileName;

        File outputDir = new File("output");
        if (!outputDir.exists()) outputDir.mkdirs();

        String path = "output/" + formattedSourceName + ".TAB";


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {

            writer.write("Código da Equipe: " + teamCode + "\n");
            writer.write("Componentes:\n");
            for (String componente : components) {
                writer.write("    " + componente + ";\n");
            }

            writer.write("\nRELATÓRIO DA TABELA DE SÍMBOLOS. Texto fonte analisado: " + sourceFileName + "\n\n");

            for (SymbolTableItem item : items.values()) {
                writer.write(String.format("Entrada: %d, Codigo: %s, Lexeme: %s,\n",
                                           item.getEntryNumber(), item.getCode(), item.getLexeme()));
                writer.write(String.format("QtdCharAntesTrunc: %d, QtdCharDepoisTrunc: %d,\n",
                                           item.getSizeBefore(), item.getSizeAfter()));
                writer.write(String.format("TipoSimb: %s, Linhas: %s.\n",
                                           item.getType(), item.getLines()));
                writer.write("-------------------------------------------------------------\n");
            }

            System.out.println("Arquivo .tab gerado com sucesso!");


        } catch (IOException e) {
            System.err.println("Erro ao gerar o arquivo: " + e.getMessage());
        }
    }
}
