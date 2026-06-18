package com.example.klebao.entity;

public record Token(
        Atom atom,
        String symbolTableIndex,
        Integer line) {
}