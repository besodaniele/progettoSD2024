package it.unimib.sd2024.DBEngine;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

// la classe è una singleton
public class PaneDB {
    private final HashMap<String, JsonNode> tabelle;
    private static PaneDB pane = null;


    private PaneDB() {
        this.tabelle = new HashMap<>();
    }

    //Singleton
    public static PaneDB getDB() {
        if (pane == null) {
            pane = new PaneDB();
        }
        return pane;
    }

    public void createTable(String tableName, JsonNode tabella) {
        tabelle.put(tableName, tabella);
    }

    public JsonNode get(String tableName, String key, String param) {
        try {
            JsonNode jn = tabelle.get(tableName);

            if(!(key.equals("*"))){
            jn = jn.get(key);
            }

            if(!param.equals("*")){
            jn = jn.get(param);
            }
            return jn;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean insert(String tableName, String key, JsonNode value) {
        JsonNode jn = tabelle.get(tableName);
        ObjectNode on = (ObjectNode) jn;

        if(value != null){
            on.set(key, value);
            tabelle.put(tableName, on);
            return true;
        }
        return false;
    }

    public boolean update(String tableName, String key, JsonNode value) {
        JsonNode jn = tabelle.get(tableName);
        ObjectNode on = (ObjectNode) jn;

        if(key != null){
            on.set(key, value);
            tabelle.put(tableName, on);
            return true;
        }
        return false;
    }

    public boolean delete(String tableName, String key) {
        JsonNode jn = tabelle.get(tableName);
        ObjectNode on = (ObjectNode) jn;

        if(key != null){
            JsonNode jnAfterRevome = on.remove(key);
            tabelle.put(tableName, jnAfterRevome);
            return true;
        }
        return false;
    }

}
