/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.korisnik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;
import turistInfo.admin.TuristickaAtrakcija;

/**
 *
 * @author raka
 */
public class PomocnaKlasa implements Serializable{

    private  static int xx;
    private  static int yy;
    private  Object[][] mapa = new Object[xx][yy];
    private  Turista[] sviTuristi;
    public   transient  SimulacijaFormaGUI sfg;
    

    public PomocnaKlasa(){}
    public PomocnaKlasa(int m, int n, SimulacijaFormaGUI sfg){
        this.xx=m;
        this.yy=n;
        this.sfg = sfg;
        mapa = new Object[m][n];
    }
    
    
    public static void podesiSpinere(ArrayList<JSpinner> spineri) {

        for (int i = 0; i < spineri.size(); i++) {
            SpinnerNumberModel snm = (SpinnerNumberModel) spineri.get(i).getModel();
            snm.setMinimum(1);
            snm.setValue(1);
            JFormattedTextField tf = ((JSpinner.DefaultEditor) spineri.get(i).getEditor()).getTextField();
            NumberFormatter formatter = (NumberFormatter) tf.getFormatter();
            DecimalFormat decimalFormat = new DecimalFormat("0");
            formatter.setFormat(decimalFormat);
            formatter.setAllowsInvalid(false);
        }

    }

    public  void postaviTuristickeAtrakcijeNaMapu(int m, int n, int brojTuristickihAtrakcija) {
        ArrayList<TuristickaAtrakcija> sveTuristickeAtrakcije = turistInfo.admin.PomocnaKlasa.deSerijalizacija();
        int brojTrenutnihAtrakcija = sveTuristickeAtrakcije.size();
       // xx = m;
        //yy = n;
         //mapa = new Object[m][n];
        int broj = brojTuristickihAtrakcija;
       
        Random rand = new Random();
        int br = 0;
        while (broj > 0) {//brojZadatihTuristickih atrakcija
            if (br < brojTrenutnihAtrakcija) {//br broj postavljenih
                int x = rand.nextInt(m);
                int y = rand.nextInt(n);
                if (mapa[x][y] == null) {
                    mapa[x][y] = sveTuristickeAtrakcije.get(br);
                    broj--;
                    br++;
                }
                //ako je broj trenutnih atrakcija manji od broja zadatih za mapu
            } else {
                br--;
                int x = rand.nextInt(xx);
                int y = rand.nextInt(yy);
                if (mapa[x][y] == null) {
                    mapa[x][y] = sveTuristickeAtrakcije.get(br);
                    broj--;
                    
                }

            }
            /*    for(int i=0; i<m; i++){
            System.out.println();
            for(int j=0; j<n; j++){
                 System.out.println("mapa: " + mapa[i][j]);
            }
        }*/

        }
    }

    public  void postaviTuristeNaMapu(int brojTurista, SimulacijaFormaGUI simulacijaFormaGUI) {
        //sfg = simulacijaFormaGUI;
        int broj = brojTurista;

        Turista turisti[] = new Turista[broj];
        for (int i = 0; i < broj; i++) {
            turisti[i] = new Turista(sfg);
        }
        sviTuristi = turisti;

        Random rand = new Random();
        while (broj > 0) {
            int x = rand.nextInt(xx);
            int y = rand.nextInt(yy);
            if (mapa[x][y] == null) {
                mapa[x][y] = turisti[broj - 1];
                turisti[broj - 1].setRedNaMapi(x);
                turisti[broj - 1].setKolonaNaMapi(y);
                broj--;
            }
        }
    }
    
    public  synchronized void upisiTekstNaFormu(String tekst){
        sfg.getTekstAreaSimulacija().append(tekst);
    }

    public  void pokreniSimulaciju() {
        //pokreni simulaciju
        for (int i = 0; i < sviTuristi.length; i++) {
            sviTuristi[i].start();
        }
        //CEKANJE TREDOVA DA ZAVRSE TRCANJE!!!KOMUNIKACIJA SA SERVEROM
        new KomunikacijaSaServerom(sviTuristi, this);
    }
    
    
    
    

    public  Object[][] getMapa() {
        return mapa;
    }

    public  void setMapa(Object[][] mapa) {
        this.mapa = mapa;
    }

    public  Turista[] getSviTuristi() {
        return sviTuristi;
    }

    public  void setSviTuristi(Turista[] sviTuristi) {
        this.sviTuristi = sviTuristi;
    }

    
    public static void serijalizacija(ArrayList<Turista> turisti, int brojacKorisnika) {
        try {
            File noviFolder = new File(".\\src\\turistInfo\\admin\\fajlovi\\" + brojacKorisnika + "\\");
            if(!noviFolder.exists()){
  
                noviFolder.mkdir();
            }
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(noviFolder.getPath() + "\\turisti.ser")));
                oos.writeObject(turisti);
                System.out.println(turisti);
                oos.close();
            }
       catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static ArrayList deSerijalizacija(int brojacKorisnika) {
        ArrayList<Turista> deSerijalizovanaLista = null;
        try {
                File noviFolder = new File(".\\src\\turistInfo\\admin\\fajlovi\\" + brojacKorisnika);
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(noviFolder.getPath() + "\\turisti.ser")));
                deSerijalizovanaLista = (ArrayList<Turista>) ois.readObject();
                ois.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return deSerijalizovanaLista;
    }
   

}




