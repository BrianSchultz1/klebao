package com.example.klebao.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SymbolTableItem {
    private final int entryNumber;
    private final String code;
    private final String lexeme;
    private int sizeBefore;
    private int sizeAfter;
    
    @Setter
    private String type;

    private List<Integer> lines;

    @Override
    public String toString() {
        return String.format("[%02d] %s | %s | %d/%d | %s | Lines: %s",
                             entryNumber, code, lexeme, sizeBefore, sizeAfter, type, lines);
    }
}