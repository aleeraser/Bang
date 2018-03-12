package com.bang.actors;

import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPlayer extends Remote {

    //void setPlayerList(ArrayList<Player> pl); //assumiamo che la lista venga inizializzata alla creazione della stanza e passata ad ogni giocatore.
    boolean isMyTurn() throws RemoteException;

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

    int getLifes(int[] callerClock) throws RemoteException;

    int getPos(int[] callerClock) throws RemoteException;

    Clock getClock(int[] callerClock) throws RemoteException;

    ArrayList<IPlayer> getPlayers(int[] callerClock) throws RemoteException;

    ArrayList<IPlayer> getPlayers() throws RemoteException;

    void refreshPList() throws RemoteException;

    ArrayList<Card> getCards(int[] callerClock) throws RemoteException;

    ArrayList<Card> getHandCards() throws RemoteException;

    Card getHandCard(int i, int[] callerClock) throws RemoteException;

    int getDistance(int[] callerClock) throws RemoteException;

    void setDeckOrder(ArrayList<Integer> order, int[] callerClock) throws RemoteException;

    void decreaseLifes(int[] callerClock) throws RemoteException;

    void indiani(int[] callerClock) throws RemoteException;

    void removeTableCard(int index, int[] callerClock) throws RemoteException;

    void removeHandCard(int index, int[] callerClock) throws RemoteException;

    void removePlayer(int index, String ip, int[] callerClock) throws RemoteException;

    void increaseLifes(int[] callerClock) throws RemoteException;

    void initPlayerList(ArrayList<String> ips) throws RemoteException;

    void setCharacter(Character character) throws RemoteException;

    Character getCharacter() throws RemoteException;

    void setCharacterDeckOrder(ArrayList<Integer> indices, int[] callerClock) throws RemoteException;

    public void syncCharacterDeck(ArrayList<Integer> indices) throws RemoteException;

}