package com.localhost.gwt.shared;

import com.localhost.gwt.shared.model.Language;
import com.localhost.gwt.shared.model.Level;
import com.localhost.gwt.shared.model.Word;

import java.io.Serializable;
import java.util.*;

/**
 * Created by AlexL on 08.11.2017.
 */
public class ServiceResponse implements Serializable {
    private List<Word> words;
    private List<Level> levels;
    private List<Language> languages;
    private Map<String, String> params = new HashMap<String, String>();

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<Language> getLanguages() {
        return languages != null ? languages : Collections.<Language>emptyList();
    }

    public List<Level> getLevels() {
        return levels != null ? levels : Collections.<Level>emptyList();
    }

    public List<Word> getWords() {
        return words != null ? words : Collections.<Word>emptyList();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public String getParam(String name) {
        return params.get(name);
    }
}
