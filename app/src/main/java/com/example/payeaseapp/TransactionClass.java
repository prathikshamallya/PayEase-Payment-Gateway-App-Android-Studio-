package com.example.payeaseapp;
public class TransactionClass {
    /*private String userID;
    private String deduct;
    private String date;

    public TransactionClass(String userID, String deduct, String date) {
        this.userID = userID;
        this.deduct = deduct;
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public String getDeduct() {
        return deduct;
    }

    public String getDate() {
        return date;
    }*/
    private long id;
    private String senderId, receiverId;
    private String amount;
    private String description;
    private String date;
    private String type;

    public TransactionClass() {
        // Default constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
