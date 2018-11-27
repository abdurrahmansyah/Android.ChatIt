package com.dev.ridho.androidchatit.Model;

public class Messages {
    private String userPhoneFrom;
    private String userPhoneTo;
    private String messageType;
    private String text;
    private String time;

    public Messages() {
    }

    public Messages(String userPhoneFrom, String userPhoneTo, String messageType, String text, String time) {
        this.userPhoneFrom = userPhoneFrom;
        this.userPhoneTo = userPhoneTo;
        this.messageType = messageType;
        this.text = text;
        this.time = time;
    }

    public String getUserPhoneFrom() {
        return userPhoneFrom;
    }

    public void setUserPhoneFrom(String userPhoneFrom) {
        this.userPhoneFrom = userPhoneFrom;
    }

    public String getUserPhoneTo() {
        return userPhoneTo;
    }

    public void setUserPhoneTo(String userPhoneTo) {
        this.userPhoneTo = userPhoneTo;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
