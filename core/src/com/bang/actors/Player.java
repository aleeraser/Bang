package com.bang.actors;

import java.util.Enumeration;

import com.badlogic.gdx.utils.Array;
import com.bang.utils.UIUtils;

import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.MalformedURLException;

public class Player extends UnicastRemoteObject implements IPlayer {
    private int lifes;
    private String ip;
    private ArrayList<Card> handCards = new ArrayList<Card>();
    private ArrayList<Card> tableCards = new ArrayList<Card>();
    private Deck deck;
    private Boolean turn;

    //private CharacterPower character;
    private int shotDistance;
    private int view; //bonus sulla distanza a cui si vedono i nemici
    private int distance; //incremento della distanza a cui viene visto
    private ArrayList<IPlayer> players ;
    private ArrayList<String> ips = new ArrayList<String>(); //valutare se tenere la lista di ip o di player
    private int pos; //index del player nella lista; sarà una lista uguale per tutti, quindi ognuno deve sapere la propria posizione
    private Boolean volcanic;
    private Boolean barrel;
    private Clock clock;

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

        this.deck = new Deck();
        this.turn = false;
    }

    public void giveTurn(int deckIndex, int[] callerClock){
        this.clock.clockIncrease(callerClock);
        this.turn = true;
        this.deck.setNextCardIndex(deckIndex);
    }

    public void refreshPList() {
        players = new ArrayList<IPlayer>();
    }

    public void setIpList(ArrayList<String> ips) { //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
        this.ips = ips;
        this.initPlayerList(ips);
        clock = new Clock(ips.size(), this.pos); //initialize also the vector clock
    }

    public void setIp(String ip){
        this.ip=ip;
    }

    public String getIp() {
        return this.ip;
    }

    public Deck getDeck(){
        return this.deck;
    }

    public int getPos(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.pos;
    }

    public ArrayList<IPlayer> getPlayers() {
        UIUtils.print(this.players.size() + "");
        return this.getPlayers(new int[this.players.size()]);
    }

    public ArrayList<IPlayer> getPlayers(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.players;
    }

    public ArrayList<Card> getCards(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.tableCards;
    }

    public Card getHandCard(int i, int[] callerClock) { //return a pointer to the card!
        this.clock.clockIncrease(callerClock);
        return handCards.get(i);
    }

    public int getLifes(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.lifes;
    }

    public Clock getClock( int[] callerClock){
        this.clock.clockIncrease(callerClock);
        return this.clock; 
    }

    public int getDistance(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.distance;
    }

    public void setDeckOrder(ArrayList<Integer> indices, int[] callerClock) { //used by other processes to synchronize the decks
        this.clock.clockIncrease(callerClock);
        this.deck.setIndices(indices);

        ArrayList<Integer> indexis = this.deck.getIndices();
        for (int i=0; i<indexis.size(); i++){
            System.out.println(indexis.get(i));
        }  
    }

    private void shot(IPlayer target, int i) { //i is the target index

        try { //TODO assicurarsi che quì il taglio sia coerente, se lo la distanza potrebbe essere sbagliata
            this.clock.clockIncreaseLocal();
            if (findDistance(i, this.pos) + target.getDistance(this.clock.getVec()) < (this.view + this.shotDistance)) { //distanza finale data dal minimo della distanza in una delle due direzioni + l'incremento di distanza del target
                this.clock.clockIncreaseLocal();
                target.decreaseLifes(this.clock.getVec()); // TODO da migliorare, lui potrebbe avere un mancato
                //System.out.println(target.getLifes());
            } else
                System.out.println("Target out of range");
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + i);

            this.allertPlayerMissing(i);
            //e.printStackTrace();
        }

    }

    private int findDistance(int i, int j) {
        int lDist = 0;
        int rDist = 0;
        if (i > j) {
            int tmp = i;
            i = j;
            j = tmp;
        }
        for (int ii = i; ii < j; ii++) {
            if (players.get(ii) != null) {
                lDist++;
            }
        }
        for (int jj = j; jj != i; jj = (jj + 1) % this.players.size()) {
            if (players.get(jj) != null) {
                rDist++;
            }
        }

        return Math.min(lDist, rDist);
    }

    //TODO forse prima di rimuvere un player bisognerebbe verificare di essere in un taglio consistente
    public void removePlayer(int index, String ip, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        if (this.ips.get(index).matches(ip)) {
            this.players.set(index, null);
            this.ips.set(index, null);
            /*if (index < this.pos) {
                this.pos--;
            }*/
        } else
            System.out.println("the ip does not match!");
    }

    private void allertPlayerMissing(int index) {
        this.clock.clockIncreaseLocal();
        this.removePlayer(index, ips.get(index), this.clock.getVec()); //first remove from own list.

        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    players.get(i).removePlayer(index, ips.get(index), this.clock.getVec());
                } catch (RemoteException e) {
                    System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                    this.allertPlayerMissing(i);
                    //e.printStackTrace();
                }
            }
        }
    }

    private void beer(IPlayer target, int i) {
        if (target != null) {
            try {
                System.out.println("nella beer");
                this.clock.clockIncreaseLocal();
                target.increaseLifes(this.clock.getVec());
                //System.out.println(target.getLifes());
            } catch (RemoteException e) {
                System.out.println("AAAAAAAAAAAAAA non c'è " + i);

                this.allertPlayerMissing(i);

                //e.printStackTrace();
            }
        }
    }

    public void decreaseLifes(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.lifes--;
        if (this.lifes <= 0) {
            System.out.println("SONO MORTO"); //todo chiamare routine per aggiornare le liste dei player
            this.allertPlayerMissing(this.pos); //when a player dies it ack the others.
        }
    }

    public void increaseLifes(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        if (this.lifes < 5) {
            this.lifes++;
        }
    }

    protected void findGun() { //searches for a gun between tableCards and removes it if present.
        for (int i = 0; i < this.tableCards.size(); i++) {
            String name = this.tableCards.get(i).getShortName();
            if (name.matches("Volcanic") || name.matches("Carabine") || name.matches("Remington")
                    || name.matches("Schofield") || name.matches("Winchester")) {
                this.clock.clockIncreaseLocal();
                this.removeTableCard(i, this.clock.getVec());
                if (name.matches("Volcanic"))
                    this.volcanic = false;
                break;
            }
        }

    }

    private void playCard(int index, int targetIndex) {
        IPlayer target = players.get(index);
        if (target != null) {
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
                } else if (name.matches("Carabine")) {
                    findGun();
                    this.shotDistance = 4;
                } else if (name.matches("Remington")) {
                    findGun();
                    this.shotDistance = 3;
                } else if (name.matches("Schofield")) {
                    findGun();
                    this.shotDistance = 2;
                } else if (name.matches("Winchester")) {
                    findGun();
                    this.shotDistance = 5;
                } else if (name.matches("Volcanic")) {
                    findGun();
                    this.shotDistance = 1;
                    this.volcanic = true;
                } else if (name.matches("Indiani")) {
                    for (int i = 0; i < players.size(); i++) {
                        if (i != this.pos && players.get(i) != null) {
                            try {
                                this.clock.clockIncreaseLocal();
                                players.get(i).indiani(this.clock.getVec());
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
    }

    private void playCard(int index, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.playCard(index, this.pos);
    }

    public void removeTableCard(int index, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.tableCards.remove(index);
    }

    public void removeHandCard(int index, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.handCards.remove(index);
    }

    private void catBalou(int cIndex, int pIndex, Boolean fromTable) {
        IPlayer target = players.get(pIndex);
        try {
            if (fromTable) {
                this.clock.clockIncreaseLocal();
                target.removeTableCard(cIndex, this.clock.getVec());
            } else {
                this.clock.clockIncreaseLocal();
                target.removeHandCard(cIndex, this.clock.getVec());
            }
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + pIndex);
            this.allertPlayerMissing(pIndex);
        }
    }

    private void panico(int cIndex, int pIndex, Boolean fromTable) {
        IPlayer target = players.get(pIndex);
        try {
            this.clock.clockIncreaseLocal();
            if (findDistance(pIndex, this.pos) + target.getDistance(this.clock.getVec()) < (this.view + 1)) { //distanza finale data dal minimo della distanza in una delle due direzioni + l'incremento di distanza del target
                Card c;
                if (fromTable) {
                    this.clock.clockIncreaseLocal();
                    c = target.getCards(this.clock.getVec()).get(cIndex).copyCard();
                    this.clock.clockIncreaseLocal();
                    target.removeTableCard(cIndex, this.clock.getVec());
                } else {
                    this.clock.clockIncreaseLocal();
                    c = target.getHandCard(cIndex, this.clock.getVec()).copyCard();
                    this.clock.clockIncreaseLocal();
                    target.removeHandCard(cIndex, this.clock.getVec());
                }
                this.handCards.add(c);
            }
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + pIndex);
            this.allertPlayerMissing(pIndex);
        }
    }

    public void indiani(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        Boolean found = false;
        for (int i = 0; i < handCards.size(); i++) {
            if (handCards.get(i).getShortName().matches("Bang")) {
                this.handCards.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            this.clock.clockIncreaseLocal();
            this.decreaseLifes(this.clock.getVec());
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
        this.players = new ArrayList<IPlayer>();
        for (int i = 0; i < ips.size(); i++) {
            try {
                if (this.ip.matches(ips.get(i))) {
                    this.pos = i;
                    this.players.add((IPlayer) this);
                } else if (this.ping(ips.get(i))) {
                    IPlayer player = (IPlayer) Naming.lookup("rmi://" + ips.get(i) + "/Player");
                    this.players.add(player);
                } else
                    players.add(null);
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                players.add(null);
                //e.printStackTrace();
                System.out.println("remote call to " + ips.get(i) + " failed. ");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            UIUtils.print(ips.get(i));
        }
        UIUtils.print("Pos: " + this.pos);
    }

    public void syncDeck(ArrayList<Integer> indices) {
        this.deck.setIndices(indices);

        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    players.get(i).setDeckOrder(this.deck.getIndices(), this.clock.getVec());
                } catch (RemoteException e) {
                    System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                    allertPlayerMissing(i);
                    //e.printStackTrace();
                }
            }
        }
    }

    private void checkConsistency(){ //nota: non sono sicuro che questa funziona servirà mai o vada fatta così
        while(true){
            ArrayList<Clock> clockList = new ArrayList<Clock>();
            for (int i=0; i<players.size(); i++){
                if (this.players.get(i)!= null){
                    try{
                        clockList.add(this.players.get(i).getClock(this.clock.getVec()));
                    }catch(RemoteException e){
                        System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                        allertPlayerMissing(i);
                    }
                }
            }
            if(this.clock.cutConsistencyCheck(clockList)){
                return ;
            };
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