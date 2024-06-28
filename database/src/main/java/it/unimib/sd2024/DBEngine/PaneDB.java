package it.unimib.sd2024.DBEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;



// la classe è una singleton
public class PaneDB {
    private final HashMap<String, Map<String, Object>> db;
    private final ConcurrentHashMap<String, String> locks;
    private static PaneDB pane = null;

    private PaneDB() {
        this.db = new HashMap<>();
        this.locks = new ConcurrentHashMap<>();
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
    
    public static Map<String, Object> deepCopyMap(Map<String, Object> original) {
        Map<String, Object> copy = new HashMap<>();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                // Ricorsivamente copia mappature annidate
                value = deepCopyMap((Map<String, Object>) value);
            }
            // Altrimenti, si assume che il valore possa essere direttamente messo nella nuova mappa
            copy.put(entry.getKey(), value);
        }
        return copy;
    }

    public Map get(Map json, String key, String param) {
        try {
            Map<String, Object> copia = deepCopyMap(json);
            
            Iterator<Map.Entry<String, Object>> keys = copia.entrySet().iterator();
    
            ArrayList<String> keysToRemove = new ArrayList();
            
            if(!key.equals("*") && param.equals("*")){
                Object chiave = copia.get(key);
                Map<String, Object> result = new HashMap<>();
                Iterator<Map.Entry<String, Object>> fields = ((Map) chiave).entrySet().iterator();
                while(fields.hasNext()){
                    Map.Entry<String, Object> entry = fields.next();
                    result.put(entry.getKey(), entry.getValue());
                }
                return result;
            }
            
            if(!key.equals("*")){
                keys = copia.entrySet().iterator();
                while(keys.hasNext()){
                    Map.Entry<String, Object> entry = keys.next();
                    String k = entry.getKey();
                    if(!k.equals(key)){
                        keysToRemove.add(k);
                    }
                }
                for (String k : keysToRemove) {
                    copia.remove(k);
                }
            }

            if(!param.equals("*")){
                keys = copia.entrySet().iterator();
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

                    copia.put(k, attributi);
                }
            }

            return copia;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String insert(String tableName, String key, String nameKey, String user, Map value) {
        //controllo lock
        String utente = locks.get(tableName + "." + nameKey);
        if (utente != null && !utente.equals(user)) {
            return "409";
        } else if (utente == null) {
            return "400";
        }

        Map table = getTable(tableName);

        if(value != null){
            table.put(key, value);
            db.put(tableName, table);
            return "200";
        }
        return "400";
    }

    public String update(String tableName, String key,String nameKey, String user, Map value) {
        //controllo lock
        String utente = locks.get(tableName + "." + nameKey);
        if (utente != null && !utente.equals(user)) {
            return "409";
        } else if (utente == null) {
            return "400";
        }

        Map table = getTable(tableName);

        if(key != null){
            table.put(key, value);
            db.put(tableName, table);
            return "200";
        }
        return "400";
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
        if (table == null) {
            return "404";
        }
        if (table.isEmpty()) {
            return "0";
        }
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

    public synchronized String lock(String tableName, String nameKey, String user) {
        String utente = locks.get(tableName + "." + nameKey);
        if (utente == null) {
            locks.put(tableName + "." + nameKey, user);
            return "200";
        } else {
            return "409";
        }
    }

    public synchronized String unlock(String tableName, String nameKey, String user) {
        String utente = locks.get(tableName + "." + nameKey);
        if(utente != null && utente.equals(user)){
            locks.remove(tableName + "." + nameKey);
            return "200";
        } else if(utente == null){
            return "404";
        } else {
            return "400";
        }
    }
}
