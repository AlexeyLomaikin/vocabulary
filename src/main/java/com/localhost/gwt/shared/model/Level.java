package com.localhost.gwt.shared.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by AlexL on 06.10.2017.
 */
@DatabaseTable(tableName = "levels")
public class Level implements Serializable {
    @DatabaseField(columnName = "id")
    private int id;
    @DatabaseField(columnName = "lvl_name")
    private String name;

    public Level() {}
    public Level(int id) {
        this.id = id;
    }
    public Level(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
