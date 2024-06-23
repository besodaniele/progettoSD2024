package it.unimib.sd2024.DBEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DBParser {
    private static DBParser parser = null;
    private static ObjectMapper mapper;
    private DBParser() {
        mapper = new ObjectMapper();
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
            JsonNode tabella = PaneDB.getDB().getTable(commandSplit[1]);
            switch (commandSplit[0]) {
                case "get":
                    // get dominio.*.* where utente = "m.rossi"
                    // parse tabella.key.param
                    String[] getSplit = commandSplit[1].split("\\.");
                    // prendo la tabella dal database
                    tabella = PaneDB.getDB().getTable(getSplit[0]);
                    JsonNode tabellaCopy = tabella.deepCopy();
                    JsonNode tabellaResult = tabella.deepCopy();

                    // se c'è una condizione where

                    if(commandSplit.length > 2 && commandSplit[2].equals("where")){
                        String[] whereSplit = commandSplit[3].split("=");
                        String param = whereSplit[0];
                        String value = whereSplit[1];
                        JsonNode tabellaDiKey = PaneDB.getDB().get(tabellaCopy, getSplit[1], param);
                        if(tabellaDiKey == null){
                            return "400";
                        }
                        ArrayList<String> keyToRemove = new ArrayList<>();
                        Iterator<Entry<String, JsonNode>> keys = tabellaDiKey.fields();
                        while(keys.hasNext()){
                            Entry<String, JsonNode> entry = keys.next();
                            String k = entry.getKey();
                            ObjectNode on = (ObjectNode) entry.getValue();
                            if(!on.get(param).asText().equals(value)){
                                keyToRemove.add(k);
                            }
                        }
                        for (String k : keyToRemove) {
                            ((ObjectNode) tabellaResult).remove(k);
                        }
                    }
                    
                    JsonNode result = PaneDB.getDB().get(tabellaResult, getSplit[1], getSplit[2]);
                    return result.toString();

                case "insert":
                    if (PaneDB.getDB().get(tabella, commandSplit[2], "*") != null) {
                        return "409";
                    }
                    try {
                        JsonNode jsonToInsert = mapper.readTree(commandSplit[3]);
                        if(PaneDB.getDB().insert(commandSplit[1], commandSplit[2], jsonToInsert) == true){
                            return "200";
                        } else {
                            return "400";
                        }
                    } catch (JsonProcessingException e) {
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
                        JsonNode jsonToUpdate = mapper.readTree(commandSplit[3]);
                        if(PaneDB.getDB().update(commandSplit[1], commandSplit[2], jsonToUpdate) == true){
                            return "200";
                        } else {
                            return "400";
                        }
                    } catch (JsonProcessingException e) {
                        return "400";
                    }
                default:
                    return "400";
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return "400";
        }

    }


}
