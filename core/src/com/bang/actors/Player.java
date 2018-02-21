package com.bang.actors;

import java.util.Enumeration;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.MalformedURLException;


public class Player extends UnicastRemoteObject implements IPlayer {
    private int lifes;
    private String ip;
    private ArrayList<Card> handCards = new ArrayList<Card>();
    private ArrayList<Card> tableCards = new ArrayList<Card>();
    private ArrayList<Card> deck = new ArrayList<Card>();
    //private CharacterPower character;
    private int shotDistance;
    private int view; //bonus sulla distanza a cui si vedono i nemici
    private int distance; //incremento della distanza a cui viene visto
    private ArrayList<IPlayer> players = new ArrayList<IPlayer>();
    private ArrayList<String> ips = new ArrayList<String>(); //valutare se tenere la lista di ip o di player
    private int pos; //index del player nella lista; sarà una lista uguale per tutti, quindi ognuno deve sapere la propria posizione
    private Boolean volcanic;
    private Boolean barrel;

    public Player() throws RemoteException {
        /*this.CharacterPower = genCharacter();
        this.lifes = CharacterPower.lifes; */

        this.ip = findIp();
        System.setProperty("java.rmi.server.hostname", this.ip);
        this.shotDistance = 1;
        this.view = 0;
        this.distance = 0;
        this.pos = -1;
        this.volcanic = false;
        this.barrel = false;
    }

    
    public void setIpList(ArrayList<String> ips) { //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
        this.ips = ips;
    }
    
    public String getIp() {
        return this.ip;
    }

    public int getPos() {
        return this.pos;
    }

    public ArrayList<IPlayer> getPlayers() {
        return this.players;
    }

    public ArrayList<Card> getCards() {
        return this.tableCards;
    }

    public Card getHandCard(int i){ //return a pointer to the card!
        return handCards.get(i);
    }

    public int getLifes() {
        return this.lifes;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDeck(ArrayList<Card> deck){ //used by other processes to synchronize the decks
        this.deck = deck;
    }

    public void shot(IPlayer target, int i) { //i is the target index

        try { //TODO assicurarsi che quì il taglio sia coerente, se lo la distanza potrebbe essere sbagliata
            int dist = Math.abs(target.getPos() - this.pos); //distanza data dalla differenza degli indici 
            if (Math.min(this.players.size() - dist, dist) + target.getDistance() < (this.view + this.shotDistance)) { //distanza finale data dal minimo della distanza in una delle due direzioni + l'incremento di distanza del target
                target.decreaseLifes(); // TODO da migliorare, lui potrebbe avere un mancato
                System.out.println(target.getLifes());
            } else
                System.out.println("Target out of range");
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + i);

            this.allertPlayerMissing(i);
            //e.printStackTrace();
        }

    }
    //TODO forse prima di rimuvere un player bisognerebbe verificare di essere in un taglio consistente
    public void removePlayer( int index, String ip) {
        if(this.ips.get(index).matches(ip)){
            this.players.remove(index);
            this.ips.remove(index);
            if (index < this.pos) {
                this.pos--;
            }
        }
        else System.out.println("the ip does not match!");
    }

    private void allertPlayerMissing( int index) {
        this.removePlayer(index, ips.get(index)); //first remove from own list.

        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos) {
                try {
                    players.get(i).removePlayer(index, ips.get(index));
                } catch (RemoteException e) {
                    System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                    this.allertPlayerMissing(i);
                    //e.printStackTrace();
                }
            }
        }
    }

    public void beer(IPlayer target, int i) {

        try {
            System.out.println("nella shot");
            target.increaseLifes();
            System.out.println(target.getLifes());
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + i);

            this.allertPlayerMissing(i);

            //e.printStackTrace();
        }

    }

    public void decreaseLifes() {
        this.lifes--;
        if (this.lifes <= 0) {
            System.out.println("SONO MORTO"); //todo chiamare routine per aggiornare le liste dei player
            this.allertPlayerMissing(this.pos); //when a player dies it ack the others.
        }
    }

    public void increaseLifes() {
        if (this.lifes < 5) {
            this.lifes++;
        }
    }

    protected void findGun(){ //searches for a gun between tableCards and removes it if present.
        for (int i = 0; i < this.tableCards.size(); i++){
            String name = this.tableCards.get(i).getShortName();
            if (name.matches("Volcanic") || name.matches("Carabine") || name.matches("Remington") 
                    || name.matches("Schofield") || name.matches("Winchester") ){
                this.removeTableCard(i);
                if (name.matches("Volcanic"))
                    this.volcanic = false;
                break;
            }
        }
        
    }

    public void playCard(int index, int targetIndex) {
        IPlayer target = players.get(index);
        Card c = handCards.get(index);
        handCards.remove(index);
        String name = c.getShortName();
        if (c.hasTarget()) {
            if (name.matches("Bang"))
                this.shot(target, targetIndex);
    
            //attiva l'effetto sul target
        } else {
            tableCards.add(c);
            if (name.matches("Mirino"))
                this.view++;
            else if (name.matches("Mustang")) {
                findGun();
                this.distance++;
            }
            else if (name.matches("Carabine")){
                findGun();
                this.shotDistance = 4;
            }
            else if (name.matches("Remington")){
                findGun();
                this.shotDistance = 3;
            }
            else if (name.matches("Schofield")){
                findGun();
                this.shotDistance = 2;
            }
            else if (name.matches("Winchester")){
                findGun();
                this.shotDistance = 5;
            }
            else if (name.matches("Winchester")) {
                findGun();
                this.shotDistance = 1;
                this.volcanic = true;
            }
            else if (name.matches("Indiani")){
                for (int i = 0; i < players.size(); i++) {
                    if (i != this.pos) {
                        try {
                            players.get(i).indiani();
                        } catch (RemoteException e) {
                            System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                            this.allertPlayerMissing(i);
                            //e.printStackTrace();
                        }
                    }
                }
            }
            //TODO valutare se gestire la volcanic;
            //attiva l'effetto su te stesso
        }
    }
    
    public void playCard(int index) {
        this.playCard(index, this.pos);
    }

    public void removeTableCard(int index){
        this.tableCards.remove(index);
    }

    public void removeHandCard(int index){
        this.handCards.remove(index);
    }

    private void catBalou(int cIndex, int pIndex, Boolean fromTable){
        IPlayer target = players.get(pIndex);
        try{
            if(fromTable){
                target.removeTableCard(cIndex);
            }
            else{
                target.removeHandCard(cIndex);
            }
        }catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + pIndex);
            this.allertPlayerMissing(pIndex);
        }
    }
    
    private void panico(int cIndex, int pIndex, Boolean fromTable) {
        IPlayer target = players.get(pIndex);
        try {
            int dist = Math.abs(target.getPos() - this.pos); //distanza data dalla differenza degli indici 
            if (Math.min(this.players.size() - dist, dist) + target.getDistance() < (this.view + 1)) { //distanza finale data dal minimo della distanza in una delle due direzioni + l'incremento di distanza del target
                Card c;
                if (fromTable) {
                    c = target.getCards().get(cIndex).copyCard();
                    target.removeTableCard(cIndex);
                } else {
                    c = target.getHandCard(cIndex).copyCard();
                    target.removeHandCard(cIndex);
                }
                this.handCards.add(c);
            }
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + pIndex);
            this.allertPlayerMissing(pIndex);
        }
    }

    public void indiani(){
        Boolean found = false;
        for (int i=0; i < handCards.size(); i++){
            if (handCards.get(i).getShortName().matches("Bang")){
                this.handCards.remove(i);
                found = true;
                break;
            }
        }
        if (!found){
            this.decreaseLifes();
        }
    }

    private static String findIp() {
        SocketException exception = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String ip = i.getHostAddress();
                    if (ip.matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+") && !(ip.substring(0, 3).matches("127"))) {
                        System.out.println(ip);
                        return (ip);
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
                System.out.println(ip + " is NOT reachable.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            return false; //TODO rilanciare l'eccezione a livello superiore
        }
    }

    public void initPlayerList(ArrayList<String> ips) {
        int unreachable = 0;
        for (int i = 0; i < ips.size(); i++) {
            try {
                if (this.ip.matches(ips.get(i))) {
                    this.pos = i - unreachable;
                    this.players.add((IPlayer) this);
                } else if (this.ping(ips.get(i))) {
                    System.out.println("inizio naming");
                    IPlayer player = (IPlayer) Naming.lookup("rmi://" + ips.get(i) + "/Player");
                    System.out.println("finita naming");
                    this.players.add(player);
                } else
                    unreachable++;
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                unreachable++;
                //e.printStackTrace();
                System.out.println("remote call to " + ips.get(i) + " failed. ");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void syncDeck(){
        for (int i = 0; i<players.size(); i++){
            if(i != this.pos){
                try{
                    players.get(i).setDeck(this.deck);
                }
                catch (RemoteException e) {
                    System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                    this.players.remove(i);
                    if (i < this.pos) {
                        this.pos--;
                    }
                    //e.printStackTrace();
                }
            }
        }
    }

    // TODO : quando si capisce che uno non c'e' bisogna anche aggiornare il campo pos di tutti
    /* public static void main(String[] args) {
        // System.setProperty("java.rmi.server.hostname", findIp());

        try {
            LocateRegistry.createRegistry(1099);
            IPlayer server = new Player();
            Naming.rebind("//" + server.getIp() + "/Player", server);

            ArrayList<String> iplist = new ArrayList<String>();
            iplist.add("1.2.3.4");
            iplist.add("192.168.1.7");
            iplist.add("192.168.1.6");
            iplist.add("192.168.1.2");
            server.initPlayerList(iplist);
            System.out.println("---->" + server.getPos());
            System.out.println("---->" + server.getPlayers().size());
            
            for (int i = 0; i < server.getPlayers().size(); i++) {
                if (i != server.getPos()) {
                    server.shot((IPlayer) server.getPlayers().get(i), i);
                    server.getPlayers().get(i).getLifes();
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    } */
}