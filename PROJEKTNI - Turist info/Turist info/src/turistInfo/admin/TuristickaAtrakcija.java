/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.admin;

import java.io.Serializable;

/**
 *
 * @author raka
 */
public abstract class TuristickaAtrakcija implements Serializable{
    
    private String naziv;
    private String lokacija;

    public TuristickaAtrakcija(){}
    
    public TuristickaAtrakcija(String naziv, String lokacija) {
        this.naziv = naziv;
        this.lokacija = lokacija;
    }
    
     

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    @Override
    public String toString(){
        return " TA:";
    }

   
    
}
