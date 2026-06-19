package com.example.klebao;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;

public class KlebaoApplication {

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        EventQueue.invokeLater(() -> {
            LauncherGUI frame = new LauncherGUI();
            frame.setVisible(true);
        });
    }

}



