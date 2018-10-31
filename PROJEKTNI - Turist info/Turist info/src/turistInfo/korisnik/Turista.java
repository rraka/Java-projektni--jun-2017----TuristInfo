/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.korisnik;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import sun.font.LayoutPathImpl;
import turistInfo.admin.Crkva;
import turistInfo.admin.IstorijskiSpomenik;
import turistInfo.admin.Muzej;
import turistInfo.admin.TuristickaAtrakcija;
import turistInfo.admin.ZabavniPark;

/**
 *
 * @author raka
 */
public class Turista extends Thread implements Serializable, Comparable<Turista> {

    public enum NacinKretanja {
        SAMO_U_JEDNOM_REDU,
        DIJAGONALNO,
        KROZ_CIJELU_MATRICU
    }

    private String ime = "ImeTuriste";
    private double novac;
    private NacinKretanja nacinKretanja;
    private File folderTuriste;
    private long vrijemeKretanja;
    private int brojPosjecenihMjesta;
    private static int br = 0;
    private transient SimulacijaFormaGUI simulacijaFormaGUI;
    private int redNaMapi;
    private int kolonaNaMapi;
    private Object[][] mapa;
    private int m;
    private int n;
    private boolean krajSimulacije = false;
    private transient JDialog dialog;
    private PomocnaKlasa pk = new PomocnaKlasa();
    private int brojacKorisnika;

    public Turista(SimulacijaFormaGUI simulacijaFormaGUI) {
        this.simulacijaFormaGUI = simulacijaFormaGUI;
        Random rand = new Random();
        this.ime = ime + (br + 1);
        brojacKorisnika = pk.sfg.gkf.getBrojacKorisnika();
        int broj = rand.nextInt(3);
        if (broj == 0) {
            nacinKretanja = NacinKretanja.SAMO_U_JEDNOM_REDU;
        } else if (broj == 1) {
            nacinKretanja = NacinKretanja.KROZ_CIJELU_MATRICU;
        } else {
            nacinKretanja = NacinKretanja.DIJAGONALNO;
        }
        this.novac = rand.nextInt(500) + 800;
        folderTuriste = new File(".\\src\\turistInfo\\korisnik\\fajlovi\\" + brojacKorisnika + "\\" + ime);
        if (!folderTuriste.exists()) {
            folderTuriste.mkdirs();
        }
        

        // folderTuriste.mkdir();
        this.vrijemeKretanja = rand.nextInt(400) + 1000;
        this.brojPosjecenihMjesta = 0;
        this.pk = simulacijaFormaGUI.pk;
        this.mapa = pk.getMapa();
        this.m = mapa.length;
        this.n = mapa[0].length;

        br++;
    }

    @Override
    public void run() {
        try {
            sleep(vrijemeKretanja);
            // for(int j = 0; j>m-1; j++)

            while (!krajSimulacije && this.novac > 0) {

                if (this.nacinKretanja == NacinKretanja.DIJAGONALNO) {
                    sleep(vrijemeKretanja);
                    if (mapa[redNaMapi][kolonaNaMapi] instanceof TuristickaAtrakcija) {
                        synchronized (this) {
                            pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je naisao na turisticku atrakciju: " + mapa[redNaMapi][kolonaNaMapi] + "na poziciji: [" + redNaMapi + "][" + kolonaNaMapi + "]");
                            if (mapa[redNaMapi][kolonaNaMapi] instanceof Muzej) {
                                Muzej muzej = (Muzej) mapa[redNaMapi][kolonaNaMapi];
                                int daLiSePlaca = muzej.daLiSePlaca();
                                if (daLiSePlaca == -1) {
                                    this.novac -= muzej.getCijenaUlaznice();
                                }
                                kopirajLetak(muzej.getLetak(), this.folderTuriste);
                            }
                            if (mapa[redNaMapi][kolonaNaMapi] instanceof IstorijskiSpomenik) {
                                IstorijskiSpomenik istorijskiSpomenik = (IstorijskiSpomenik) mapa[redNaMapi][kolonaNaMapi];
                                prikaziFotografiju(istorijskiSpomenik.getFotografija());
                            }
                            if (mapa[redNaMapi][kolonaNaMapi] instanceof ZabavniPark) {
                                ZabavniPark zabavniPark = (ZabavniPark) mapa[redNaMapi][kolonaNaMapi];
                                novac -= zabavniPark.getCijenaUlaznice();
                            }
                            if (mapa[redNaMapi][kolonaNaMapi] instanceof Crkva) {
                                Crkva crkva = (Crkva) mapa[redNaMapi][kolonaNaMapi];
                                Random rand = new Random();
                                double prilog = rand.nextInt(20) + 5;
                                crkva.setUkupanPrilog(prilog);
                            }
                            // mapa[redNaMapi][kolonaNaMapi] = this;
                            this.brojPosjecenihMjesta++;
                            if (redNaMapi < m - 1 && kolonaNaMapi < n - 1) {
                                redNaMapi++;
                                kolonaNaMapi++;
                            } else {
                                krajSimulacije = true;
                                break;
                            }

                        }
                    } else if (mapa[redNaMapi][kolonaNaMapi] == null) {
                        pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je na poziciji: [" + redNaMapi + "][" + kolonaNaMapi + "]");
                        mapa[redNaMapi][kolonaNaMapi] = this;
                        if (redNaMapi < m - 1 && kolonaNaMapi < n - 1) {
                            redNaMapi++;
                            kolonaNaMapi++;
                        } else {
                            krajSimulacije = true;
                            break;
                        }
                    } else if (mapa[redNaMapi][kolonaNaMapi] instanceof Turista) {
                        pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je na poziciji: [" + redNaMapi + "][" + kolonaNaMapi + "]");
                        if (redNaMapi < m - 1 && kolonaNaMapi < n - 1) {
                            redNaMapi++;
                            kolonaNaMapi++;
                        } else {
                            krajSimulacije = true;
                            break;
                        }
                    }
                }
                if (this.nacinKretanja == NacinKretanja.SAMO_U_JEDNOM_REDU) {
                    // sleep(vrijemeKretanja);
                    for (int j = kolonaNaMapi; j < n; j++) {
                        sleep(vrijemeKretanja);
                        if (mapa[redNaMapi][j] instanceof TuristickaAtrakcija) {
                            synchronized (this) {
                                pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je naisao na turisticku atrakciju: " + mapa[redNaMapi][j] + "na poziciji: [" + redNaMapi + "][" + j + "]");
                                if (mapa[redNaMapi][j] instanceof Muzej) {
                                    Muzej muzej = (Muzej) mapa[redNaMapi][j];
                                    int daLiSePlaca = muzej.daLiSePlaca();
                                    if (daLiSePlaca == -1) {
                                        this.novac -= muzej.getCijenaUlaznice();
                                    }
                                    kopirajLetak(muzej.getLetak(), this.folderTuriste);
                                }
                                if (mapa[redNaMapi][j] instanceof IstorijskiSpomenik) {
                                    IstorijskiSpomenik istorijskiSpomenik = (IstorijskiSpomenik) mapa[redNaMapi][j];
                                    prikaziFotografiju(istorijskiSpomenik.getFotografija());
                                }
                                if (mapa[redNaMapi][j] instanceof ZabavniPark) {
                                    ZabavniPark zabavniPark = (ZabavniPark) mapa[redNaMapi][j];
                                    novac -= zabavniPark.getCijenaUlaznice();
                                }
                                if (mapa[redNaMapi][j] instanceof Crkva) {
                                    Crkva crkva = (Crkva) mapa[redNaMapi][j];
                                    Random rand = new Random();
                                    double prilog = rand.nextInt(20) + 5;
                                    crkva.setUkupanPrilog(prilog);
                                }
                                // mapa[redNaMapi][kolonaNaMapi] = this;
                                this.brojPosjecenihMjesta++;
                                if (j < n - 1) {
                                    //  kolonaNaMapi++;
                                } else {
                                    krajSimulacije = true;
                                    break;
                                }
                            }
                        } else if (mapa[redNaMapi][j] == null) {

                            pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je na poziciji: [" + redNaMapi + "][" + j + "]");
                            mapa[redNaMapi][j] = this;
                            if (j < n - 1) {
                                //   kolonaNaMapi++;
                            } else {
                                krajSimulacije = true;
                            }
                        } else if (mapa[redNaMapi][j] instanceof Turista) {
                            pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je na poziciji: [" + redNaMapi + "][" + j + "]");
                            if (j < n - 1) {
                                /// kolonaNaMapi++;
                            } else {
                                krajSimulacije = true;
                                break;
                            }
                        }
                    }
                }

                if (this.nacinKretanja == NacinKretanja.KROZ_CIJELU_MATRICU) {
                    // sleep(vrijemeKretanja);
                    for (int i = redNaMapi; i < m; i++) {
                        for (int j = kolonaNaMapi; j < n; j++) {
                            sleep(vrijemeKretanja);
                            synchronized (this) {
                                if (mapa[i][j] instanceof TuristickaAtrakcija) {
                                    pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je naisao na turisticku atrakciju: " + mapa[i][j] + "na poziciji: [" + i + "][" + j + "]");
                                    if (mapa[i][j] instanceof Muzej) {
                                        Muzej muzej = (Muzej) mapa[i][j];
                                        int daLiSePlaca = muzej.daLiSePlaca();
                                        if (daLiSePlaca == -1) {
                                            this.novac -= muzej.getCijenaUlaznice();
                                        }
                                        kopirajLetak(muzej.getLetak(), this.folderTuriste);
                                    }
                                    if (mapa[i][j] instanceof IstorijskiSpomenik) {
                                        IstorijskiSpomenik istorijskiSpomenik = (IstorijskiSpomenik) mapa[i][j];
                                        prikaziFotografiju(istorijskiSpomenik.getFotografija());
                                    }
                                    if (mapa[i][j] instanceof ZabavniPark) {
                                        ZabavniPark zabavniPark = (ZabavniPark) mapa[i][j];
                                        novac -= zabavniPark.getCijenaUlaznice();
                                    }
                                    if (mapa[i][j] instanceof Crkva) {
                                        Crkva crkva = (Crkva) mapa[i][j];
                                        Random rand = new Random();
                                        double prilog = rand.nextInt(20) + 5;
                                        crkva.setUkupanPrilog(prilog);
                                    }
                                    this.brojPosjecenihMjesta++;
                                } else if (mapa[i][j] == null) {

                                    pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je na poziciji: [" + i + "][" + j + "]");
                                    mapa[i][j] = this;
                                } else if (mapa[i][j] instanceof Turista) {
                                    pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je na poziciji: [" + i + "][" + j + "]");
                                }
                                if (j == (n - 1)) {
                                    kolonaNaMapi = 0;
                                }
                            }
                        }
                    }
                    krajSimulacije = true;
                }
            }

            pk.upisiTekstNaFormu("\nTurista: " + this.ime + " je zavrsio takmicenje!");
            // simulacijaFormaGUI.upisiTekstNaFormu("\nTurista: " + this.ime + " je posjetio: " + brojPosjecenihMjesta + " turistickih atrakcija!");
        } catch (InterruptedException ex1) {
            //prekid simulacije gasenjem prozora
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Turista " + ime;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public double getNovac() {
        return novac;
    }

    public void setNovac(double novac) {
        this.novac = novac;
    }

    public File getFajl() {
        return folderTuriste;
    }

    public void setFajl(File fajl) {
        this.folderTuriste = fajl;
    }

    public long getVrijemeKretanja() {
        return vrijemeKretanja;
    }

    public void setVrijemeKretanja(long vrijemeKretanja) {
        this.vrijemeKretanja = vrijemeKretanja;
    }

    public int getBrojPosjecenihMjesta() {
        return brojPosjecenihMjesta;
    }

    public void setBrojPosjecenihMjesta(int brojPosjecenihMjesta) {
        this.brojPosjecenihMjesta = brojPosjecenihMjesta;
    }

    public NacinKretanja getNacinKretanja() {
        return nacinKretanja;
    }

    public void setNacinKretanja(NacinKretanja nacinKretanja) {
        this.nacinKretanja = nacinKretanja;
    }

    public File getFolderTuriste() {
        return folderTuriste;
    }

    public void setFolderTuriste(File folderTuriste) {
        this.folderTuriste = folderTuriste;
    }

    public int getRedNaMapi() {
        return redNaMapi;
    }

    public void setRedNaMapi(int redNaMapi) {
        this.redNaMapi = redNaMapi;
    }

    public int getKolonaNaMapi() {
        return kolonaNaMapi;
    }

    public void setKolonaNaMapi(int kolonaNaMapi) {
        this.kolonaNaMapi = kolonaNaMapi;
    }

    public static void kopirajLetak(String letak, File folderTuriste) {
        try {

            File izvor = new File(letak);
            String nazivLetka = izvor.getName();
            // File odrediste = new File(folderTuriste.getPath());
            if (!folderTuriste.exists()) {
                folderTuriste.mkdirs();
            }
            File fajl = new File(folderTuriste.getPath() + "\\" + nazivLetka);
            FileOutputStream fos = new FileOutputStream(fajl);
            FileInputStream fis = new FileInputStream(izvor);
            int broj = 0;
            while ((broj = fis.read()) != -1) {
                fos.write(broj);
            }
            fis.close();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void prikaziFotografiju(String fotografija) {

        dialog = new JDialog();
        //dialog.setUndecorated(true);
        dialog.setSize(450, 450);
        JLabel labelaZaSliku = new JLabel();
        labelaZaSliku.setSize(450, 450);
        ImageIcon icon = new ImageIcon(new ImageIcon(fotografija).getImage().getScaledInstance(labelaZaSliku.getWidth(), labelaZaSliku.getHeight(), Image.SCALE_DEFAULT));

        labelaZaSliku.setIcon(icon);
        dialog.add(labelaZaSliku);
        dialog.pack();
        dialog.setVisible(true);
        int vrijeme = 700;
        Timer tajmer = new Timer();
        tajmer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dispose();
            }
        }, vrijeme);

    }

    /*
    class  ZatvoriProzorSaFotografijom extends TimerTask {

        @Override
        public  void run() {
            
        }
    }*/

    @Override
    public int compareTo(Turista t) {
        return t.brojPosjecenihMjesta - this.brojPosjecenihMjesta;
    }

}
