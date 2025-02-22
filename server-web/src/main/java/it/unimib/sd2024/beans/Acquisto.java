package it.unimib.sd2024.beans;

import java.time.LocalDate;

public class Acquisto {

    private String cliente, id,mail,numeroCarta, cvv, nomeIntestatario, cognomeIntestatario,tipo,dominio;
    private LocalDate dataScadenza,dataOrdine;
    private int numAnni;
    private double quota;


    
    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public int getNumAnni() {
        return numAnni;
    }

    public void setNumAnni(int numAnni) {
        this.numAnni = numAnni;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNomeIntestatario() {
        return nomeIntestatario;
    }

    public void setNomeIntestatario(String nomeIntestatario) {
        this.nomeIntestatario = nomeIntestatario;
    }

    public String getCognomeIntestatario() {
        return cognomeIntestatario;
    }

    public void setCognomeIntestatario(String cognomeIntestatario) {
        this.cognomeIntestatario = cognomeIntestatario;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public double getQuota() {
        return quota;
    }

    public void setQuota(double quota) {
        this.quota = quota;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

}
