package com.example.klebao.compiler;


import com.example.klebao.entity.Atom;
import com.example.klebao.entity.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record GeneratorFileLEX(String teamCode, List<String> components, String sourceFileName) {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorFileLEX.class);

    public void generateFileLEX(List<Token> tokens) {

        createOutputDirectory();

        Path outputFile = Path.of("output", getFormattedSourceName() + ".LEX");

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            writeHeader(writer);

            for (Token token : tokens) {
                writer.write(buildTokenLine(token));
            }

        } catch (IOException e) {
            logger.error("Erro ao gerar o arquivo LEX: {}", e.getMessage());
        }
    }

    private void createOutputDirectory() {
        File outputDir = new File("output");

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    private String formatLexeme(String lexeme) {
        if (lexeme.length() <= 30) {
            return lexeme;
        }

        if (lexeme.startsWith("\"")) {
            return lexeme.substring(0, 29) + "\"";
        }

        return lexeme.substring(0, 30);
    }

    private void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("Código da Equipe: " + teamCode + "\n");
        writer.write("Componentes:\n");

        for (String component : components) {
            writer.write("    " + component + ";\n");
        }

        writer.write("\nRELATÓRIO DA ANÁLISE LÉXICA");
        writer.write("\nTexto fonte analisado: " + sourceFileName + "\n\n");
    }

    private String getFormattedSourceName() {
        if (!sourceFileName.contains(".")) {
            return sourceFileName;
        }

        return sourceFileName.substring(
                0,
                sourceFileName.lastIndexOf('.')
        );
    }

    private String buildTokenLine(Token token) {
        Atom atom = token.atom();



        return String.format(
                "Lexeme: %-30s Código: %-7s ÍndiceTabSimb: %-3s Linha: %s.%n",
                formatLexeme(atom.lexeme()),
                atom.code(),
                token.symbolTableIndex() == null ? "-" : token.symbolTableIndex(),
                token.line()
        );
    }
}
