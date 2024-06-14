package it.unimib.sd2024;

import java.time.LocalDate;

public class Dominio {
    private String dominio;
    private LocalDate dataRegistrazione, dataScadenza;

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDate dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

}
