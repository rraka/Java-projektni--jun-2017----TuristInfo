/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.korisnik;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import turistInfo.poruka.Poruka;

/**
 *
 * @author raka
 */
public class KomunikacijaSaServerom extends Thread {

    public  Socket soket;
    public  Socket soketArhiver;
    public  ObjectOutputStream oosArhiver;
    public  ObjectInputStream oisArhiver;
    public  ObjectOutputStream oos;
    public  ObjectInputStream ois;
    public static final int PORT = 9000;
    public static final int PORT_ARHIVER = 9001;
    public Turista[] sviTuristi;
    public ArrayList<Turista> turisti;
    public HashMap<String, Integer> razvrstaniLeci;
    public PomocnaKlasa pk;
    public GlavnaKorisnickaFormaGUI gkf;
    public int brojacKorisnika;

    public KomunikacijaSaServerom(Turista[] sviTuristi, PomocnaKlasa pk) {
        try {
            this.sviTuristi = sviTuristi;
            this.turisti = new ArrayList<>();
            this.gkf = SimulacijaFormaGUI.gkf;
            this.pk = pk;
            this.brojacKorisnika = gkf.getBrojacKorisnika();
            for (int i = 0; i < sviTuristi.length; i++) {
                turisti.add(sviTuristi[i]);//dodavanje turista u arraylistu za serijalizaciju
            }
            InetAddress adresa = InetAddress.getByName("127.0.0.1");
            this.soket = new Socket(adresa, PORT);
             this.soketArhiver = new Socket(adresa, PORT_ARHIVER);
            this.oos = new ObjectOutputStream(soket.getOutputStream());
            this.ois = new ObjectInputStream(soket.getInputStream());
            this.oosArhiver = new ObjectOutputStream(soketArhiver.getOutputStream());
            this.oisArhiver = new ObjectInputStream(soketArhiver.getInputStream());
            this.start();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public int getBrojacKorisnika() {
        return brojacKorisnika;
    }
    
    
    

    @Override
    public void run() {
        try {
            for (int i = 0; i < sviTuristi.length; i++) {
                sviTuristi[i].join();//cekanje da svi turisti zavrse takmicenje
            }
            pk.upisiTekstNaFormu("\n\n=======================TAKMICENJE ZAVRSENO=======================\n\n");
            PomocnaKlasa.serijalizacija(turisti, brojacKorisnika);           
            File izvorZaZIP = new File(".\\src\\turistInfo\\korisnik\\fajlovi\\" + brojacKorisnika);
            if(!izvorZaZIP.exists()){
                izvorZaZIP.mkdir();
            }
            File odredisteZaZIP = new File(".\\src\\turistInfo\\korisnik\\" + brojacKorisnika);
            if(!odredisteZaZIP.exists()){
                odredisteZaZIP.mkdir();
            }
            File odredisteZaZIP_1 = new File(odredisteZaZIP.getPath() +  "\\turisti_folderi.zip");
            zip(izvorZaZIP, odredisteZaZIP_1);
            oosArhiver.writeObject(new Poruka("leci"));
            posaljiFajlArhiveru(odredisteZaZIP_1);//saljemo zip arhiver serveru
            //Poruka porukaOdArhivera = (Poruka) oisArhiver.readObject();
           // System.out.println("Preuzeto od ARHIVER SERVERA: " + porukaOdArhivera.getDodatak());
            //prikaziRezulatateOdArhivera(porukaOdArhivera.getDodatak());
           // File izvorZaUnZIP = new File(".\\src\\turistInfo\\korisnik\\turisti_folderi.zip");
           // File odredisteZaUnZIP = new File(".\\src\\turistInfo\\korisnik\\unZip");
           
            oos.writeObject(new Poruka("turisti"));
            File putanja = new File(".\\src\\turistInfo\\admin\\fajlovi\\" + brojacKorisnika);
            if(!putanja.exists()){
            putanja.mkdir();
            }
            posaljiFajl(new File(putanja.getPath() + "\\turisti.ser"));
            oos.writeObject(new Poruka("top5"));
            File top5folder = new File(".\\src\\turistInfo\\admin\\fajlovi\\");
            if(!top5folder.exists()){
                izvorZaZIP.mkdir();
            }
            File top5fajl = new File(top5folder.getPath()  + "\\" + brojacKorisnika + "\\top5.ser");
            primiFajl(top5fajl);
            ArrayList<Turista> top5 = deSerijalizacija(brojacKorisnika);
            new Top5FormaGUI(top5, gkf, this);
            
            Poruka poruka = (Poruka) oisArhiver.readObject();
            if(poruka.getPoruka().equals("broj letaka")){
               HashMap<String, Integer> brojLetaka = (HashMap<String, Integer>) poruka.getDodatak();
               StringBuilder sb = new StringBuilder();
               for(char znak='a'; znak<'z'+1; znak++){
                   if(brojLetaka.get(String.valueOf(znak))!= null){
                       sb.append(String.valueOf(znak) + ": ");
                       sb.append(brojLetaka.get(String.valueOf(znak)) + "\n");
                   }
               }
                JTextArea tekstArea = new JTextArea();
                tekstArea.setText(sb.toString());
                tekstArea.setEditable(false);
                JScrollPane sp = new JScrollPane(tekstArea);
                if(brojLetaka.size() == 0){
                    JOptionPane.showMessageDialog(null, "Broj letaka: 0", "Broj letaka po odredjemo slovu", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                JOptionPane.showMessageDialog(null, sb.toString(), "Broj letaka po odredjemo slovu", JOptionPane.INFORMATION_MESSAGE);
            }}
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
    
    
    private void posaljiFajlArhiveru(File putanjaDoKnjige) {
        try {
            long duzina = putanjaDoKnjige.length();
            oosArhiver.writeObject(duzina);
            byte[] buffer = new byte[2 * 1024 * 1024];
            InputStream fajl = new FileInputStream(putanjaDoKnjige);
            int length = 0;
            while ((length = fajl.read(buffer)) > 0) {
                oosArhiver.write(buffer, 0, length);
            }
            fajl.close();
            oosArhiver.reset();
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

    public static void zip(File directory, File zipfile) throws IOException {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }

    public static void unZip(File zipfile, File outdir) {
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
            ZipEntry entry;
            String name, dir;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                if (entry.isDirectory()) {
                    File d = new File(outdir, name);
                    if (!d.exists()) {
                        d.mkdirs();
                    }
                    continue;
                }
              
                int s = name.lastIndexOf(File.separatorChar);
                dir = s == -1 ? null : name.substring(0, s);

                if (dir != null) {
                    File d = new File(outdir, dir);
                    if (!d.exists()) {
                        d.mkdirs();
                    }
                }

                // extractFile(zin, outdir, name);
                byte[] buffer = new byte[4096];
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir, name)));
                int count = -1;
                while ((count = zin.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.close();
            }
            zin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    public static ArrayList deSerijalizacija(int brojacKorisnika) {
        ArrayList<Turista> deSerijalizovanaLista = null;
        try {
            File noviFolder = new File(".\\src\\turistInfo\\admin\\fajlovi");
            if(!noviFolder.exists()){
                noviFolder.mkdir();
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(noviFolder.getPath() + "\\" + brojacKorisnika + "\\top5.ser")));
            deSerijalizovanaLista = (ArrayList<Turista>) ois.readObject();
            ois.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return deSerijalizovanaLista;
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    private static void copy(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            copy(in, out);
        } finally {
            out.close();
        }
    }

    public ObjectOutputStream getOosArhiver() {
        return oosArhiver;
    }

    public ObjectInputStream getOisArhiver() {
        return oisArhiver;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }
    
    

}
