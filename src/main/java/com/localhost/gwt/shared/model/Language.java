package com.localhost.gwt.shared.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by AlexL on 07.10.2017.
 */
@DatabaseTable(tableName = "languages")
public class Language implements Serializable {
    @DatabaseField(columnName = "langId")
    private int id;
    @DatabaseField(columnName = "lang_name")
    private String name;
    @DatabaseField(columnName = "lang_short_name")
    private String shortName;

    public Language() {}

    public Language(int id){
        this.id = id;
    }

    public Language(int id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Language)) {
            return false;
        }
        Language that = (Language)obj;
        return this.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return getId() * 3 + 5;
    }
}
