package it.unimib.sd2024.DBEngine;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

// la classe è una singleton
public class PaneDB {
    private final HashMap<String, HashMap<String, String>> db;
    private final HashMap<String, String> tabella;

    private JsonNode arrayNode;


    private static PaneDB pane = null;

    private PaneDB() {
        this.db = new HashMap<>();
        this.tabella = new HashMap<>();
    }

    public static PaneDB getDB() {
        if (pane == null) {
            pane = new PaneDB();
        }
        return pane;
    }

    public void createTable(String tableName) {

        db.put(tableName, tabella);
    }
    public void setupTest(JsonNode jsonNode) {
        this.arrayNode = jsonNode;
    }

    public JsonNode get(String tableName, String key, String param) {
        return arrayNode.get(key);

    }

}
