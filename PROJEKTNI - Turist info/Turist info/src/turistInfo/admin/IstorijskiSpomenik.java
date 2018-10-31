/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.admin;

import turistInfo.admin.interfejsi.NePlacaSeInterfejs;
import turistInfo.admin.interfejsi.PlacaSeInterfejs;

/**
 *
 * @author raka
 */
public class IstorijskiSpomenik extends TuristickaAtrakcija implements NePlacaSeInterfejs{
    
    private String opis;
    private String fotografija;

    public IstorijskiSpomenik(String opis, String fotografija, String naziv, String lokacija) {
        super(naziv, lokacija);
        this.opis = opis;
        this.fotografija = fotografija;
    }

    

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getFotografija() {
        return fotografija;
    }

    public void setFotografija(String fotografija) {
        this.fotografija = fotografija;
    }
    
    @Override
    public int daLiSePlaca(){
        return -1;
    }
     @Override
    public String toString(){
        return super.toString() + "istorijski spomenik";
    }
}
