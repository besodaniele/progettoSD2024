package it.unimib.sd2024.DBEngine;

import java.util.HashMap;


// la classe è una singleton
public class PaneDB {
    private final HashMap<String, HashMap<String, String>> db;
    private final HashMap<String, String> tabella;

    private PaneDB pane = null;

    private PaneDB() {
        this.db = new HashMap<>();
        this.tabella = new HashMap<>();
    }

    public PaneDB getDB() {
        if (pane == null) {
            pane = new PaneDB();
        }
        return pane;
    }
}
