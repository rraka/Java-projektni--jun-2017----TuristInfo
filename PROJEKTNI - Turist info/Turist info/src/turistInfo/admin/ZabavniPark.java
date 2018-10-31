/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.admin;

import java.util.Calendar;
import turistInfo.admin.interfejsi.PlacaSeInterfejs;

/**
 *
 * @author raka
 */
public class ZabavniPark extends TuristickaAtrakcija implements PlacaSeInterfejs{

    
    private double cijenaUlaznice;
    
    public ZabavniPark(String naziv, String lokacija, double cijenaUlaznice) {
        super(naziv, lokacija);
        this.cijenaUlaznice = cijenaUlaznice;
    }

    public double getCijenaUlaznice() {
        return cijenaUlaznice;
    }

    public void setCijenaUlaznice(double cijenaUlaznice) {
        this.cijenaUlaznice = cijenaUlaznice;
    }

    public int daLiSePlaca(){

        return 1;
        }
 @Override
    public String toString(){
        return super.toString() + "zabavni park";
    }
    
    

    
}
