package com.example.klebao_static_checker.compiler;

import com.example.klebao_static_checker.entity.Atom;
import com.example.klebao_static_checker.entity.Token;
import lombok.Data;

import java.util.*;

@Data
public class AnalyzerLEX {

    private final Buffer buffer;
    private final String sourceFilePath;

    private final Map<String, Integer> symbolTable = new HashMap<>();
    private int nextSymbolIndex = 1;
    private String context = "";
    private boolean contextConsumed = false;

    public String[] applyFilters(List<Atom> klebaoList) {
        String[] lines = buffer.splitTextIntoLines(buffer.convertFileToString(sourceFilePath));
        lines = removeComments(lines);
        return lines;
    }

    public String[] removeComments(String[] lines) {
        String[] result = new String[lines.length];
        boolean inBlockComment = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (inBlockComment) {
                int endComment = line.indexOf("*/");
                if (endComment != -1) {
                    line = line.substring(endComment + 2);
                    inBlockComment = false;
                } else {
                    result[i] = "";
                    continue;
                }
            }

            while (true) {
                int startBlock = line.indexOf("/*");
                int startLine = line.indexOf("//");

                if (startBlock != -1 && (startLine == -1 || startBlock < startLine)) {
                    int endBlock = line.indexOf("*/", startBlock + 2);
                    if (endBlock != -1) {
                        line = line.substring(0, startBlock) + line.substring(endBlock + 2);
                    } else {
                        line = line.substring(0, startBlock);
                        inBlockComment = true;
                        break;
                    }
                } else if (startLine != -1) {
                    line = line.substring(0, startLine);
                    break;
                } else {
                    break;
                }
            }

            result[i] = line.trim();
        }

        return result;
    }


    public List<Token> captureValidTokens(String[] lines, List<Atom> klebaoList) {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int currentLine = i + 1;

            List<String> lexemes = splitLexemes(line);
            String previous = "";

            for (String lexeme : lexemes) {
                if (lexeme.isBlank()) continue;

                updateContext(previous);

                Optional<Atom> reserved = klebaoList.stream()
                        .filter(a -> a.lexeme().equals(lexeme))
                        .findFirst();

                Atom atom;

                if (reserved.isPresent()) {
                    atom = reserved.get();
                } else {
                    atom = classifyIdentifier(lexeme, tokens);
                    if (atom == null || atom.code().equals("AIN02")) {
                        previous = lexeme;
                        continue;
                    }
                }

                String code = atom.code();
                String index;
                if (code.startsWith("C")) {
                    index = String.valueOf(getSymbolTableIndex(lexeme));
                } else {
                    index = "-";
                }

                tokens.add(new Token(atom, index, currentLine));
                previous = lexeme;
            }

            if (contextConsumed) {
                context = "";
                contextConsumed = false;
            }
        }
        return tokens;
    }


    private void updateContext(String previous) {
        switch (previous) {
            case "program" -> {
                context = "programName";
                contextConsumed = false;
            }
            case "funcType" -> {
                context = "functionName";
                contextConsumed = false;
            }
            case "integer", "real", "string", "character", "boolean" -> {
                context = "variable";
                contextConsumed = false;
            }
            case "endDeclarations", "endFunctions", "endProgram", "start" -> {
                context = "";
                contextConsumed = false;
            }
        }
    }

    private List<String> splitLexemes(String line) {
        List<String> lexemes = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!current.isEmpty()) {
                    lexemes.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            if (c == '"' || c == '\'') {
                current.append(c);
                i++;
                while (i < line.length()) {
                    char nc = line.charAt(i);
                    current.append(nc);
                    if (nc == c) break;
                    i++;
                }
                lexemes.add(current.toString());
                current.setLength(0);
                continue;
            }

            if (":=<>!#".indexOf(c) != -1 && i + 1 < line.length()) {
                char next = line.charAt(i + 1);
                String possibleSymbol = "" + c + next;
                if (possibleSymbol.matches(":=|==|!=|<=|>=|#")) {
                    if (!current.isEmpty()) {
                        lexemes.add(current.toString());
                        current.setLength(0);
                    }
                    lexemes.add(possibleSymbol);
                    i++;
                    continue;
                }
            }

            if (";:(),[]{}+-*/%".indexOf(c) != -1) {
                if (!current.isEmpty()) {
                    lexemes.add(current.toString());
                    current.setLength(0);
                }
                lexemes.add(Character.toString(c));
                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            lexemes.add(current.toString());
        }

        return lexemes;
    }

    private Atom classifyIdentifier(String lexeme, List<Token> previousTokens) {
        boolean alreadyDeclared = symbolTable.containsKey(lexeme);

        switch (context) {
            case "programName":
                if (!contextConsumed && lexeme.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                    contextConsumed = true;
                    getSymbolTableIndex(lexeme);  // Ensure it enters the table
                    return new Atom(lexeme, "C01");
                }
                break;
            case "functionName":
                if (!contextConsumed && lexeme.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                    contextConsumed = true;
                    getSymbolTableIndex(lexeme);
                    return new Atom(lexeme, "C03");
                }
                break;
            case "variable":
                if (!contextConsumed && lexeme.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    contextConsumed = true;
                    getSymbolTableIndex(lexeme);
                    return new Atom(lexeme, "C02");
                }
                break;
            default:
                break;
        }

        if (alreadyDeclared) {
            Optional<Token> prevToken = previousTokens.stream()
                    .filter(t -> t.atom().lexeme().equals(lexeme))
                    .findFirst();

            if (prevToken.isPresent()) {
                String existingCode = prevToken.get().atom().code();
                return new Atom(lexeme, existingCode);
            }
        }

        if (lexeme.matches("[0-9]+")) return new Atom(lexeme, "C04");
        if (lexeme.matches("[0-9]+\\.[0-9]+(e[+-]?[0-9]+)?")) return new Atom(lexeme, "C05");
        if (lexeme.matches("\"[a-zA-Z0-9 $. _]*\"")) return new Atom(lexeme, "C06");
        if (lexeme.matches("'[a-z]'")) return new Atom(lexeme, "C07");


        return new Atom(lexeme, "C02");
    }


    private int getSymbolTableIndex(String lexeme) {
        if (!symbolTable.containsKey(lexeme)) {
            symbolTable.put(lexeme, nextSymbolIndex++);
        }
        return symbolTable.get(lexeme);
    }

}
