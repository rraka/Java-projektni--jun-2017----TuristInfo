/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.admin;

import java.util.Calendar;
import turistInfo.admin.interfejsi.NePlacaSeInterfejs;
import turistInfo.admin.interfejsi.PlacaSeInterfejs;

/**
 *
 * @author raka
 */
public class Muzej extends TuristickaAtrakcija implements PlacaSeInterfejs, NePlacaSeInterfejs{
    
    private String letak;
    private double cijenaUlaznice;

    public Muzej(String letak, String naziv, String lokacija, double cijenaUlaznice) {
        super(naziv, lokacija);
        this.letak = letak;
        this.cijenaUlaznice = cijenaUlaznice;
    }

    
    
    public String getLetak() {
        return letak;
    }

    public void setLetak(String letak) {
        this.letak = letak;
    }

    public double getCijenaUlaznice() {
        return cijenaUlaznice;
    }

    public void setCijenaUlaznice(double cijenaUlaznice) {
        this.cijenaUlaznice = cijenaUlaznice;
    }
    @Override
    public int daLiSePlaca(){
        Calendar datum = Calendar.getInstance();
        int dan = datum.DAY_OF_WEEK_IN_MONTH;
        if((dan % 2) != 0)
        { 
            return 1;
                }
        else return -1;
        
    }
    @Override
    public String toString(){
        return super.toString() + "muzej";
    }
}
