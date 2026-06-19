package com.example.klebao.compiler;


import com.example.klebao.entity.Atom;
import com.example.klebao.entity.Token;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public record GeneratorFileLEX(String teamCode, List<String> components, String sourceFileName) {

    public void generateFileLEX(List<Token> tokens) {

        String formattedSourceName = sourceFileName.contains(".") ? sourceFileName.substring(0, sourceFileName.lastIndexOf('.')) : sourceFileName;

        File outputDir = new File("output");
        if (!outputDir.exists()) outputDir.mkdirs();

        String path = "output/" + formattedSourceName + ".LEX";


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {

            writer.write("Código da Equipe: " + teamCode + "\n");
            writer.write("Componentes:\n");
            for (String componente : components) {
                writer.write("    " + componente + ";\n");
            }

            writer.write("\nRELATÓRIO DA ANÁLISE LÉXICA");
            writer.write("\nTexto fonte analisado: " + sourceFileName + "\n\n");

            for (Token token : tokens) {
                Atom atom = token.atom();
                String lexemeOriginal = atom.lexeme();
                String code = atom.code();
                String index = token.symbolTableIndex() == null ? "-" : token.symbolTableIndex();
                String line = token.line().toString();

                String lexemeDisplay;

                if (lexemeOriginal.length() > 30) {
                    if (lexemeOriginal.startsWith("\"")) {
                        lexemeDisplay = lexemeOriginal.substring(0, 29) + "\"";
                    } else {
                        lexemeDisplay = lexemeOriginal.substring(0, 30);
                    }
                } else {
                    lexemeDisplay = lexemeOriginal;
                }

                writer.write(String.format("Lexeme: %-30s Código: %-7s ÍndiceTabSimb: %-3s Linha: %s" + ".%n", lexemeDisplay, code, index, line));
            }


        } catch (IOException e) {
            System.err.println("Erro ao gerar o arquivo LEX: " + e.getMessage());
        }
    }
}
