package com.example.klebao_static_checker.entity;

public record Token(
        Atom atom,
        String symbolTableIndex,
        Integer line) {
}