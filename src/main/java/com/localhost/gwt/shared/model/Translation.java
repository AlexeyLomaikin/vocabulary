package com.localhost.gwt.shared.model;

import java.io.Serializable;

/**
 * Created by AlexL on 07.10.2017.
 */
public class Translation implements Serializable {
    private String word;
    private String transcription;

    public Translation() {}
    public Translation(String word, String transcription) {
        this.word = word;
        this.transcription = transcription;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTranscription() {
        return transcription;
    }

    public String getWord() {
        return word;
    }
}
