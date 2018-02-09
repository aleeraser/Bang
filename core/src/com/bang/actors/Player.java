package com.bang.actors;

import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Player extends UnicastRemoteObject implements IPlayer {
    private int lifes;
    private String ip;
    //private ArrayList<Card> handCards = new ArrayList<Card>();
    //private ArrayList<Card> tableCards = new ArrayList<Card>();
    //private CharacterPower character;
    private int view; //ditanza a cui può sparare
    private int distance; //incremento della distanza a cui viene visto
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<String> ips = new ArrayList<String>(); //valutare se tenere la lista di ip o di player

    public Player() throws RemoteException {
        /*this.CharacterPower = genCharacter();
        this.lifes = CharacterPower.lifes; */
        this.ip = findIp();
        this.view = 0;
        this.distance = 0;
    }

    public void setPlayerList(ArrayList<Player> pl) { //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
        this.players = pl;
    }

    public void setIpList(ArrayList<String> ips) { //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
        this.ips = ips;
    }

    public String getIp() {
        return this.ip;
    }

    public int getLifes() {
        return this.lifes;
    }

    public int getDistancs() {
        return this.distance;
    }

    public void shot(String ip) {
        //todo implementare controllo distanza tra i e j 

        /* try {
            p.decreaseLifes();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } */

        try {
            IPlayer target = (IPlayer) Naming.lookup("rmi://" + ip + "/Player");
            target.decreaseLifes();
            System.out.println(target.getLifes());
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void decreaseLifes() {
        this.lifes--;
        if (this.lifes <= 0) {
            System.out.println("SONO MORTO"); //todo chiamare routine per aggiornare le liste dei player
        }
    }

    private static String findIp() {
        SocketException exception = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            int count = 0;
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    count++;
                    if (count == 2) {
                        System.out.println(i.getHostAddress());
                        return (i.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            exception = e;
        }

        return exception.toString();
    }

    private Boolean ping(String ip) {
        try {
            InetAddress inet = InetAddress.getByName(ip);
            System.out.println("Sending Ping Request to " + ip);
            if (inet.isReachable(5000)) {
                System.out.println(ip + " is reachable.");
                return true;
            } else {
                System.out.println(ip + "is NOT reachable.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false; //todo rilanciare l'eccezione a livello superiore
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", findIp());

        try {
            IPlayer server = new Player();
            Naming.rebind("//" + server.getIp() + "/Player", server);

            if (server.getIp().matches("192.168.1.4")) {
                server.shot("192.168.1.6");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}