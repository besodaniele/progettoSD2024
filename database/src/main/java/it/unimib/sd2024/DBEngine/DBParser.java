package it.unimib.sd2024.DBEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DBParser {
    private static DBParser parser = null;
    private static final ObjectMapper mapper = new ObjectMapper();
    private DBParser() {
    }

    public static DBParser getParser() {
        if (parser == null) {
            parser = new DBParser();
        }
        return parser;
    }

    public String parse(String command) {
        String[] commandSplit = command.split(" ");
        JsonNode tabella = PaneDB.getDB().getTable(commandSplit[1]);
        switch (commandSplit[0]) {
            case "get":
                // get dominio.*.* where *.utente = "m.rossi"
                // parse tabella key param
                String[] getSplit = commandSplit[1].split(".");
                tabella = PaneDB.getDB().getTable(getSplit[0]);
                // se c'è una condizione where
                if(commandSplit[2].equals("where")){

                } 
                
                JsonNode result = PaneDB.getDB().get(tabella, getSplit[1], getSplit[2]);
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
    }


}
