package com.example.klebao.compiler;


import com.example.klebao.entity.SymbolTableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public record GeneratorFilesTAB(String teamCode, List<String> components, String sourceFileName) {

    static Logger logger = LoggerFactory.getLogger(Buffer.class);


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
                writer.write(String.format("Entrada: %d, Codigo: %s, Lexeme: %s,%n" +
                                           "QtdCharAntesTrunc: %d, QtdCharDepoisTrunc: %d,%n" +
                                           "TipoSimb: %s, Linhas: %s.%n" +
                                           "-------------------------------------------------------------%n",

                item.getEntryNumber(), item.getCode(), item.getLexeme(),
                item.getSizeBefore(), item.getSizeAfter(), item.getType(),  item.getLines()
                ));
            }

            logger.info("Arquivo .TAB gerado com sucesso: {}", path);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
