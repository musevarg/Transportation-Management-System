package com.aquarian.drivers.ui.receipts;

public class Receipt {

    private String receiptType;
    private String receiptAmount;

    public String getReceiptType() { return receiptType; }
    public void setReceiptType(String receiptType) { this.receiptType = receiptType; }
    public String getReceiptAmount() { return receiptAmount; }
    public void setReceiptAmount(String receiptAmount) { this.receiptAmount = receiptAmount; }

    public Receipt(String receiptType, String receiptAmount) {
        this.receiptType = receiptType;
        this.receiptAmount = receiptAmount;
    }
}
