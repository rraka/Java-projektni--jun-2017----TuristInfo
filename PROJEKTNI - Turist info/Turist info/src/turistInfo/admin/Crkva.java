/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.admin;

import turistInfo.admin.interfejsi.DobrovoljniPrilogInterfejs;
import turistInfo.admin.interfejsi.PlacaSeInterfejs;

/**
 *
 * @author raka
 */
public class Crkva extends TuristickaAtrakcija implements DobrovoljniPrilogInterfejs{
    
    private double ukupanPrilog = 0;

    public Crkva(String naziv, String lokacija) {
        super(naziv, lokacija);
    }

    
    public double getUkupanPrilog() {
        return ukupanPrilog;
    }

    public void setUkupanPrilog(double ukupanPrilog) {
        this.ukupanPrilog += ukupanPrilog;
    }
    
    @Override
    public int daLiSePlaca(){
        return 0;
    }
    
     @Override
    public String toString(){
        return super.toString() + "crkva";
    }
}
