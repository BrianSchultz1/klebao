package com.example.klebao.compiler;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class Buffer {
    Logger logger = LoggerFactory.getLogger(Buffer.class);

    public String convertFileToString(String filePath261) {
        try {
            return Files.readString(Path.of(filePath261));
        } catch (IOException e) {
            logger.error("Error reading file {}", filePath261, e);
            return "";
        }
    }

    public String[] splitTextIntoLines(String text) {
        logger.debug("splitTextIntoLines {}", text);
        return text.split("\\r?\\n");
    }
}