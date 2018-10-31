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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import turistInfo.korisnik.Turista;
import turistInfo.poruka.Poruka;

/**
 *
 * @author raka
 */
class ServerArhiverThread extends Thread {

    private Socket soket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private HashMap<String, Integer> prebrojeniLeci;
    private int brojac;

    public ServerArhiverThread(Socket soket, int brojac) {
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
                if (poruka.getPoruka().equals("leci")) {

                    File putanjaZaZipFajl = new File(".\\src\\ServerArhiver\\fajlovi\\" + brojac);
                    if (!putanjaZaZipFajl.exists()) {
                        putanjaZaZipFajl.mkdir();
                    }

                    primiFajl(new File(putanjaZaZipFajl.getPath() + "\\leci.zip"));

                    File putanjaDoUnZipFajla = new File(".\\src\\ServerArhiver\\fajlovi\\unZipovani\\" + brojac);
                    if (!putanjaDoUnZipFajla.exists()) {
                        putanjaDoUnZipFajla.mkdir();
                    }
                    unZip(new File(putanjaZaZipFajl.getPath() + "\\leci.zip"), putanjaDoUnZipFajla);
                    razvrstajLetke(putanjaDoUnZipFajla);
                    prebrojeniLeci = prebrojLetke();
                    // oos.writeObject(new Poruka("razvrstani leci", razvrstaniLeci));
                    oos.writeObject(new Poruka("broj letaka", prebrojeniLeci));
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void razvrstajLetke(File putanjaDoFajlova) {

        Calendar datum = Calendar.getInstance();
        SimpleDateFormat formatDatum = new SimpleDateFormat("dd.MM.yyyy");
        String formatiranDatum = formatDatum.format(datum.getTime());
        File folder = new File(".\\src\\ServerArhiver\\fajlovi\\unZipovani\\" + brojac);
        File rasporedjeniLeci = new File(".\\src\\ServerArhiver\\fajlovi\\rasporedjeniLeci\\" + brojac);
        rasporedjeniLeci.mkdir();

        // get all the files from a directory
        File[] nizFajlovaUFolderu = folder.listFiles();

        for (File fajlUFolderu : nizFajlovaUFolderu) {
            if (fajlUFolderu.isDirectory()) {
                File[] nizFajlovaUPodFolderu = fajlUFolderu.listFiles();
                for (File fajlUPodFolderu : nizFajlovaUPodFolderu) {
                    int pos = fajlUPodFolderu.getName().lastIndexOf(".");
                    if (fajlUPodFolderu.isFile()) {
                        String pocetnoSlovo = fajlUPodFolderu.getName().substring(0, 1);
                        File folderSaPocetnimSlovom = new File(".\\src\\ServerArhiver\\fajlovi\\rasporedjeniLeci\\" + brojac + "\\" + pocetnoSlovo);
                        if (!folderSaPocetnimSlovom.exists()) {
                            folderSaPocetnimSlovom.mkdir();
                        }
                        fajlUPodFolderu.renameTo(new File(".\\src\\ServerArhiver\\fajlovi\\rasporedjeniLeci\\" + brojac + "\\" + pocetnoSlovo + "\\" + fajlUPodFolderu.getName().substring(0, pos) + formatiranDatum + ".txt"));

                    }
                }
            }
        }
    }

    public HashMap<String, Integer> prebrojLetke() {
        HashMap<String, Integer> prebrojaniLeci = new HashMap<>();
        File folderSaLecima = new File(".\\src\\ServerArhiver\\fajlovi\\rasporedjeniLeci\\" + brojac);

        File[] nizFolderaUFolderu = folderSaLecima.listFiles();

        for (File folderUFolderu : nizFolderaUFolderu) {
            if (folderUFolderu.isDirectory()) {
                File[] nizFajlova = folderUFolderu.listFiles();
                int brojac = 0;
                for (File fajlUFolderu : nizFajlova) {
                    if (fajlUFolderu.isFile()) {
                        String pocetnoSlovo = fajlUFolderu.getName().substring(0, 1);
                        if (prebrojaniLeci.get(pocetnoSlovo) == null) {
                            brojac = 1;
                            prebrojaniLeci.put(pocetnoSlovo, brojac);
                        } else {
                            prebrojaniLeci.put(pocetnoSlovo, ++brojac);
                        }
                    }
                }
            }
        }
        return prebrojaniLeci;
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

}
