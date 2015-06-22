package com.example.esquivel.anpr_imagin;

/**
 * Created by Rafael on 20/05/2015.
 */
public class Validaciones {
    private String placa;

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String Validacion(String placa, String tipovehiculo){

        boolean Val=false;
        int cont=0;
        int cont2=0;
        int cont3=0;
        int i=0;

        if(placa.toString()==null){

            return "0";
        }else {
            if (tipovehiculo=="0" && placa.length()==6){
                for(i=0; i<placa.length();i++ ){
                    if(Character.isLetter(placa.charAt(i)) && i<=2){
                        cont=cont+1;
                    }
                    if(Character.isDigit(placa.charAt(i)) && i>=3 && cont==3 ){
                        cont2=cont2+1;
                    }

                }
                if((cont+cont2)==6) return "Placa de automovil valida";

            }else if(tipovehiculo=="1" && (placa.length()==6 || placa.length()==5) ){

                for( i=0; i<placa.length();i++ ){
                    if(Character.isLetter(placa.charAt(i)) && i<=2){
                        cont=cont+1;
                    }
                    if(Character.isDigit(placa.charAt(i)) && i>=3 &&i<5 && cont==3 ){
                        cont2=cont2+1;
                    }
                    if((cont+cont2)==5 && Character.isLetter(placa.charAt(i)) && i==5){
                        cont3=cont+cont2+1;
                    }
                }
                if((placa.length()==5 && (cont+cont2)==5) || (placa.length()==6 && cont3==6)) return "Placa de motocicleta valida";}
        }
        return "1";
    }
}
