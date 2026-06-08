package com.example.klebao_static_checker.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class SymbolTableItem {
    int entryNumber;
    String code;
    String lexeme;
    int sizeBefore;
    int sizeAfter;
    @Setter
    String type;
    List<Integer> lines;

    public SymbolTableItem(int entryNumber, String code, String lexeme, int sizeBefore,
                            int sizeAfter, String type, List<Integer> lines) {
        this.entryNumber = entryNumber;
        this.code = code;
        this.lexeme = lexeme;
        this.sizeBefore = sizeBefore;
        this.sizeAfter = sizeAfter;
        this.type = type;
        this.lines = lines;
    }

    @Override
    public String toString() {
        return String.format("[%02d] %s | %s | %d/%d | %s | Lines: %s",
                             entryNumber, code, lexeme, sizeBefore, sizeAfter, type, lines);
    }
}

