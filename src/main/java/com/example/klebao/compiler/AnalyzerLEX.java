package com.example.klebao.compiler;

import com.example.klebao.entity.Atom;
import com.example.klebao.entity.Token;
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

    private static final String INTEGER_REGEX = "[0-9]+";
    private static final String REAL_REGEX = "[0-9]+\\.[0-9]+([eE][+-]?[0-9]+)?";
    private static final String STRING_REGEX = "\"[a-zA-Z0-9 $._]*\"";
    private static final String CHAR_REGEX = "'[a-zA-Z]'";

    public String[] applyFilters() {
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
                boolean shouldSkip = false;

                if (lexeme.isBlank()) {
                    previous = lexeme;
                } else {
                    updateContext(previous);

                    Optional<Atom> reserved = klebaoList.stream()
                            .filter(a -> a.lexeme().equalsIgnoreCase(lexeme))
                            .findFirst();

                    Atom atom;
                    if (reserved.isPresent()) {
                        atom = reserved.get();
                    } else {
                        atom = classifyIdentifier(lexeme, tokens);
                        if ("C02".equals(atom.code())) {
                            previous = lexeme;
                            shouldSkip = true;
                        }
                    }

                    if (!shouldSkip) {
                        String code = atom.code();
                        String index = code.startsWith("C") ? String.valueOf(getSymbolTableIndex(lexeme)) : "-";
                        tokens.add(new Token(atom, index, currentLine));
                        previous = lexeme;
                    }
                }
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

        final Set<String> twoCharOps = Set.of(":=", "==", "!=", "<=", ">=");
        final String singleOps = ";:(),[]{}+-*/%<>!#?";

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
                if (!current.isEmpty()) {
                    lexemes.add(current.toString());
                    current.setLength(0);
                }
                char quote = c;
                StringBuilder literal = new StringBuilder();
                literal.append(quote);
                i++;
                while (i < line.length()) {
                    char nc = line.charAt(i);
                    literal.append(nc);
                    if (nc == quote) {
                        break;
                    }
                    i++;
                }
                lexemes.add(literal.toString());
                continue;
            }

            if (i + 1 < line.length()) {
                String two = "" + c + line.charAt(i + 1);
                if (twoCharOps.contains(two)) {
                    if (!current.isEmpty()) {
                        lexemes.add(current.toString());
                        current.setLength(0);
                    }
                    lexemes.add(two);
                    i++;
                    continue;
                }
            }

            if (singleOps.indexOf(c) != -1) {
                if (!current.isEmpty()) {
                    lexemes.add(current.toString());
                    current.setLength(0);
                }
                lexemes.add(String.valueOf(c));
                continue;
            }

            if (!isValidChar(c)) {
                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            lexemes.add(current.toString());
        }

        return lexemes;
    }

    private boolean isValidChar(char c) {
        if (Character.isLetterOrDigit(c)) return true;
        return switch (c) {
            case ' ', '\t', '$', '_', '.' -> true;
            default -> false;
        };
    }

    private Atom classifyIdentifier(String lexeme, List<Token> previousTokens) {
        boolean alreadyDeclared = symbolTable.containsKey(lexeme);
        String regex = "[a-zA-Z][a-zA-Z0-9]*";

        switch (context) {
            case "programName":
                if (!contextConsumed && lexeme.matches(regex)) {
                    contextConsumed = true;
                    getSymbolTableIndex(lexeme);  // Ensure it enters the table
                    return new Atom(lexeme, "C01");
                }
                break;
            case "functionName":
                if (!contextConsumed && lexeme.matches(regex)) {
                    contextConsumed = true;
                    getSymbolTableIndex(lexeme);
                    return new Atom(lexeme, "C03");
                }
                break;
            case "variable":
                if (!contextConsumed && lexeme.matches(regex)) {
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

        if (lexeme.matches(INTEGER_REGEX)) return new Atom(lexeme, "C04");
        if (lexeme.matches(REAL_REGEX)) return new Atom(lexeme, "C05");
        if (lexeme.matches(STRING_REGEX)) return new Atom(lexeme, "C06");
        if (lexeme.matches(CHAR_REGEX)) return new Atom(lexeme, "C07");


        return new Atom(lexeme, "C02");
    }


    private int getSymbolTableIndex(String lexeme) {
        if (!symbolTable.containsKey(lexeme)) {
            symbolTable.put(lexeme, nextSymbolIndex++);
        }
        return symbolTable.get(lexeme);
    }

}
