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
    private int lives;
    private String ip;
    private ArrayList<Card> handCards = new ArrayList<Card>();
    private ArrayList<Card> tableCards = new ArrayList<Card>();
    private Deck deck;
    private int turnOwner; //turn holder index
    private CharacterDeck characterDeck;
    private Character character;
    private int shotDistance;
    private int view; //bonus sulla distanza a cui si vedono i nemici
    private int distance; //incremento della distanza a cui viene visto
    private ArrayList<IPlayer> players;
    private ArrayList<String> ips = new ArrayList<String>(); //valutare se tenere la lista di ip o di player
    private int pos; //index del player nella lista; sarà una lista uguale per tutti, quindi ognuno deve sapere la propria posizione
    private Boolean volcanic;
    private Boolean barrel;
    private Clock clock;
    private long startTimeoutTime;
    private long playerTimeout;
    private int turn;
    private Boolean mustUpdateGUI;

    public Player() throws RemoteException {
        /*this.CharacterPower = genCharacter();
        this.lives = CharacterPower.lives; */

        this.ip = findIp();
        System.setProperty("java.rmi.server.hostname", this.ip);
        this.shotDistance = 1;
        this.view = 0;
        this.distance = 0;
        this.pos = -1;
        this.volcanic = false;
        this.barrel = false;

        this.deck = new Deck();

        this.turnOwner = 0;
        this.characterDeck = new CharacterDeck();

        this.playerTimeout = 100;
        this.startTimeoutTime = 0;
        this.turn = 0;
        this.mustUpdateGUI = false;
    }

    public boolean isMyTurn() {
        return (this.pos == this.turnOwner);
    }

    public int getTurn() {
        return this.turn;
    }

    public void setTurn(int deckIndex, int characterIndex, int turnHolder, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        System.out.println("Starting 'setTurn'... " + this.clock.toString());
        this.turnOwner = turnHolder;
        this.deck.setNextCardIndex(deckIndex);
        this.characterDeck.setNextCardIndex(characterIndex);
        this.startTimeoutTime = System.currentTimeMillis();
        if (turnHolder == this.pos) {
            if (this.turn == 0) {
                System.out.println("Drawing character card... " + this.clock.toString());
                this.drawCharacter();
                System.out.println("Drew character card. " + this.clock.toString());
                for (int i = 0; i < this.character.getLives(); i++) {
                    System.out.println("Drawing card... " + this.clock.toString());
                    this.draw();
                    System.out.println("Drew card. " + this.clock.toString());
                }
                this.playerTimeout = 15000;
                System.out.println("Calling 'giveTurn' " + this.clock.toString());
                //this.giveTurn();
                this.turn++;
            } else if (turn > 1) {
                // standard turn
                System.out.println("Standard turn, drawing two cards... " + this.clock.toString());
                this.draw();
                this.draw();
                System.out.println("Standard turn, drew two cards. " + this.clock.toString());
            } else {
                this.turn++;
            }
        }
        checkCrashes();
    }

    public void giveTurn() {
        Integer nextPlayer = findNext(this.pos);
        this.turnOwner = nextPlayer;

        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos && players.get(i) != null && i != nextPlayer) {
                System.out.println("In 'giveTurn', i = " + i + " " + this.clock.toString());
                try {
                    players.get(i).setTurn(deck.getNextCardIndex(), characterDeck.getNextCardIndex(), nextPlayer,
                            this.clock.getVec());
                    System.out.println("In 'giveTurn', called 'setTurn' " + this.clock.toString());
                } catch (RemoteException e) {
                    UIUtils.print("Error while passing token to player " + i + ".");
                    this.alertPlayerMissing(i);
                    //e.printStackTrace();
                }
            }
        }

        try {
            players.get(nextPlayer).setTurn(deck.getNextCardIndex(), characterDeck.getNextCardIndex(), nextPlayer,
                    this.clock.getVec());
            System.out.println("In 'giveTurn', called 'setTurn' " + this.clock.toString());
        } catch (RemoteException e) {
            UIUtils.print("Error while passing token to player " + nextPlayer + ".");
            this.alertPlayerMissing(nextPlayer);
            //e.printStackTrace();
        }

    }

    public void draw() {
        this.handCards.add(deck.draw());
    }

    public void drawCharacter() {
        this.character = characterDeck.drawCharacter();
        this.lives = this.character.getLives();
    }

    public void refreshPList() {
        this.players = new ArrayList<IPlayer>();
    }

    public void setIpList(ArrayList<String> ips) { //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
        this.ips = ips;
        this.initPlayerList(ips);
        this.clock = new Clock(ips.size(), this.pos); //initialize also the vector clock
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public Deck getDeck() {
        return this.deck;
    }

    public CharacterDeck getCharacterDeck() {
        return this.characterDeck;
    }

    public int getPos(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.pos;
    }

    public ArrayList<IPlayer> getPlayers() {
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
        this.clock.clockIncrease(callerClock);//pesca char
        //pesca carte pari alle vite del char
        return handCards.get(i);
    }

    public ArrayList<Card> getHandCards() {
        return this.handCards;
    }

    public int getHandCardsSize() {
        return this.handCards.size();
    }

    public int getLives(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.lives;
    }

    public Clock getClock(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.clock;
    }

    public int getDistance(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.distance;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return this.character;
    }

    public void setDeckOrder(ArrayList<Integer> indices, int[] callerClock) { //used by other processes to synchronize the decks
        this.clock.clockIncrease(callerClock);
        this.deck.setIndices(indices);

        this.startTimeoutTime = System.currentTimeMillis();
    }

    public void checkTimeout(long currentTime) {
        if (this.startTimeoutTime > 0 && this.turnOwner != this.pos) { //if not the game isn't still started
            if (currentTime - startTimeoutTime > this.playerTimeout) {
                try {
                    System.out.println("checking if the turn holder is alive");
                    if (this.players.get(this.turnOwner) != null) {
                        players.get(this.turnOwner).getPos(this.clock.getVec());
                        this.startTimeoutTime = System.currentTimeMillis();
                        //this code is executed only if the player is still up
                    } else
                        throw new RemoteException();
                } catch (RemoteException e) { //the turn Holder is crashed
                    //this.removePlayer(this.turnOwner, ips.get(this.turnOwner), this.clock.getVec()); //remove the player locally
                    this.alertPlayerMissing(this.turnOwner);
                    System.out.println("the Player " + this.turnOwner + " crashed.");
                    int next = this.findNext(this.turnOwner);
                    if (next == this.pos) { //you are the next
                        System.out.println("I'm taking the turn");
                        this.turnOwner = this.pos;

                        if (this.turn == 0) {
                            // initial turn
                            this.drawCharacter();
                            for (int i = 0; i < this.character.getLives(); i++) {
                                this.draw();
                            }
                            this.playerTimeout = 15000;
                            //this.giveTurn();
                            this.turn++;
                        } else if (this.turn > 1) {
                            // standard turn
                            this.draw();
                            this.draw();
                        } else {
                            this.turn++;
                        }
                    } else {
                        this.turnOwner = next;
                        this.startTimeoutTime = System.currentTimeMillis();
                    }
                    checkCrashes();
                }
            }
        }
    }

    private void shot(IPlayer target, int i) { //i is the target index

        try { //TODO assicurarsi che quì il taglio sia coerente, se lo la distanza potrebbe essere sbagliata
            this.clock.clockIncreaseLocal();
            if (findDistance(i, this.pos) + target.getDistance(this.clock.getVec()) <= (this.view + this.shotDistance)) { //distanza finale data dal minimo della distanza in una delle due direzioni + l'incremento di distanza del target
                this.clock.clockIncreaseLocal();
                target.decreaselives(this.clock.getVec()); // TODO da migliorare, lui potrebbe avere un mancato
                //System.out.println(target.getLives());
            } else
                System.out.println("Target out of range");
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'è " + i);

            this.alertPlayerMissing(i);
            //e.printStackTrace();
        }

    }

    private void checkCrashes() {
        for (int i = 0; i < this.players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                try {
                    players.get(i).getPos(this.clock.getVec());
                } catch (RemoteException e) {
                    this.alertPlayerMissing(i);
                }
            }
        }
        this.redraw();
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

    private int findNext(int index) {
        int checked = 0;
        int i = (index + 1) % this.players.size();
        while (checked < this.players.size()) {
            if (this.players.get(i) != null) {
                return i;
            }
            checked++;
            i = (i + 1) % this.players.size();
        }
        return index; //this will not be ever reached;
    }

    //TODO forse prima di rimuvere un player bisognerebbe verificare di essere in un taglio consistente
    public void removePlayer(int index, String ip, int[] callerClock) {
        this.clock.clockIncrease(callerClock);

        this.players.set(index, null);
        this.ips.set(index, null);

        if (this.checkVictory()) {
            System.out.println("HO VINTOOOOOOOOOOOOOOOOOOO");
        }

    }

    private Boolean checkVictory() {
        for (int i = 0; i < this.players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                return false;
            }
        }
        return true;
    }

    public void alertPlayerMissing(int index) {
        this.clock.clockIncreaseLocal();
        this.removePlayer(index, ips.get(index), this.clock.getVec()); //first remove from own list.

        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    players.get(i).removePlayer(index, ips.get(index), this.clock.getVec());
                } catch (RemoteException e) {
                    System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                    this.alertPlayerMissing(i);
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
                target.increaselives(this.clock.getVec());
                //System.out.println(target.getLives());
            } catch (RemoteException e) {
                System.out.println("AAAAAAAAAAAAAA non c'è " + i);

                this.alertPlayerMissing(i);

                //e.printStackTrace();
            }
        }
    }

    public void decreaselives(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.lives--;
        System.out.println("mi hanno sparato, ho " + this.getLives(this.clock.getVec()) + " vite");
        if (this.lives <= 0) {
            System.out.println("SONO MORTO"); //todo chiamare routine per aggiornare le liste dei player
            this.alertPlayerMissing(this.pos); //when a player dies it ack the others.
        }
    }

    public void increaselives(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        if (this.lives < 5) {
            this.lives++;
        }
    }

    protected void findGun() { //searches for a gun between tableCards and removes it if present.
        for (int i = 0; i < this.tableCards.size(); i++) {
            String name = this.tableCards.get(i).getName();
            System.out.println("--->" + name);
            if (name.matches("volcanic") || name.matches("carabine") || name.matches("remington")
                    || name.matches("schofield") || name.matches("winchester")) {
                this.clock.clockIncreaseLocal();
                this.removeTableCard(i, this.clock.getVec());
                if (name.matches("volcanic"))
                    this.volcanic = false;
                break;
            }
        }
    }

    public void playCard(Card c) {
        this.playCard(c, -1, -1, false);
    }

    public void playCard(Card c, int targetIndex) {
        this.playCard(c, targetIndex, -1, false);
    }

    //TODO: ora come ora se usi una carta su un target crashato la carta viene comunque tolta dalla tua mano, valutare se cambiare questa cosa
    public void playCard(Card c, int targetIndex, int targetCardIndex, boolean fromTable) {
        this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec());
        String name = c.getName();
        if (c.getType().matches("target")) {
            IPlayer target = players.get(targetIndex);
            if (target != null) {
                this.checkCrashes();
                if (name.matches("bang"))
                    this.shot(target, targetIndex);
                else if (name.matches("catbalou"))
                    this.catBalou(targetIndex, targetCardIndex, fromTable);
                else if (name.matches("panico"))
                    this.panico(targetIndex, targetCardIndex, fromTable);
            }
            //attiva l'effetto sul target
        } else if (c.getType().matches("table")) {
            tableCards.add(c);
            if (name.matches("mirino"))
                this.view++;
            else if (name.matches("mustang")) {
                findGun();
                this.distance++;
            } else if (name.matches("carabine")) {
                findGun();
                this.shotDistance = 4;
            } else if (name.matches("remington")) {
                findGun();
                this.shotDistance = 3;
            } else if (name.matches("schofield")) {
                findGun();
                this.shotDistance = 2;
            } else if (name.matches("winchester")) {
                findGun();
                this.shotDistance = 5;
            } else if (name.matches("volcanic")) {
                findGun();
                this.shotDistance = 1;
                this.volcanic = true;
            } else { //single-usage cards
                if (name.matches("indiani")) {
                    for (int i = 0; i < players.size(); i++) {
                        if (i != this.pos && players.get(i) != null) {
                            try {
                                this.clock.clockIncreaseLocal();
                                players.get(i).indiani(this.clock.getVec());
                            } catch (RemoteException e) {
                                System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                                this.alertPlayerMissing(i);
                                //e.printStackTrace();
                            }
                        }
                    }
                }
            }
            //TODO valutare se gestire la volcanic;
            //attiva l'effetto su te stesso
        }
        redraw();
    }

    public void removeTableCard(int index, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.tableCards.remove(index);
        this.deck.discard(index);
    }

    public void removeHandCard(int index, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.handCards.remove(index);
        this.deck.discard(index);
    }

    private void catBalou(int pIndex, int cIndex, Boolean fromTable) {
        IPlayer target = players.get(pIndex);
        if (target != null) {
            try {
                this.clock.clockIncreaseLocal();
                if (fromTable) {
                    target.removeTableCard(cIndex, this.clock.getVec());
                } else {
                    target.removeHandCard(cIndex, this.clock.getVec());
                }
            } catch (RemoteException e) {
                System.out.println("AAAAAAAAAAAAAA non c'è " + pIndex);
                this.alertPlayerMissing(pIndex);
            }
        }
        redraw();
    }

    private void panico(int pIndex, int cIndex, Boolean fromTable) {
        IPlayer target = players.get(pIndex);
        if (target != null) {
            try {
                this.clock.clockIncreaseLocal();
                if (findDistance(pIndex, this.pos) + target.getDistance(this.clock.getVec()) <= (this.view + 1)) { //distanza finale data dal minimo della distanza in una delle due direzioni + l'incremento di distanza del target
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
                this.alertPlayerMissing(pIndex);
            }
        }
        redraw();
    }

    public void indiani(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        Boolean found = false;
        for (int i = 0; i < handCards.size(); i++) {
            if (handCards.get(i).getName().matches("bang")) {
                this.handCards.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            this.clock.clockIncreaseLocal();
            this.decreaselives(this.clock.getVec());
        }
        redraw();
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
        this.refreshPList();
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
                    alertPlayerMissing(i);
                    //e.printStackTrace();
                }
            }
        }
    }

    // Character cards deck
    public void setCharacterDeckOrder(ArrayList<Integer> indices, int[] callerClock) { //used by other processes to synchronize the char decks
        this.clock.clockIncrease(callerClock);
        this.characterDeck.setIndices(indices);

        ArrayList<Integer> indexis = this.characterDeck.getIndices();

        /*for (int i = 0; i < indexis.size(); i++) {
            System.out.println("CharDeck: " + indexis.get(i));
        }*/

        startTimeoutTime = System.currentTimeMillis();
    }

    public void syncCharacterDeck(ArrayList<Integer> indices) {
        this.characterDeck.setIndices(indices);

        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    players.get(i).setCharacterDeckOrder(this.characterDeck.getIndices(), this.clock.getVec());
                } catch (RemoteException e) {
                    System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                    alertPlayerMissing(i);
                    //e.printStackTrace();
                }
            }
        }
    }

    private void checkConsistency() { //nota: non sono sicuro che questa funziona servirà mai o vada fatta così
        while (true) {
            ArrayList<Clock> clockList = new ArrayList<Clock>();
            for (int i = 0; i < players.size(); i++) {
                if (this.players.get(i) != null) {
                    try {
                        clockList.add(this.players.get(i).getClock(this.clock.getVec()));
                    } catch (RemoteException e) {
                        System.out.println("AAAAAAAAAAAAAA non c'è " + i);
                        alertPlayerMissing(i);
                    }
                }
            }
            if (this.clock.cutConsistencyCheck(clockList)) {
                return;
            }
            ;
        }
    }

    public void redrawSingle() {
        this.mustUpdateGUI = true;
    }

    public void redraw() {
        this.mustUpdateGUI = true;
        for (int i = 0; i < players.size(); i++) {
            try {
                if (i != this.pos && this.players.get(i) != null) {
                    this.players.get(i).redrawSingle();
                }
            } catch (RemoteException e) {
                this.alertPlayerMissing(i);
            }
        }
    }

    public void redrawSingle(Boolean shouldRedraw) {
        this.mustUpdateGUI = shouldRedraw;
    }

    public Boolean shouldUpdateGUI() {
        return this.mustUpdateGUI;
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
                    server.getPlayers().get(i).getLives();
                }
            }
    
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    
    } */
}

//TODO ping al player in pos turno per vedere se è vivo, se è vivo aspetti, altrimenti ti dichiari in possesso del turno.
// tutticontrollano chi ha il turno, se quello con il turno muore controlli se tu sei quello subito dopo, nel caso ti dichiari avente il turno, altrimenti inizi a controllare quello subito dopo.
//quando uno passa il turno avverte tutti. e tutti resettano il tempo di riferimento
//quando uno ha ricevuto le carte iniziali cambia il temeout per un tempo plausibile al gioco.
