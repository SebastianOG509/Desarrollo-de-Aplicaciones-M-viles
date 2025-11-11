package com.example.reto10;

public class CasoCovid {
    private String departamentoNom;
    private String ciudadMunicipioNom;
    private String sexo;
    private String edad;
    private String estado;
    private String fuenteContagio;

    public CasoCovid(String departamentoNom, String ciudadMunicipioNom, String sexo, String edad, String estado, String fuenteContagio) {
        this.departamentoNom = departamentoNom;
        this.ciudadMunicipioNom = ciudadMunicipioNom;
        this.sexo = sexo;
        this.edad = edad;
        this.estado = estado;
        this.fuenteContagio = fuenteContagio;
    }

    public String getDepartamentoNom() { return departamentoNom; }
    public String getCiudadMunicipioNom() { return ciudadMunicipioNom; }
    public String getSexo() { return sexo; }
    public String getEdad() { return edad; }
    public String getEstado() { return estado; }
    public String getFuenteContagio() { return fuenteContagio; }
}
