package com.quantumforge.quickdial;


import lombok.Getter;

@Getter
public enum QColor {
    Green ("\u001B[32m"),
    Red ("\u001B[31m"),
    Blue ("\u001B[34m"),
    Yellow ("\u001B[33m");

    private final String ansiCode;

    QColor(String ansiCode){
        this.ansiCode = ansiCode;
    }
}
