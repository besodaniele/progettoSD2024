package it.unimib.sd2024.DBEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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

    public void cond(){
        
    }

    public JsonNode get(String tableName, String key, String param) {
        try {
            JsonNode jn = tabelle.get(tableName);
            ObjectNode on = (ObjectNode) jn;
            Iterator<Entry<String, JsonNode>> keys = jn.fields();
            ArrayList<String> keysToRemove = new ArrayList<String>();
            
            if(!(key.equals("*"))){
                while(keys.hasNext()){
                    String k = keys.next().getKey();
                    if(!k.equals(key)){
                        keysToRemove.add(k);
                    }
                }
                for (String k : keysToRemove) {
                    on.remove(k);
                }
            }

            if(!param.equals("*")){
                keys = on.fields();
                while(keys.hasNext()){
                    String k = keys.next().getKey();
                    ObjectNode on2 = (ObjectNode) keys.next().getValue();
                    Iterator<Entry<String, JsonNode>> fields = on2.fields();
                    while(fields.hasNext()){
                        String k2 = fields.next().getKey();
                        if(!k2.equals(param)){
                            on2.remove(k2);
                        }
                    }
                    on.set(k, on2);
                }
            } else {
                
            }
            return on;
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
