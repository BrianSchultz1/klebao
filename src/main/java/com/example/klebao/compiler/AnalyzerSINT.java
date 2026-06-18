package com.example.klebao.compiler;


import com.example.klebao.entity.Atom;
import com.example.klebao.entity.SymbolTableItem;
import com.example.klebao.entity.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyzerSINT {

    public static void main(String[] args) {
        List<Atom> klebaoList = populateKlebaoList();

        String filePath;
        if (args.length > 0) {
            filePath = args[0];
        } else {
            System.err.println("Nenhum arquivo de entrada foi especificado.");
            return;
        }


        Buffer buffer261 = new Buffer();

        AnalyzerLEX analyzerLEX = new AnalyzerLEX(buffer261, filePath);

        String[] lines = analyzerLEX.applyFilters();

        for (String line : lines) {
            System.out.println(line);
        }

        List<Token> tokenList = analyzerLEX.captureValidTokens(lines, klebaoList);

        String[] components = {
                "Brian Friedrich dos Santos Schultz",
                "Guilherme Ferreira Sampaio",
                "Olivier Teles Leal Araujo",
                "Samuel Pereira dos Santos Santana"
        };

        File file = new File(filePath);
        String sourceFileName = file.getName();

        GeneratorFileLEX generatorFileLEX = new GeneratorFileLEX("EQ03", components, sourceFileName);
        generatorFileLEX.generateFileLEX(tokenList);

        SymbolTableGenerator symbolTableGenerator = new SymbolTableGenerator();
        GeneratorFilesTAB generatorFilesTAB = new GeneratorFilesTAB("EQ03", components, sourceFileName);

        Map<String, SymbolTableItem> items = symbolTableGenerator.processTokens(tokenList, klebaoList);

        for (SymbolTableItem item : items.values()) {
            System.out.println(item);
        }

        generatorFilesTAB.generateTabFile(items);

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
