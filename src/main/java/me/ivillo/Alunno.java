package me.ivillo;

import java.util.Date;

public class Alunno {
    protected String nome;
    protected String cognome;
    protected Date dataNascita;
    
    public Alunno (){}

    public Alunno(String nome, String cognome, Date dataNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Date getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(Date nascita) {
        this.dataNascita = nascita;
    }

    @Override
    public String toString() {
        return "Nome: " + this.nome + " - " +
        "Cognome: " + this.cognome + " - " +
        "DataNascita: " + this.dataNascita.toString();
    }
}
