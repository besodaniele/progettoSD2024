package it.unimib.sd2024.DBEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



// la classe è una singleton
public class PaneDB {
    private final HashMap<String, Map<String, Object>> db;
    private static PaneDB pane = null;

    private PaneDB() {
        this.db = new HashMap<>();
    }

    //Singleton
    public static synchronized PaneDB getDB() {
        if (pane == null) {
            pane = new PaneDB();
        }
        return pane;
    }

    public Map getTable(String tableName) {
        return (Map) db.get(tableName);
    }

    public void createTable(String tableName, Map tabella) {
        db.put(tableName, tabella);
    }
    
    public Map get(Map json, String key, String param) {
        try {
            Map<String, Object> coppia = new HashMap<>();
            Iterator<Map.Entry<String, Object>> keys = json.entrySet().iterator();
            while (keys.hasNext()) {
                Map.Entry<String, Object> entry = keys.next();
                coppia.put(entry.getKey(), entry.getValue());
            }
            ArrayList<String> keysToRemove = new ArrayList();
            
            if(!key.equals("*")){
                keys = coppia.entrySet().iterator();
                while(keys.hasNext()){
                    Map.Entry<String, Object> entry = keys.next();
                    String k = entry.getKey();
                    if(!k.equals(key)){
                        keysToRemove.add(k);
                    }
                }
                for (String k : keysToRemove) {
                    coppia.remove(k);
                }
            }

            if(!param.equals("*")){
                keys = coppia.entrySet().iterator();
                while(keys.hasNext()){
                    Map.Entry<String, Object> entry = keys.next();
                    String k = entry.getKey();
                    Map attributi = (Map) entry.getValue();
                    ArrayList<String> fieldsToRemove = new ArrayList<>();
                    Iterator<Map.Entry<String, Object>> fields = attributi.entrySet().iterator();
                    while(fields.hasNext()){
                        Map.Entry<String, Object> entry2 = fields.next();
                        if(!entry2.getKey().equals(param)){
                            fieldsToRemove.add(entry2.getKey());
                        }
                    }
                    for (String field : fieldsToRemove) {
                        attributi.remove(field);
                    }

                    coppia.put(k, attributi);
                }
            }

            return coppia;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean insert(String tableName, String key, Map value) {
        Map table = getTable(tableName);

        if(value != null){
            table.put(key, value);
            db.put(tableName, table);
            return true;
        }
        return false;
    }

    public boolean update(String tableName, String key, Map value) {
        Map table = getTable(tableName);

        if(key != null){
            table.put(key, value);
            db.put(tableName, table);
            return true;
        }
        return false;
    }

    public boolean delete(String tableName, String key) {
        Map table = getTable(tableName);

        if(key != null){
            table.remove(key);
            db.put(tableName, table);
            return true;
        }
        return false;
    }

    public String getLastIndex(String tableName) {
        Map table = getTable(tableName);
        Iterator<Map.Entry<String, Object>> keys = table.entrySet().iterator();
        ArrayList<Integer> keysList = new ArrayList<>();
        while(keys.hasNext()){
            Entry<String, Object> entry = keys.next();
            String k = entry.getKey();
            keysList.add(Integer.valueOf(k));
        }
        int lastKey = 0;
        for (int k : keysList) {
            if(k > lastKey){
                lastKey = k;
            }
        }
        return String.valueOf(lastKey);
    }
}
