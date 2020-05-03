package com.aquarian.drivers.ui.vehicle;

public class ListItem {
    private String header;
    private String value;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ListItem(String header, String value) {
        this.header = header;
        this.value = value;
    }
}
