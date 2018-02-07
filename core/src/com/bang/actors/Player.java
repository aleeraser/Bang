import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Player extends UnicastRemoteObject {
    private int lifes;
    private String ip;
    //private ArrayList<Card> handCards = new ArrayList<Card>();
    //private ArrayList<Card> tableCards = new ArrayList<Card>();
    //private CharacterPower character;
    private int view; //ditanza a cui può sparare
    private int distance; //incremento della distanza a cui viene visto
    private ArrayList<Player> players = new ArrayList<Player>();

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

    public String getIp() {
        return this.ip;
    }

    public int getLifes() {
        return this.lifes;
    }

    public int getDistancs() {
        return this.distance;
    }

    public void shot(Player p) {
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

        p.decreaseLifes();
    }

    public void decreaseLifes() {
        this.lifes--;
        if (this.lifes <= 0) {
            System.out.println("SONO MORTO"); //todo chiamare routine per aggiornare le liste dei player
        }

    }

    private String findIp() {
        SocketException exception = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    return (i.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            exception = e;
        }

        return exception.toString();
    }
}