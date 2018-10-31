/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import turistInfo.korisnik.Turista;
import turistInfo.poruka.Poruka;

/**
 *
 * @author raka
 */
class ServerAnalizatorRezultataThread extends Thread {

    private Socket soket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ArrayList<Turista> turisti;
    private int brojac;

    public ServerAnalizatorRezultataThread(Socket soket, int brojac) {
        try {
            this.soket = soket;
            this.brojac = brojac;
            this.ois = new ObjectInputStream(soket.getInputStream());
            this.oos = new ObjectOutputStream(soket.getOutputStream());
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Poruka poruka = (Poruka) ois.readObject();
                if (poruka.getPoruka().equals("turisti")) {
                    File putanjaDoTurista = new File(".\\src\\ServerAnalizatorRezultata\\fajlovi\\" + brojac);
                    if (!putanjaDoTurista.exists()) {
                        putanjaDoTurista.mkdir();
                    }
                    File putanjaFajla = new File(putanjaDoTurista.getPath() + "\\turisti.ser");
                    primiFajl(putanjaFajla);
                    this.turisti = deSerijalizacija(putanjaFajla);
                } else if (poruka.getPoruka().equals("top5")) {
                    ArrayList<Turista> top5 = izdvojiTop5(turisti);//sortiranje top5
                    serijalizacija(top5, brojac);
                    posaljiFajl((new File(".\\src\\ServerAnalizatorRezultata\\fajlovi\\" + brojac + "\\top5.ser")));
                } else if (poruka.getPoruka().equals("rezultati")) {
                    rezultatiUCSV(turisti);
                    posaljiFajl(new File(".\\src\\ServerAnalizatorRezultata\\fajlovi\\" + brojac + "\\rezultati.csv"));

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void rezultatiUCSV(ArrayList<Turista> turisti) {
        try {
            File lokacija = new File(".\\src\\ServerAnalizatorRezultata\\fajlovi\\" + brojac);
            if(!lokacija.exists()){
                lokacija.mkdir();
            }
            PrintWriter upis = new PrintWriter(new File(lokacija.getPath() + "\\rezultati.csv"));
            StringBuilder sb = new StringBuilder();
            sb.append("RB");
            sb.append(",");
            sb.append("IME");
            sb.append(",");
            sb.append("BROJ POSJECENIH T.A.");
            sb.append("\n");
            for (int i = 0; i < turisti.size(); i++) {
                sb.append(i);
                sb.append(",");
                sb.append(turisti.get(i).getIme());
                sb.append(",");
                sb.append(turisti.get(i).getBrojPosjecenihMjesta());
                sb.append("\n");
            }
            upis.write(sb.toString());
            upis.close();
            System.out.println("Zavrseno kreiranje rezultati.csv fajla!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void primiFajl(File putanjaFajla) {
        try {
            System.out.println("Preuzimanje pocinje...");
            long duzinaLong = (long) ois.readObject();
            int duzina = (int) duzinaLong;
            int kontrolnaDuzina = 0, flag = 0;
            byte[] buffer = new byte[2 * 1024 * 1024];
            OutputStream fajl = new FileOutputStream(putanjaFajla);
            while ((kontrolnaDuzina = ois.read(buffer)) > 0) {
                fajl.write(buffer, 0, kontrolnaDuzina);
                flag += kontrolnaDuzina;
                if (duzina <= flag) {
                    break;
                }
            }
            System.out.println("Preuzimanje zavrseno...");
            fajl.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void posaljiFajl(File putanjaDoKnjige) {
        try {
            long duzina = putanjaDoKnjige.length();
            oos.writeObject(duzina);
            byte[] buffer = new byte[2 * 1024 * 1024];
            InputStream fajl = new FileInputStream(putanjaDoKnjige);
            int length = 0;
            while ((length = fajl.read(buffer)) > 0) {
                oos.write(buffer, 0, length);
            }
            fajl.close();
            oos.reset();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void serijalizacija(ArrayList<Turista> turisti, int brojac) {
        try {
            File putanjaDofajla = new File(".\\src\\ServerAnalizatorRezultata\\fajlovi\\" + brojac);
            if (!putanjaDofajla.exists()) {
                putanjaDofajla.mkdir();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(putanjaDofajla.getPath() + "\\top5.ser")));
            oos.writeObject(turisti);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("ZAVRSENA SERIJALIZACIJA!!!");
    }

    public static ArrayList deSerijalizacija(File putanjaFajla) {
        ArrayList<Turista> deSerijalizovanaLista = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(putanjaFajla));
            deSerijalizovanaLista = (ArrayList<Turista>) ois.readObject();
            ois.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return deSerijalizovanaLista;
    }

    public static ArrayList<Turista> izdvojiTop5(ArrayList<Turista> sviTuristi) {
        ArrayList<Turista> top5 = new ArrayList<>();
        Collections.sort(sviTuristi);
        for (int i = 0; i < 5; i++) {
            top5.add(sviTuristi.get(i));
        }
        return top5;
    }

}
