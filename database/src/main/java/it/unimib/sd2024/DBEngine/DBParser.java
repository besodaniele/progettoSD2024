package it.unimib.sd2024.DBEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;



public class DBParser {
    private static DBParser parser = null;
    private static Jsonb jsonb;
    private DBParser() {
        jsonb = JsonbBuilder.create();
    }

    public static DBParser getParser() {
        if (parser == null) {
            parser = new DBParser();
        }
        return parser;
    }

    public String parse(String command) {
        try {
            String[] commandSplit = command.split(" ");
            Map tabella = PaneDB.getDB().getTable(commandSplit[1]);
            switch (commandSplit[0]) {
                case "get":
                    // parse tabella.key.param
                    String[] getSplit = commandSplit[1].split("\\.");
                    // prendo la tabella dal database
                    tabella = PaneDB.getDB().getTable(getSplit[0]);
                    //coppia 
                    Map<String, Object> coppia = new HashMap<>();
                    Iterator<Map.Entry<String, Object>> chiavi = tabella.entrySet().iterator();
                    while (chiavi.hasNext()) {
                        Map.Entry<String, Object> entry = chiavi.next();
                        coppia.put(entry.getKey(), entry.getValue());
                    }

                    //tabella di risultato
                    Map<String, Object> tabellaResult = new HashMap<>();
                    chiavi = tabella.entrySet().iterator();
                    while (chiavi.hasNext()) {
                        Map.Entry<String, Object> entry = chiavi.next();
                        tabellaResult.put(entry.getKey(), entry.getValue());
                    }

                    // se c'è una condizione where

                    if(commandSplit.length > 2 && commandSplit[2].equals("where")){
                        String[] whereSplit = commandSplit[3].split("=");
                        String param = whereSplit[0];
                        String value = whereSplit[1];
                        Map tabellaDiKey = PaneDB.getDB().get(coppia, getSplit[1], param);
                        if(tabellaDiKey == null){
                            return "400";
                        }
                        ArrayList<String> keyToRemove = new ArrayList<>();
                        Iterator<Map.Entry<String, Object>> keys = tabellaDiKey.entrySet().iterator();
                        while(keys.hasNext()){
                            Map.Entry<String, Object> entry = keys.next();
                            String k = entry.getKey();
                            Map campi = (Map) entry.getValue();
                            if(!campi.get(param).equals(value)){
                                keyToRemove.add(k);
                            }
                        }
                        for (String k : keyToRemove) {
                            tabellaResult.remove(k);
                        }
                    }
                    
                    Map result = PaneDB.getDB().get(tabellaResult, getSplit[1], getSplit[2]);
                    return result.toString().replaceAll("=", ":");

                case "insert":
                    if (PaneDB.getDB().get(tabella, commandSplit[2], "*") != null) {
                        return "409";
                    }
                    try {
                        Map jsonToInsert = jsonb.fromJson(commandSplit[3], new HashMap<String, Object>(){}.getClass().getGenericSuperclass());
                        if(PaneDB.getDB().insert(commandSplit[1], commandSplit[2], jsonToInsert) == true){
                            return "200";
                        } else {
                            return "400";
                        }
                    }catch (JsonbException | IllegalArgumentException e) {
                        return "400";
                    }

                case "delete":
                    if (PaneDB.getDB().get(tabella, commandSplit[2], "*") == null) {
                        return "404";
                    } 
                    if(PaneDB.getDB().delete(commandSplit[1], commandSplit[2]) == true){
                        return "200";
                    } else {
                        return "400";
                    }
                case "update":
                    if (PaneDB.getDB().get(tabella, commandSplit[2], "*") == null) {
                        return "404";
                    }
                    try {
                        Map jsonToUpdate = jsonb.fromJson(commandSplit[3], new HashMap<String, Object>(){}.getClass().getGenericSuperclass());
                        if(PaneDB.getDB().update(commandSplit[1], commandSplit[2], jsonToUpdate) == true){
                            return "200";
                        } else {
                            return "400";
                        }
                    } catch (JsonbException | IllegalArgumentException e) {
                        return "400";
                    }
                case "getLastIndex":
                    return PaneDB.getDB().getLastIndex(commandSplit[1]);
                default:
                    return "400";
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return "400";
        }

    }


}
