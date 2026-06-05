package com.example.securestoragelabjava.model;

public class AppEntity {
    private final int identifier;
    private final String label;
    private final int value;

    public AppEntity(int identifier, String label, int value) {
        this.identifier = identifier;
        this.label = label;
        this.value = value;
    }

    public int getIdentifier() { return identifier; }
    public String getLabel() { return label; }
    public int getValue() { return value; }
}