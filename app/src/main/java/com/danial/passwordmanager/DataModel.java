package com.danial.passwordmanager;

public class DataModel {
    private String line1;
    private String line2;
    private String documentId;

    public DataModel(String line1, String line2, String documentId) {
        this.line1 = line1;
        this.line2 = line2;
        this.documentId = documentId;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getDocumentId() {
        return documentId;
    }
}