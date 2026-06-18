package com.example.klebao.compiler;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class Buffer {

    public String convertFileToString(String filePath261) {
        try {
            return Files.readString(Path.of(filePath261));
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return "";
        }
    }

    public String[] splitTextIntoLines(String text) {
        return text.split("\\r?\\n");
    }
}