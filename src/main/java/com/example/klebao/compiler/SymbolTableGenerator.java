package com.example.klebao.compiler;

import com.example.klebao.entity.SymbolTableItem;
import com.example.klebao.entity.Token;

import java.util.*;

public class SymbolTableGenerator {

    private final Map<String, SymbolTableItem> table = new LinkedHashMap<>();
    private int counter = 1;

    public Map<String, SymbolTableItem> processTokens(List<Token> tokens) {
        for (Token token : tokens) {
            String atomCode = token.atom().code();

            if (atomCode.startsWith("C")) {
                String originalLexeme = token.atom().lexeme();
                int line = token.line();

                String truncatedLexeme = applyTruncation(originalLexeme, atomCode);
                String symbolType = determineSymbolTypeCorrected(truncatedLexeme, atomCode);
                int sizeBefore = originalLexeme.length();
                int sizeAfter = truncatedLexeme.length();

                String key = originalLexeme + "-" + atomCode;

                if (!table.containsKey(key)) {
                    List<Integer> lines = new ArrayList<>();
                    lines.add(line);
                    table.put(key, new SymbolTableItem(counter++, atomCode, truncatedLexeme, sizeBefore, sizeAfter, symbolType, lines));
                } else {
                    List<Integer> existingLines = table.get(key).getLines();
                    if (existingLines.size() < 5 && !existingLines.contains(line)) {
                        existingLines.add(line);
                    }
                }
            }
        }

        fillVariableTypes(tokens);
        return table;
    }

    private String applyTruncation(String lexeme, String atomCode) {
        int sizeBefore = lexeme.length();

        if (atomCode.equals("C06") || atomCode.equals("C07")) {
            if (sizeBefore > 30) {
                return lexeme.substring(0, 29) + "\"";
            } else {
                return lexeme;
            }
        } else {
            return sizeBefore > 30 ? lexeme.substring(0, 30) : lexeme;
        }
    }

    private String determineSymbolTypeCorrected(String originalLexeme, String readAtomCode) {
        return switch (readAtomCode) {
            case "C01" -> "VD";  // program name
            case "C02" -> "-";   // variable
            case "C03" -> "VD";  // function
            case "C04", "C05" -> {
                if (originalLexeme.contains(".")) {
                    yield "FP";
                } else {
                    yield "IN";
                }
            }
            case "C06" -> "ST";
            case "C07" -> "CH";
            default -> "-";
        };
    }

    public void fillVariableTypes(List<Token> tokens) {
        String currentType = null;

        for (Token token : tokens) {
            String code = token.atom().code();
            String lexeme = token.atom().lexeme();

            switch (lexeme) {
                case "integer" -> currentType = "IN";
                case "string" -> currentType = "ST";
                case "character" -> currentType = "CH";
                case "real" -> currentType = "FP";
                case "boolean" -> currentType = "BL";
                default -> {
                    if (currentType != null && code.equals("C02")) {
                        for (SymbolTableItem item : table.values()) {
                            if (item.getLexeme().equals(applyTruncation(lexeme, code)) && item.getCode().equals(code)) {
                                item.setType(currentType);
                            }
                        }
                    }

                    if (!code.startsWith("C")) {
                        currentType = null;
                    }
                }
            }
        }
    }
}

