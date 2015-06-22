package com.example.esquivel.anpr_imagin;

/**
 * Created by Rafael on 19/05/2015.
 */
public class RegistroDTO {
    private String IdRegistro ;
    private String UsuarioCedula ;
    private String Placa;
    private String FechaRegistro ;
    private String TipoRegistro ;


    public String getIdRegistro() {
        return IdRegistro;
    }
    public void setIdRegistro(String idRegistro) {
        IdRegistro = idRegistro;
    }
    public String getUsuarioCedula() {
        return UsuarioCedula;
    }
    public void setUsuarioCedula(String usuarioCedula) {
        UsuarioCedula = usuarioCedula;
    }
    public String getPlaca() {
        return Placa;
    }
    public void setPlaca(String placa) {
        Placa = placa;
    }
    public String getFechaRegistro() {
        return FechaRegistro;
    }
    public void setFechaRegistro(String fechaRegistro) {
        FechaRegistro = fechaRegistro;
    }
    public String getTipoRegistro() {
        return TipoRegistro;
    }
    public void setTipoRegistro(String tipoRegistro) {
        TipoRegistro = tipoRegistro;
    }
}
