package com.example.uploadingfiles.service;

public class StringModel {
    private String receivedText;
    private String definitions;

    public StringModel(){
    }
    public StringModel(String receivedText){
        this.receivedText = receivedText;
    }

    public void setReceivedText(String receivedText){
        this.receivedText = receivedText;
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }

    public String getReceivedText(){
        return receivedText;
    }

    public String getDefinitions(){
        return definitions;
    }
}
