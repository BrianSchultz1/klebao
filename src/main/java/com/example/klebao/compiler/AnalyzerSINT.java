package com.example.klebao.compiler;


import com.example.klebao.entity.Atom;
import com.example.klebao.entity.SymbolTableItem;
import com.example.klebao.entity.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyzerSINT {
    private AnalyzerSINT() {}

    private static final String TEAM_CODE = "EQ03";

    private static final List<String> COMPONENTS =
            List.of(
                    "Brian Friedrich dos Santos Schultz",
                    "Guilherme Ferreira Sampaio",
                    "Olivier Teles Leal Araujo",
                    "Samuel Pereira dos Santos Santana"
            );


    public static void analyze(String filePath) {

        List<Token> tokenList = executeLexAnalysis(filePath);

        generateLexFile(tokenList, filePath);

        generateSymbolTable(tokenList, filePath);
    }


    private static void generateSymbolTable(List<Token> tokenList, String filePath
    ) {
        SymbolTableGenerator symbolTableGenerator = new SymbolTableGenerator();
        Map<String, SymbolTableItem> items = symbolTableGenerator.processTokens(tokenList);
        String sourceFileName = new File(filePath).getName();

        for (SymbolTableItem item : items.values()) {
            System.out.println(item);
        }

        GeneratorFilesTAB generatorFilesTAB =
                new GeneratorFilesTAB(
                        TEAM_CODE,
                        COMPONENTS,
                        sourceFileName
                );

        generatorFilesTAB.generateTabFile(items);
    }

    private static void generateLexFile(List<Token> tokenList, String filePath) {
        String sourceFileName = new File(filePath).getName();

        GeneratorFileLEX generatorFileLEX = new GeneratorFileLEX(TEAM_CODE, COMPONENTS, sourceFileName);
        generatorFileLEX.generateFileLEX(tokenList);

    }

    private static List<Token> executeLexAnalysis(String filePath) {
        Buffer buffer261 = new Buffer();

        AnalyzerLEX analyzerLEX = new AnalyzerLEX(buffer261, filePath);

        String[] lines = analyzerLEX.applyFilters();

        for (String line : lines) {
            System.out.println(line);
        }

        return analyzerLEX.captureValidTokens(lines, populateKlebaoList());
    }


    public static List<Atom> populateKlebaoList() {
        List<Atom> list = new ArrayList<>();

        list.add(new Atom("boolean", "A01"));
        list.add(new Atom("break", "A02"));
        list.add(new Atom("character", "A03"));
        list.add(new Atom("declarations", "A04"));
        list.add(new Atom("else", "A05"));
        list.add(new Atom("endDeclarations", "A06"));
        list.add(new Atom("endFunction", "A07"));
        list.add(new Atom("endFunctions", "A08"));
        list.add(new Atom("endIf", "A09"));
        list.add(new Atom("endProgram", "A10"));
        list.add(new Atom("endWhile", "A11"));
        list.add(new Atom("false", "A12"));
        list.add(new Atom("functions", "A13"));
        list.add(new Atom("funcType", "A14"));
        list.add(new Atom("if", "A15"));
        list.add(new Atom("integer", "A16"));
        list.add(new Atom("paramType", "A17"));
        list.add(new Atom("print", "A18"));
        list.add(new Atom("program", "A19"));
        list.add(new Atom("real", "A20"));
        list.add(new Atom("return", "A21"));
        list.add(new Atom("string", "A22"));
        list.add(new Atom("true", "A23"));
        list.add(new Atom("varType", "A24"));
        list.add(new Atom("void", "A25"));
        list.add(new Atom("while", "A26"));

        list.add(new Atom(";", "B01"));
        list.add(new Atom(",", "B02"));
        list.add(new Atom(":", "B03"));
        list.add(new Atom(":=", "B04"));
        list.add(new Atom("?", "B05"));
        list.add(new Atom("(", "B06"));
        list.add(new Atom(")", "B07"));
        list.add(new Atom("[", "B08"));
        list.add(new Atom("]", "B09"));
        list.add(new Atom("{", "B10"));
        list.add(new Atom("}", "B11"));
        list.add(new Atom("+", "B12"));
        list.add(new Atom("-", "B13"));
        list.add(new Atom("*", "B14"));
        list.add(new Atom("/", "B15"));
        list.add(new Atom("%", "B16"));
        list.add(new Atom("==", "B17"));
        list.add(new Atom("!", "B18"));
        list.add(new Atom("#", "B18"));
        list.add(new Atom("<", "B19"));
        list.add(new Atom("<=", "B20"));
        list.add(new Atom(">", "B21"));
        list.add(new Atom(">=", "B22"));

        list.add(new Atom("programName", "C01"));
        list.add(new Atom("variable", "C02"));
        list.add(new Atom("functionName", "C03"));
        list.add(new Atom("intConst", "C04"));
        list.add(new Atom("realConst", "C05"));
        list.add(new Atom("stringConst", "C06"));
        list.add(new Atom("charConst", "C07"));

        return list;
    }
}

