/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author raka
 */
public class ServerAnalizatorRezultata {
    
    private static final int PORT = 9000;
    private static int brojac = 1;
    
    
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server Analizator rezultata POKRENUT!");
        while(true){
            Socket soket = ss.accept();
            new ServerAnalizatorRezultataThread(soket, brojac);
            brojac++;
        }
    }
    
}
