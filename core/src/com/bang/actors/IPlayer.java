package com.bang.actors;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.bang.gameui.LogBox;

public interface IPlayer extends Remote {

    //void setPlayerList(ArrayList<Player> pl); //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
    boolean isMyTurn() throws RemoteException;

    Boolean shouldUpdateGUI() throws RemoteException;

    void redraw() throws RemoteException;
    
    void redrawSingle() throws RemoteException;
    
    void redrawSingle(Boolean shouldRedraw) throws RemoteException;

    int getTurn() throws RemoteException;
    
    void giveTurn() throws RemoteException;

    void draw() throws RemoteException;

    void drawCharacter() throws RemoteException;

    void setTurn(int deckIndex, int characterIndex, int turnHolder, int[] callerClock) throws RemoteException;

    void setIpList(ArrayList<String> ips) throws RemoteException; //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.

    String getIp() throws RemoteException;

    Deck getDeck() throws RemoteException;

    CharacterDeck getCharacterDeck() throws RemoteException;

    void syncDeck(ArrayList<Integer> indices) throws RemoteException;

    void checkTimeout(long currentTime) throws RemoteException;

    int getLives(int[] callerClock) throws RemoteException;

    int getPos(int[] callerClock) throws RemoteException;

    Clock getClock(int[] callerClock) throws RemoteException;

    ArrayList<IPlayer> getPlayers(int[] callerClock) throws RemoteException;

    ArrayList<IPlayer> getPlayers() throws RemoteException;

    void refreshPList() throws RemoteException;

    ArrayList<Card> getCards(int[] callerClock) throws RemoteException;

    ArrayList<Card> getHandCards() throws RemoteException;

    int getHandCardsSize() throws RemoteException;

    Card getHandCard(int i, int[] callerClock) throws RemoteException;

    int getDistance(int[] callerClock) throws RemoteException;

    void setDeckOrder(ArrayList<Integer> order, int[] callerClock) throws RemoteException;

    void decreaselives(int[] callerClock) throws RemoteException;

    void indiani(int[] callerClock) throws RemoteException;

    void removeTableCard(int index, int[] callerClock) throws RemoteException;

    void removeTableCard(int index, int[] callerClock, Boolean toDiscard) throws RemoteException;

    void removeHandCard(int index, int[] callerClock) throws RemoteException;

    void removeHandCard(int index, int[] callerClock, Boolean toDiscard) throws RemoteException;

    void removePlayer(int index, String ip, int[] callerClock) throws RemoteException;

    void increaselives(int[] callerClock) throws RemoteException;

    void initPlayerList(ArrayList<String> ips) throws RemoteException;

    void setCharacter(Character character) throws RemoteException;

    Character getCharacter() throws RemoteException;

    void setCharacterDeckOrder(ArrayList<Integer> indices, int[] callerClock) throws RemoteException;

    void syncCharacterDeck(ArrayList<Integer> indices) throws RemoteException;

    void playCard(Card c) throws RemoteException;

    void playCard(Card c,int targetIndex) throws RemoteException; 
    
    void playCard(Card c,int targetIndex, int targetCardIndex, boolean fromTable ) throws RemoteException; 
    
    void alertPlayerMissing(int index) throws RemoteException;

    void setDiscards(ArrayList<Integer> discards, int[] callerClock) throws RemoteException;

    void bang(int[] callerClock) throws RemoteException;

    Boolean isInJail( int[] callerClock) throws RemoteException;

    void jail(Card jail, int[] callerClock) throws RemoteException;
    
    void setLogBox(LogBox logBox) throws RemoteException;
    
    void log (String event) throws RemoteException;

    void dynamite( Card dinamite, int[]callerClock) throws RemoteException;

    void setMarketCards( ArrayList<Card> mc, int[] callerClock, Boolean value) throws RemoteException;

    Boolean isMarketTurn() throws RemoteException;

    ArrayList<Card> getMarketCards( int [] callerClock) throws RemoteException;

    void addMarketCardToHand(int i) throws RemoteException;
    
    Semaphore getCardsSemaphore() throws RemoteException;

    void syncMarketCards() throws RemoteException;

    void syncMarketCards(Boolean value) throws RemoteException;
    
    void setIp(String ip) throws RemoteException;
    
    int getTurnOwner() throws RemoteException;

    void duello(Boolean duel, Boolean turn, int enemy, int[] callerClock) throws RemoteException;
     
    Boolean isInDuel() throws RemoteException;
     
    Boolean isDuelTurn() throws RemoteException;

    int getDuelEnemy() throws RemoteException;

    int findCard(ArrayList<Card> cards, String name) throws RemoteException;
    
    public void redrawDuel(Boolean b) throws RemoteException;
    
    public Boolean shouldUpdateDuel() throws RemoteException;
}