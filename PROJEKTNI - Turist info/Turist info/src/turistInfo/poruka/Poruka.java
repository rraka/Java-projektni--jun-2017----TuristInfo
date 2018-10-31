/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.poruka;

import java.io.Serializable;

/**
 *
 * @author raka
 */
public class Poruka implements Serializable{
    
    private String poruka;
    private Object dodatak;

    
    public Poruka(String poruka){
        this.poruka = poruka;
    }
    
    public Poruka(String poruka, Object dodatak) {
        this.poruka = poruka;
        this.dodatak = dodatak;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public Object getDodatak() {
        return dodatak;
    }

    public void setDodatak(Object dodatak) {
        this.dodatak = dodatak;
    }
    
    
    
}
