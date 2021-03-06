package com.bang.actors;

//import java.rmi.registry.LocateRegistry;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.Semaphore;

import javax.lang.model.util.ElementScanner6;

import com.bang.gameui.LogBox;
import com.bang.utils.CardsUtils;
import com.bang.utils.UIUtils;

import org.apache.commons.io.filefilter.TrueFileFilter;

public class Player extends UnicastRemoteObject implements IPlayer {
    private int lives;
    private String ip;
    private ArrayList<Card> handCards = new ArrayList<Card>();
    private ArrayList<Card> tableCards = new ArrayList<Card>();
    private ArrayList<Card> marketCards = new ArrayList<Card>();
    private Deck deck;
    private int turnOwner; //turn holder index
    private CharacterDeck characterDeck;
    private Character character;
    private int shotDistance;
    private int view; //bonus sulla distanza a cui si vedono i nemici
    private int distance; //incremento della distanza a cui viene visto
    private ArrayList<IPlayer> players;
    private ArrayList<String> ips = new ArrayList<String>(); //valutare se tenere la lista di ip o di player
    private int pos; //index del player nella lista; sara' una lista uguale per tutti, quindi ognuno deve sapere la propria posizione
    private Boolean alreadyShot;
    private Boolean volcanic;
    private Boolean jail;
    private int dinamite;
    private Boolean isMarketTurn;
    private Boolean alreadyDrawMarket;
    private Boolean iPlayedMarket;
    private Boolean isIndiansTurn;
    private Boolean iPlayedIndians;
    private Boolean alreadyPlayedIndians;
    private Boolean duel;
    private Boolean duelTurn;
    private String bangTurn; //target, killer, or null;
    private int enemy;

    private int barrel;
    private Clock clock;
    private long startTimeoutTime;
    private long playerTimeout;
    private int turn;
    private Boolean mustUpdateGUI;
    private Boolean mustUpdateDuel;
    private Boolean mustUpdateBang;
    private LogBox logBox;
    private Boolean EXIT;
    private String gameStatus;

    public Semaphore cardsSemaphore;
    protected Semaphore redrawingSemaphore;

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
        this.barrel = 0;
        this.jail = false;
        this.dinamite = 0;
        this.isMarketTurn = false;
        this.alreadyDrawMarket = false;
        this.iPlayedMarket = false;
        this.isIndiansTurn = false;
        this.iPlayedIndians = false;
        this.alreadyPlayedIndians = false;
        this.duel = false;
        this.duelTurn = false;
        this.enemy = -1;
        this.bangTurn = "";
        this.gameStatus = "";

        this.deck = new Deck();

        this.turnOwner = 0;
        this.characterDeck = new CharacterDeck();

        this.playerTimeout = 100;
        this.startTimeoutTime = 0;
        this.turn = 0;
        this.mustUpdateGUI = false;
        this.mustUpdateDuel = false;

        this.cardsSemaphore = new Semaphore(1);
        this.redrawingSemaphore = new Semaphore(1);

        this.EXIT = false;
    }

    public boolean isMyTurn() {
        return (this.pos == this.turnOwner);
    }

    public int getTurn() {
        return this.turn;
    }

    public void setNextCardIndex(int deckIndex) {
        this.deck.setNextCardIndex(deckIndex);
    }

    private void syncNextCardIndex(int deckIndex) {
        for (IPlayer p : players) {
            if (p != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    p.setNextCardIndex(deckIndex);
                    redraw();

                } catch (RemoteException e) {
                    this.alertPlayerMissing(players.indexOf(p));
                }
            }
        }
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
                    this.draw(true);
                    System.out.println("Drew card. " + this.clock.toString());
                }
                this.playerTimeout = 3000;
                //System.out.println("Calling 'giveTurn' " + this.clock.toString());
                //this.giveTurn();
                this.turn++;
            } else if (turn > 1) {
                if (isIndiansTurn && !alreadyPlayedIndians) {
                    //log("gli indiani sono arrivati!");
                } else {
                    if (isIndiansTurn && alreadyPlayedIndians) {
                        System.out.println("Fine turno indiani");
                        isIndiansTurn = false;
                        alreadyPlayedIndians = false;
                        syncIndians();
                        redraw();
                        if (!this.iPlayedIndians) { //if you have to close indian turn but you did not played it you have to close it and than to play your turn normally
                            this.setTurn(deckIndex, characterIndex, turnHolder, callerClock);
                        } else
                            this.iPlayedIndians = false;
                    } else {
                        if (isMarketTurn && !alreadyDrawMarket) {
                            log("e' il mio turno di pescare dall' emporio");
                        } else {
                            if (isMarketTurn && alreadyDrawMarket) {
                                System.out.println("Fine turno emporio");
                                for (int i = 0; i < marketCards.size(); i++) {
                                    this.removeCard(i, this.clock.getVec(), true, "market");
                                }
                                isMarketTurn = false;
                                alreadyDrawMarket = false;
                                System.out.println("calling syncMarkeCards false");
                                syncMarketCards(false);
                                System.out.println("Fine sych emporio");
                                if (!this.iPlayedMarket) {
                                    this.setTurn(deckIndex, characterIndex, turnHolder, callerClock);
                                } else
                                    this.iPlayedMarket = false;
                                redraw();
                            } else {
                                // standard turn
                                //System.out.println("Standard turn, drawing two cards... " + this.clock.toString());
                                this.logOthers("E' il turno di " + this.getCharacter().getName());
                                //log("E' il mio turno!");
                                System.out.println("Standard turn, drew two cards. " + this.clock.toString());
                                while (this.dinamite > 0) {
                                    log("dinamite:");
                                    Card c = this.draw(false);
                                    this.deck.discard(this.deck.getNextCardIndex() - 1);
                                    this.syncDiscards();
                                    if (c.getSuit() == 1 && (int) c.getValue().charAt(0) >= 50
                                            && (int) c.getValue().charAt(0) <= 57) { // working with ASCII codes
                                        this.logOthers("BOOM: " + this.getCharacter().getName()
                                                + " ha fatto esplodere la dinamite!");
                                        //log("\t Mannaggia sono esploso");
                                        this.removeTableCard(this.findCard(tableCards, "dinamite"),
                                                this.clock.getVec());
                                        this.decreaselives(this.clock.getVec());
                                        this.decreaselives(this.clock.getVec());
                                        this.decreaselives(this.clock.getVec());
                                        if (this.getLives(this.clock.getVec()) <= 0) {
                                            this.giveTurn();
                                            return;
                                        }
                                    } else {
                                        this.logOthers(this.getCharacter().getName() + " non e' esploso");
                                        //log("\t few, non sono esploso");
                                        boolean found = false;
                                        while (!found) {
                                            int next = this.findNext(this.pos);
                                            int ind = this.findCard(tableCards, "dinamite");
                                            this.clock.clockIncreaseLocal();
                                            try {
                                                this.players.get(next).dynamite(this.tableCards.get(ind),
                                                        this.clock.getVec());
                                                this.removeTableCard(ind, this.clock.getVec(), false);
                                                found = true;
                                            } catch (RemoteException e) {
                                                this.alertPlayerMissing(next);
                                            }
                                        }
                                    }
                                }

                                if (this.jail) {
                                    log("Prigione:");
                                    Card c = this.draw(false);
                                    this.deck.discard(this.deck.getNextCardIndex() - 1);
                                    this.syncDiscards();
                                    this.removeTableCard(this.findCard(tableCards, "prigione"), this.clock.getVec());
                                    if (c.getSuit() == 2) {
                                        this.logOthers(
                                                this.getCharacter().getName() + " ha pescato cuori, ora e' libero");
                                        //log("\tE' cuori, sono scagionato!");
                                    } else {
                                        this.logOthers(this.getCharacter().getName()
                                                + " non ha pescato cuori, ha perso il turno");
                                        //log("\tNon cuori, salto!");
                                        this.giveTurn();
                                        return;
                                    }

                                }
                                this.draw(true);
                                this.draw(true);
                            }
                        }
                    }
                }
            } else {
                this.turn++;
            }

        }
        checkCrashes();
    }

    public void giveTurn() {
        Integer nextPlayer = findNext(this.pos);
        this.turnOwner = nextPlayer;
        this.alreadyShot = false;
        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos && players.get(i) != null && i != nextPlayer) {
                System.out.println("In 'giveTurn', i = " + i + " " + this.clock.toString());
                try {
                    this.clock.clockIncreaseLocal();
                    players.get(i).setTurn(deck.getNextCardIndex(), characterDeck.getNextCardIndex(), nextPlayer,
                            this.clock.getVec());
                    System.out.println("In 'giveTurn', called 'setTurn' " + this.clock.toString());
                } catch (RemoteException e) {
                    UIUtils.print("Error while passing token to player " + i + ".");
                    this.alertPlayerMissing(i);
                    ////e.printStackTrace();
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
            ////e.printStackTrace();
        }

    }

    private void syncIndians() {
        for (IPlayer p : this.players) {
            if (p != null && this.players.indexOf(p) != this.pos) {
                try {
                    this.clock.clockIncreaseLocal();
                    p.setIndians(this.clock.getVec());
                } catch (RemoteException e) {
                    this.alertPlayerMissing(this.players.indexOf(p));
                }
            }
        }
    }

    public int findCard(ArrayList<Card> cards, String name) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().matches(name))
                return i;
        }
        return -1;
    }

    public Card draw(Boolean addToHand) {
        Card drawn;
        // Integer drewCardIndex = this.deck.getNextCardIndex() == this.deck.getIndices().size() ? 0 : this.deck.getNextCardIndex();

        if (this.deck.getNextCardIndex() == this.deck.getIndices().size()) {
            ArrayList<Integer> handCards = new ArrayList<Integer>();

            for (IPlayer player : this.getPlayers()) {
                try {
                    for (Card c : player.getHandCards()) {
                        handCards.add(this.deck.getOrderedDeck()
                                .indexOf(CardsUtils.getMatchingCard(c, this.deck.getOrderedDeck())));
                    }

                    for (Card c : player.getCards(this.clock.getVec())) {
                        handCards.add(this.deck.getOrderedDeck()
                                .indexOf(CardsUtils.getMatchingCard(c, this.deck.getOrderedDeck())));
                    }
                } catch (Exception e) {
                    this.alertPlayerMissing(this.players.indexOf(player));
                }

            }
            this.deck.setPlayerCards(handCards);

            drawn = this.deck.draw();

            UIUtils.print("///////////////// New Deck");
            this.deck.printDeck();

            syncDeck(this.deck.getIndices());
            syncDiscards();
        } else {
            drawn = this.deck.draw();
        }

        // Card c = this.deck.getCard(drewCardIndex);

        if (addToHand)
            this.addHandCard(drawn);
        this.syncNextCardIndex(this.deck.getNextCardIndex());
        return drawn;
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
        this.deck.setCurrentSize(indices.size());

        this.startTimeoutTime = System.currentTimeMillis();
    }

    public void checkTimeout(long currentTime) {
        if (this.startTimeoutTime > 0) { //if not the game isn't still started
            if (currentTime - startTimeoutTime > this.playerTimeout) {
                if (this.turnOwner != this.pos) {
                    try {
                        // System.out.println("checking if the turn holder is alive");
                        if (this.players.get(this.turnOwner) != null) {
                            this.clock.clockIncreaseLocal();
                            players.get(this.turnOwner).getPos(this.clock.getVec());
                            this.startTimeoutTime = System.currentTimeMillis();
                            //this code is executed only if the player is still up
                        } else
                            throw new RemoteException();
                    } catch (RemoteException e) { //the turn Holder is crashed
                        this.alertPlayerMissing(this.turnOwner);
                        System.out.println("the Player " + this.turnOwner + " crashed.");
                        log("Il giocatore " + this.turnOwner + " e' crashato.");
                        int next = this.findNext(this.turnOwner);
                        if (next == this.pos) { //you are the next
                            System.out.println("I'm taking the turn");
                            this.setTurn(deck.getNextCardIndex(), characterDeck.getNextCardIndex(), next,
                                    this.clock.getVec());
                        } else {
                            this.turnOwner = next;
                            this.startTimeoutTime = System.currentTimeMillis();
                            checkCrashes();
                        }
                    }
                }
                if ((this.duel) || (this.bangTurn.matches("killer")) || (this.bangTurn.matches("target"))) {
                    try {
                        if (players.get(this.enemy) != null) {
                            this.clock.clockIncreaseLocal();
                            players.get(this.enemy).getPos(this.clock.getVec());
                            this.startTimeoutTime = System.currentTimeMillis();
                            //this code is executed only if the player is still up
                        } else {
                            throw (new RemoteException());
                        }
                    } catch (RemoteException e) {
                        if (players.get(this.enemy) != null) {
                            alertPlayerMissing(this.enemy);
                        }
                        if (this.duel)
                            this.duello(false, false, -1, this.clock.getVec());
                        else
                            this.setBangTurn("");
                    }
                }
            }
        }
    }

    private void shot(IPlayer target, int i) { // i is the target index
        try {
            this.clock.clockIncreaseLocal();
            this.bangTurn = "killer";
            target.bang(this.pos, this.clock.getVec());
            this.alreadyShot = true;
            this.enemy = this.players.indexOf(target);
            System.out.println("sto sparando");
            this.redrawBang(true);
            ;
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
            this.alertPlayerMissing(i);
            ////e.printStackTrace();
        }

    }

    public void bang(int enemy, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.enemy = enemy;
        for (int i = 0; i < barrel; i++) {
            Card c = this.draw(false);
            log("Ho un Barile in gioco, pesco...");
            if (c.getSuit() == 2) {
                System.out.println("ho pescato cuori, mi hanno mancato!");
                this.logOthers(this.getCharacter().getName() + " ha pescato cuori, non e' stato colpito");
                //log("\tho pescato cuori, mi hanno mancato!");
                try {
                    this.players.get(enemy).setBangTurn("");
                } catch (RemoteException e) {
                    this.alertPlayerMissing(enemy);
                }
                return;
            } else {
                this.logOthers("Il barile di " + this.getCharacter().getName() + " non ha avuto effetto");
                String suit;
                if (c.getSuit() == 1)
                    suit = "picche";
                else if (c.getSuit() == 3)
                    suit = "quadri";
                else
                    suit = "fiori";
                //log("\tho pescato " + suit + ", colpito.");
            }
            this.deck.discard(this.deck.getNextCardIndex() - 1);
            this.syncDiscards();
        }

        /*int i = this.findCard(handCards, "mancato");
        if (i != -1) {
            this.logOthers(this.getCharacter().getName() + " ha un Mancato!");
            System.out.println("Mancato!");
            //log("Ho usato il Mancato!");
            this.removeHandCard(i, this.clock.getVec());
            return;
        }
        
        this.decreaselives(this.clock.getVec());
        */
        System.out.println("mi hanno sparato");
        this.bangTurn = "target";
        this.redrawBang(true);
    }

    private void checkCrashes() {
        for (int i = 0; i < this.players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    players.get(i).getPos(this.clock.getVec());
                } catch (RemoteException e) {
                    this.alertPlayerMissing(i);
                }
            }
        }
        this.redraw();
    }

    private int findDistance(int i, int j, IPlayer target, int targetIndex) {
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

        int distance = Math.min(lDist, rDist);
        try {
            this.clock.clockIncreaseLocal();

            // distanza finale data dal minimo della distanza in una delle due direzioni + l'incremento di distanza del target
            distance += target.getDistance(this.clock.getVec());
        } catch (RemoteException e) {
            System.out.println("AAAAAAAAAAAAAA non c'e' " + targetIndex);
            this.alertPlayerMissing(targetIndex);
        }

        return distance;
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
            this.gameStatus = "winner";
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
                    System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
                    this.alertPlayerMissing(i);
                    ////e.printStackTrace();
                }
            }
        }

        this.redraw();
    }

    private void discardAll() {
        int i = this.handCards.size();
        while (i > 0) {
            this.removeHandCard(0, this.clock.getVec());
            i--;
        }

        i = this.tableCards.size();
        while (i > 0) {
            this.removeTableCard(0, this.clock.getVec());
            i--;
        }
        this.redraw();
    }

    public void decreaselives(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.lives--;
        System.out.println("mi hanno colpito, ho " + this.getLives(this.clock.getVec()) + " vite");
        log("Mi hanno colpito, vite rimaste: " + this.getLives(this.clock.getVec()) + ".");
        if (this.lives == 0) {
            System.out.println("SONO MORTO"); //todo chiamare routine per aggiornare le liste dei player
            this.gameStatus = "dead";
            this.discardAll();
            this.alertPlayerMissing(this.pos); //when a player dies it ack the others.
            this.logOthers(this.getCharacter().getName() + " e' MORTO!!!");
        }
    }

    public void increaselives(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        if (this.lives < 5) {
            this.lives++;
        }
    }

    public Boolean isMarketTurn() {
        return this.isMarketTurn;
    }

    public Boolean isIndiansTurn() {
        return this.isIndiansTurn;
    }

    public ArrayList<Card> getMarketCards(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.marketCards;
    }

    protected void findGun() { //searches for a gun between tableCards and removes it if present.
        for (int i = 0; i < this.tableCards.size(); i++) {
            String name = this.tableCards.get(i).getName();
            // System.out.println("--->" + name);
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

    public Boolean isInJail(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        return this.jail;
    }

    public void jail(Card jail, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        Card j = CardsUtils.getMatchingCard(jail.copyCard(), this.deck.getOrderedDeck());
        this.addTableCard(j);
        this.jail = true;
    }

    public void dynamite(Card dinamite, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        Card d = CardsUtils.getMatchingCard(dinamite.copyCard(), this.deck.getOrderedDeck());
        this.addTableCard(d);
        this.dinamite++;
    }

    public void addMarketCardToHand(int i) {
        this.addHandCard(CardsUtils.getMatchingCard(this.marketCards.get(i), this.deck.getOrderedDeck()));
        //log("Ho pescato " + this.marketCards.get(i).getName());
        this.logOthers(this.getCharacter().getName() + " ha pescato " + this.marketCards.get(i).getName());
        this.marketCards.remove(i);
        System.out.println("calling syncMarkeCards true");
        this.syncMarketCards();
        this.alreadyDrawMarket = true;
        this.giveTurn();
    }

    public void playCard(Card c) {
        this.playCard(c, -1, -1, false);
    }

    public void playCard(Card c, int targetIndex) {
        this.playCard(c, targetIndex, -1, false);
    }

    //TODO: ora come ora se usi una carta su un target crashato la carta viene comunque tolta dalla tua mano, valutare se cambiare questa cosa
    public void playCard(Card c, int targetIndex, int targetCardIndex, boolean fromTable) {
        System.out.println("in play card");
        String name = c.getName();
        if (c.getType().matches("target")) {
            IPlayer target = players.get(targetIndex);
            String targetName = "";
            try {
                targetName = target.getCharacter().getName();
            } catch (RemoteException e) {
                this.alertPlayerMissing(targetIndex);
            }
            if (target != null) {
                this.checkCrashes();
                Integer distance = findDistance(targetIndex, this.pos, target, targetIndex);
                if (name.matches("bang")) {
                    if (distance <= (this.view + this.shotDistance)) {
                        if (!alreadyShot || volcanic) {
                            this.logOthers(this.getCharacter().getName() + " ha sparato a " + targetName);
                            this.shot(target, targetIndex);
                            this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec());
                        } else {
                            System.out.println("Already shot");
                            log("Ho gia' sparato.");
                        }
                    } else {
                        System.out.println("Target out of range");
                        log("Il bersaglio e' troppo lontano.");
                    }

                } else if (name.matches("catbalou")) {
                    this.catBalou(targetIndex, targetCardIndex, fromTable);
                    this.logOthers(this.getCharacter().getName() + " ha distrutto una carta a " + targetName);
                    this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec());
                } else if (name.matches("panico")) {
                    if (distance <= 1) {
                        this.panico(targetIndex, targetCardIndex, fromTable);
                        this.logOthers(this.getCharacter().getName() + " ha rubato una carta a " + targetName);
                        this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec());
                    } else {
                        System.out.println("Target out of range");
                        log("Il bersaglio e' troppo lontano.");
                    }
                } else if (name.matches("prigione")) {
                    try {
                        this.clock.clockIncreaseLocal();
                        if (!this.players.get(targetIndex).isInJail(this.clock.getVec())) {
                            this.logOthers(this.getCharacter().getName() + " ha messo in prigione " + targetName);
                            this.clock.clockIncreaseLocal();
                            this.players.get(targetIndex).jail(c, this.clock.getVec());
                            this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec(), false);
                        } else
                            this.log(targetName + "e' gia' in prigione!!");
                    } catch (RemoteException e) {
                        System.out.println("AAAAAAAAAAAAAA non c'e' " + targetIndex);
                        this.alertPlayerMissing(targetIndex);
                    }
                } else if (name.matches("duello")) {
                    System.out.println("in duello in play card");
                    try {
                        this.duel = true;
                        this.clock.clockIncreaseLocal();
                        this.enemy = targetIndex;
                        this.logOthers(this.getCharacter().getName() + " ha sfidato a duello " + targetName);
                        this.players.get(targetIndex).duello(true, true, this.pos, this.clock.getVec());
                        this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec());
                        this.redrawDuel(true);
                        this.redraw();
                    } catch (RemoteException e) {
                        System.out.println("AAAAAAAAAAAAAA non c'e' " + targetIndex);
                        this.alertPlayerMissing(targetIndex);
                    }
                }
            }
            //attiva l'effetto sul target
        } else if (c.getType().matches("table"))

        {
            if (name.matches("mirino")) {
                this.view++;
                this.logOthers(this.getCharacter().getName() + " ha un mirino! Vedra' a distanza +1");
            } else if (name.matches("mustang")) {
                this.distance++;
                this.logOthers(this.getCharacter().getName() + " e' su un mustang, sara' piu' difficile sparargli");
            } else if (name.matches("carabine")) {
                findGun();
                this.shotDistance = 4;
                this.logOthers(this.getCharacter().getName() + " ha una carabina");

            } else if (name.matches("remington")) {
                findGun();
                this.shotDistance = 3;
                this.logOthers(this.getCharacter().getName() + " ha un remington");

            } else if (name.matches("schofield")) {
                findGun();
                this.shotDistance = 2;
                this.logOthers(this.getCharacter().getName() + " ha una schofield");

            } else if (name.matches("winchester")) {
                findGun();
                this.shotDistance = 5;
                this.logOthers(this.getCharacter().getName() + " ha un winchester");

            } else if (name.matches("volcanic")) {
                findGun();
                this.shotDistance = 1;
                this.volcanic = true;
                this.logOthers(this.getCharacter().getName() + " ha una volcanic");
            } else if (name.matches("barile")) {
                this.barrel++;
            } else if (name.matches("dinamite")) {
                this.dinamite++;
            }
            addTableCard(c);
            this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec(), false);
        } else { //single-usage cards
            if (name.matches("indiani")) {
                this.logOthers(this.getCharacter().getName() + " ha giocato indiani");
                syncIndians();
                this.isIndiansTurn = true;
                this.alreadyPlayedIndians = true;
                this.iPlayedIndians = true;
                this.giveTurn();
            } else if (name.matches("birra")) {
                if (this.lives < this.character.getLives()) {
                    this.increaselives(this.clock.getVec());
                    this.logOthers(this.getCharacter().getName() + " ha recuperato una vita bevendo una birra!");
                } else
                    return;
            } else if (name.matches("diligenza")) {
                this.draw(true);
                this.draw(true);
                this.logOthers(this.getCharacter().getName() + " ha pescato 2 carte");
            } else if (name.matches("wellsfargo")) {
                this.draw(true);
                this.draw(true);
                this.draw(true);
                this.logOthers(this.getCharacter().getName() + " ha pescato 3 carte");

            } else if (name.matches("gatling")) {
                this.logOthers(this.getCharacter().getName() + " ha usato un gatling!");
                for (int i = 0; i < this.players.size(); i++) {
                    if (i != this.pos && this.players.get(i) != null) {
                        try {
                            this.clock.clockIncreaseLocal();
                            this.players.get(i).decreaselives(this.clock.getVec());
                        } catch (RemoteException e) {
                            System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
                            this.alertPlayerMissing(i);
                        }
                    }
                }
            } else if (name.matches("saloon")) {
                this.logOthers(this.getCharacter().getName() + " ha usato un saloon!");
                for (int i = 0; i < this.players.size(); i++) {
                    if (this.players.get(i) != null) {
                        try {
                            this.clock.clockIncreaseLocal();
                            if (this.players.get(i).getLives(this.clock.getVec()) < this.players.get(i).getCharacter()
                                    .getLives()) {
                                this.clock.clockIncreaseLocal();
                                this.players.get(i).increaselives(this.clock.getVec());
                            }
                        } catch (RemoteException e) {
                            System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
                            this.alertPlayerMissing(i);
                        }
                    }
                }
            } else if (name.matches("emporio")) {
                this.logOthers(this.getCharacter().getName() + " ha giocato un emporio!");
                this.iPlayedMarket = true;
                int num = 0;
                for (IPlayer p : players) {
                    if (p != null)
                        num++;
                }
                for (int i = 0; i < num; i++) {
                    marketCards.add(this.draw(false));
                }
                System.out.println("calling syncMarkeCards true");
                this.syncMarketCards();
            }
            if (name.matches("mancato")) {
                this.log("non e' questo il momento di schivare un colpo!");
            } else {
                this.removeHandCard(this.handCards.indexOf(c), this.clock.getVec());
            }
        }
        redraw();

    }

    public void setIndians(int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.isIndiansTurn = !this.isIndiansTurn;

        if (this.alreadyPlayedIndians) {
            this.alreadyPlayedIndians = false;
        }
        System.out.println(" setting indians to " + this.isIndiansTurn());
    }

    public void syncMarketCards() {
        syncMarketCards(true);
    }

    public void syncMarketCards(Boolean value) {
        for (IPlayer p : players) {
            if (p != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    p.setMarketCards(this.marketCards, this.clock.getVec(), value);
                    redraw();

                } catch (RemoteException e) {
                    this.alertPlayerMissing(players.indexOf(p));
                }
            }
        }
    }

    public void setMarketCards(ArrayList<Card> mc, int[] callerClock, Boolean value) {
        this.clock.clockIncrease(callerClock);
        this.marketCards = mc;
        System.out.println("setting isMarket turn to " + value);
        this.isMarketTurn = value;
        if (value == false) {
            alreadyDrawMarket = false;
            System.out.println("Ricevuto fine emporio");
        }
        redrawSingle();
    }

    private void removeCard(int index, int[] callerClock, Boolean toDiscard, String removeFrom) {

        try {
            cardsSemaphore.acquire(1);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        this.clock.clockIncrease(callerClock);

        Card removedCard;
        if (removeFrom.matches("table"))
            removedCard = this.tableCards.remove(index);
        else if (removeFrom.matches("market"))
            removedCard = CardsUtils.getMatchingCard(this.marketCards.remove(index), this.deck.getOrderedDeck());
        else // standard hand discard
            removedCard = this.handCards.remove(index);
        UIUtils.print("called remove card on card: " + removedCard.getName());

        if (removedCard == null) {
            UIUtils.print("######### CARD NOT FOUND WHILE REMOVING IT");
            EXIT_NOW();
        }

        String name = removedCard.getName();

        if (toDiscard) {
            // Integer i = this.deck.getOrderedDeck().indexOf(removedCard);
            Integer i = this.deck.getIndices().indexOf(this.deck.getOrderedDeck().indexOf(removedCard));
            if (i == -1) {
                UIUtils.print("######### CARD NOT FOUND WHILE REMOVING IT: " + removedCard.getName());
                if (removeFrom.matches("hand")) {
                    UIUtils.print("Hand cards: ");
                    for (Card c : handCards) {
                        CardsUtils.printCard(c);
                    }
                }
                EXIT_NOW();
            }
            this.deck.discard(i);
            this.syncDiscards();

            UIUtils.print("Should discard " + removedCard.getName() + ", actually discarded "
                    + this.deck.getCard(i).getName());
        }

        cardsSemaphore.release(1);

        if (removeFrom.matches("table")) {
            UIUtils.print("Updating player status for " + name);

            if (name.matches("barile"))
                this.barrel--;
            else if (name.matches("volcanic"))
                this.volcanic = false;
            else if (name.matches("mirino"))
                this.view--;
            else if (name.matches("mustang"))
                this.distance--;
            else if (name.matches("prigione"))
                this.jail = false;
            else if (name.matches("dinamite"))
                this.dinamite--;
            else
                this.shotDistance = 1;
        }

        redraw();
    }

    public void removeTableCard(int index, int[] callerClock) {
        removeTableCard(index, callerClock, true);
    }

    public void removeTableCard(int index, int[] callerClock, Boolean toDiscard) {
        this.removeCard(index, callerClock, toDiscard, "table");
    }

    public void addTableCard(Card c) {
        try {
            cardsSemaphore.acquire(1);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        this.tableCards.add(c);
        cardsSemaphore.release(1);
    }

    public void removeHandCard(int index, int[] callerClock) {
        removeHandCard(index, callerClock, true);
    }

    public void removeHandCard(int index, int[] callerClock, Boolean toDiscard) {
        this.removeCard(index, callerClock, toDiscard, "hand");
    }

    public void addHandCard(Card c) {
        try {
            cardsSemaphore.acquire(1);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        this.handCards.add(c);
        cardsSemaphore.release(1);
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
                System.out.println("AAAAAAAAAAAAAA non c'e' " + pIndex);
                this.alertPlayerMissing(pIndex);
            }
        }
        redraw();
    }

    private void panico(int pIndex, int cIndex, Boolean fromTable) {
        IPlayer target = players.get(pIndex);
        if (target != null) {
            try {
                Card c;
                if (fromTable) {
                    this.clock.clockIncreaseLocal();
                    c = CardsUtils.getMatchingCard(target.getCards(this.clock.getVec()).get(cIndex).copyCard(),
                            this.deck.getOrderedDeck());
                    this.clock.clockIncreaseLocal();
                    target.removeTableCard(cIndex, this.clock.getVec(), false);
                } else {
                    this.clock.clockIncreaseLocal();
                    c = CardsUtils.getMatchingCard(target.getHandCard(cIndex, this.clock.getVec()).copyCard(),
                            this.deck.getOrderedDeck());
                    this.clock.clockIncreaseLocal();
                    target.removeHandCard(cIndex, this.clock.getVec(), false);
                }
                this.addHandCard(c);
            } catch (RemoteException e) {
                System.out.println("AAAAAAAAAAAAAA non c'e' " + pIndex);
                this.alertPlayerMissing(pIndex);
            }
        }
        redraw();
    }

    public void setBangTurn(String val) {
        this.bangTurn = val;
        if (val == null) {
            this.enemy = -1;
        }
        this.redrawSingle();
    }

    public String isInBangTurn() {
        return this.bangTurn;
    }

    public void indiani(Boolean playedBang) {
        if (playedBang) {
            int i = this.findCard(handCards, "bang");
            if (i != -1) {
                this.removeHandCard(i, this.clock.getVec());
            } else {
                this.clock.clockIncreaseLocal();
                this.decreaselives(this.clock.getVec());
            }
        } else {
            this.clock.clockIncreaseLocal();
            this.decreaselives(this.clock.getVec());
        }
        this.alreadyPlayedIndians = true;
        redraw();
        giveTurn();
    }

    public void duello(Boolean duel, Boolean turn, int enemy, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.duelTurn = turn;
        this.duel = duel;
        this.enemy = enemy;
        this.redrawDuel(true);
    }

    public Boolean isInDuel() {
        return this.duel;
    }

    public Boolean isDuelTurn() {
        return this.duelTurn;
    }

    public int getDuelEnemy() {
        return this.enemy;
    }

    public int getBangEnemy() {
        return this.enemy;
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
            //e.printStackTrace();
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
                    if (player != null)
                        System.out.println("Successfully added " + player.getIp());
                    this.players.add(player);
                } else
                    players.add(null);
            } catch (NotBoundException e) {
                //e.printStackTrace();
            } catch (RemoteException e) {
                players.add(null);
                ////e.printStackTrace();
                System.out.println("remote call to " + ips.get(i) + " failed. ");
            } catch (MalformedURLException e) {
                //e.printStackTrace();
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
                    System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
                    alertPlayerMissing(i);
                    ////e.printStackTrace();
                }
            }
        }
    }

    // Character cards deck
    public void setCharacterDeckOrder(ArrayList<Integer> indices, int[] callerClock) { //used by other processes to synchronize the char decks
        this.clock.clockIncrease(callerClock);
        this.characterDeck.setIndices(indices);

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
                    System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
                    alertPlayerMissing(i);
                    ////e.printStackTrace();
                }
            }
        }
    }

    private void checkConsistency() { //nota: non sono sicuro che questa funziona servira' mai o vada fatta cosi'
        while (true) {
            ArrayList<Clock> clockList = new ArrayList<Clock>();
            for (int i = 0; i < players.size(); i++) {
                if (this.players.get(i) != null) {
                    try {
                        clockList.add(this.players.get(i).getClock(this.clock.getVec()));
                    } catch (RemoteException e) {
                        System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
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

    public void redrawDuel(Boolean b) {
        this.mustUpdateDuel = b;
    }

    public void redrawBang(Boolean b) {
        this.mustUpdateBang = b;
    }

    public Boolean shouldUpdateDuel() {
        return this.mustUpdateDuel;
    }

    public Boolean shouldUpdateBang() {
        return this.mustUpdateBang;
    }

    public Boolean shouldUpdateGUI() {
        return this.mustUpdateGUI;
    }

    public void setDiscards(ArrayList<Integer> discards, int[] callerClock) {
        this.clock.clockIncrease(callerClock);
        this.deck.setDiscardPile(discards);
    }

    protected void syncDiscards() {
        for (int i = 0; i < players.size(); i++) {
            if (i != this.pos && players.get(i) != null) {
                try {
                    this.clock.clockIncreaseLocal();
                    players.get(i).setDiscards(this.deck.getDiscardPile(), this.clock.getVec());
                } catch (RemoteException e) {
                    System.out.println("AAAAAAAAAAAAAA non c'e' " + i);
                    alertPlayerMissing(i);
                    ////e.printStackTrace();
                }
            }
        }
    }

    public void setLogBox(LogBox logBox) {
        this.logBox = logBox;
    }

    public void log(String event) {
        if (logBox != null)
            logBox.addEvent(event);

        UIUtils.print(event);
    }

    public void logOthers(String event) {
        for (IPlayer p : players) {
            if (p != null && p != this) {
                try {
                    p.log(event);
                } catch (RemoteException e) {
                    alertPlayerMissing(players.indexOf(p));
                    //e.printStackTrace();
                }
            }
        }
    }

    public String getGameStatus() {
        return this.gameStatus;
    }

    @Override
    public Semaphore getCardsSemaphore() {
        return cardsSemaphore;
    }

    public Semaphore getDrawingSemaphore() {
        return redrawingSemaphore;
    }

    public int getTurnOwner() {
        return turnOwner;
    }

    private void EXIT_NOW() {
        UIUtils.print("Called EXIT_NOW();");
        this.EXIT = true;
    }

    public Boolean shouldExit() {
        return this.EXIT;
    }
}