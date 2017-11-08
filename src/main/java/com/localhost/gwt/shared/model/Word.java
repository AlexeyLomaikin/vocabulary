package com.localhost.gwt.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlexL on 07.10.2017.
 */
public class Word implements Serializable {
    private int wordId;
    private Level level;
    private Map<Language, Translation> translationsMap = new HashMap<Language, Translation>();

    public Word(){}
    public Word(int wordId) {
        this.wordId = wordId;
    }
    public Word(int wordId, Map<Language, Translation> translationsMap, Level level) {
        this.wordId = wordId;
        this.translationsMap = translationsMap;
        this.level = level;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
    public int getWordId() {
        return wordId;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
    public Level getLevel() {
        return level;
    }

    public void setTranslationsMap(Map<Language, Translation> translationsMap) {
        this.translationsMap = translationsMap;
    }
    public Map<Language, Translation> getTranslationsMap() {
        return translationsMap;
    }

    public Translation getTranslation(Language language) {
        return translationsMap.get(language);
    }

    public void addTranslation(Language language, Translation translation) {
        translationsMap.put(language, translation);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Translation translation: translationsMap.values()) {
            sb.append(translation.getWord());
            if (translation.getTranscription() != null) {
                sb.append(" ");
                sb.append(translation.getTranscription());
            }
            sb.append(" | ");
        }
        String s = sb.toString();
        return s.substring(0, s.length() - 2);
    }
}
