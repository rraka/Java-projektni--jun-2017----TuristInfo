/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turistInfo.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author raka
 */
public class PomocnaKlasa {

    public static ArrayList<TuristickaAtrakcija> turistickeAtrakcije;

    public static void main(String[] args) {

        File muzej11 = new File("C:\\Users\\raka\\Desktop\\istorijskiSpomeniciFoto\\arhiv.jpg");
        File muzej22 = new File("C:\\Users\\raka\\Desktop\\istorijskiSpomeniciFoto\\sehitluci.jpg");
        File muzej33 = new File("C:\\Users\\raka\\Desktop\\istorijskiSpomeniciFoto\\hotelPalas.jpg");
        File muzej44 = new File("C:\\Users\\raka\\Desktop\\istorijskiSpomeniciFoto\\srednjovjekovniUtvrđeniGradZvecaj.jpg");
        File muzej55 = new File("C:\\Users\\raka\\Desktop\\istorijskiSpomeniciFoto\\srednjovjekovnoUtvrđenjeBocac.jpg");
        File muzej66 = new File("C:\\Users\\raka\\Desktop\\istorijskiSpomeniciFoto\\zgradaMitropolije.jpg");
        File muzej77 = new File("C:\\Users\\raka\\Desktop\\istorijskiSpomeniciFoto\\zgradaStareZeljeznickeStanice.jpg");

        File muzej88 = new File("C:\\Users\\raka\\Desktop\\muzejLeci\\muzejRS.txt");
        File muzej99 = new File("C:\\Users\\raka\\Desktop\\muzejLeci\\etnoMuzejLjubackeDoline.txt");
        File muzej00 = new File("C:\\Users\\raka\\Desktop\\muzejLeci\\muzejSavremeneUmjetnostiRS.txt");

        turistickeAtrakcije = new ArrayList<TuristickaAtrakcija>();

        Crkva crkva1 = new Crkva("Saborni hram Hrista spasitelja", "Banja Luka, Centar");
        Crkva crkva2 = new Crkva("Hram Svete Trojice", "Banja Luka, Centar");
        Crkva crkva3 = new Crkva("Hram rodj. Presvete Bogorodice", "Banja Luka, Starcevica");
        Crkva crkva4 = new Crkva("Hram Sv. apostola Petra i Pavla", "Banja Luka, Petricevac");
        Crkva crkva5 = new Crkva("Hram Sv. Luke", "Banja Luka, Cesma");

        IstorijskiSpomenik iSpomenik1 = new IstorijskiSpomenik("Opis  arhiv", muzej11.getAbsolutePath(), "Zgrada Arhiv-a RS", "Banja Luka");
        IstorijskiSpomenik iSpomenik2 = new IstorijskiSpomenik("Opis  hotelPalas", muzej33.getAbsolutePath(), "Hotel Palas", "Banja Luka");
        IstorijskiSpomenik iSpomenik3 = new IstorijskiSpomenik("Opis sehitluci", muzej22.getAbsolutePath(), "Spomenik na Sehitlucima", "Banja Luka");
        IstorijskiSpomenik iSpomenik4 = new IstorijskiSpomenik("Opis srednjovjekovniUtvrđeniGradZvecaj", muzej44.getAbsolutePath(), "Srednjovjekovni utvrđeni grad Zvecaj", "Banja Luka");
        IstorijskiSpomenik iSpomenik5 = new IstorijskiSpomenik("Opis srednjovjekovnoUtvrđenjeBocac", muzej55.getAbsolutePath(), "Srednjovjekovno utvrđenje Bocac", "Bocac");
        IstorijskiSpomenik iSpomenik6 = new IstorijskiSpomenik("Opis zgradaMitropolije", muzej66.getAbsolutePath(), "Zgrada pravoslavne Mitropolije", "Banja Luka");
        IstorijskiSpomenik iSpomenik7 = new IstorijskiSpomenik("Opis zgradaStareZeljeznickeStanice", muzej77.getAbsolutePath(), "Zgrada stare zeljeznicke stanice", "Banja Luka");

        Muzej muzej1 = new Muzej(muzej99.getAbsolutePath(), "Etno muzej Ljubacke doline", "Banja Luka, Ljubacevo", 5.00);
        Muzej muzej2 = new Muzej(muzej88.getAbsolutePath(), "Muzej Republike Srpske", "Banja Luka", 7.00);
        Muzej muzej3 = new Muzej(muzej00.getAbsolutePath(), "Muzej savremene umjetnosti Republike Srpske", "Banja Luka", 8.00);

        ZabavniPark zPark1 = new ZabavniPark("Fun park Mirnovec", "Biograd na Moru", 50.00);
        ZabavniPark zPark2 = new ZabavniPark("Ferrari land", "Salou, Spanija", 120.00);
        ZabavniPark zPark3 = new ZabavniPark("Dino park", "Trebinje", 11.00);

        turistickeAtrakcije.add(crkva1);
        turistickeAtrakcije.add(crkva2);
        turistickeAtrakcije.add(crkva3);
        turistickeAtrakcije.add(crkva4);
        turistickeAtrakcije.add(crkva5);
        turistickeAtrakcije.add(iSpomenik1);
        turistickeAtrakcije.add(iSpomenik2);
        turistickeAtrakcije.add(iSpomenik3);
        turistickeAtrakcije.add(iSpomenik4);
        turistickeAtrakcije.add(iSpomenik5);
        turistickeAtrakcije.add(iSpomenik6);
        turistickeAtrakcije.add(iSpomenik7);
        turistickeAtrakcije.add(iSpomenik1);
        turistickeAtrakcije.add(muzej1);
        turistickeAtrakcije.add(muzej2);
        turistickeAtrakcije.add(muzej3);
        turistickeAtrakcije.add(zPark1);
        turistickeAtrakcije.add(zPark2);
        turistickeAtrakcije.add(zPark3);

        serijalizacija(turistickeAtrakcije);

    }

    public static void serijalizacija(ArrayList<TuristickaAtrakcija> turistickeAtrakcije) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(".\\src\\turistInfo\\admin\\fajlovi\\listaTuristickihAtrakcija.ser")));
            oos.writeObject(turistickeAtrakcije);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList deSerijalizacija() {
        ArrayList<TuristickaAtrakcija> deSerijalizovanaLista = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(".\\src\\turistInfo\\admin\\fajlovi\\listaTuristickihAtrakcija.ser")));
            deSerijalizovanaLista = (ArrayList<TuristickaAtrakcija>) ois.readObject();
            ois.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return deSerijalizovanaLista;
    }

}
